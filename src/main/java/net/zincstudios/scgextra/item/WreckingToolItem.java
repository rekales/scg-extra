package net.zincstudios.scgextra.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.zincstudios.scgextra.sounds.WreckersSounds;

import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public class WreckingToolItem extends PickaxeItem implements ArmorPiercing {

    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("8c8028fd-3f5d-4a65-9711-6b70c4c2c081");

    private static final int DAMAGE_INTERVAL_TICKS = 5;
    private static final int SOUND_INTERVAL_TICKS = 20;
    private static final double ENTITY_REACH = 2.5D;
    private static final double BLOCK_REACH = 3.0D;
    private static final float MAX_DRILL_HARDNESS = 50.0F;

    private static final WeakHashMap<Player, DrillState> DRILL_STATES = new WeakHashMap<>();

    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public WreckingToolItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, double reachModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                attackDamageModifier + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_MODIFIER_UUID, "Weapon modifier",
                reachModifier, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(livingEntity instanceof Player player)) {
            return;
        }
        int useTicks = this.getUseDuration(stack) - remainingUseDuration;
        if (useTicks % SOUND_INTERVAL_TICKS == 0) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    WreckersSounds.TOOL_USE.get(), SoundSource.PLAYERS, 0.5F, 0.95F + level.getRandom().nextFloat() * 0.1F);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 view = player.getViewVector(1.0F);
        LivingEntity entityTarget = this.findEntityTarget(level, player, eye, view);
        if (entityTarget != null) {
            this.clearDrillState(level, player);
            if (useTicks % DAMAGE_INTERVAL_TICKS == 0) {
                this.drillEntity(player, stack, entityTarget);
            }
            return;
        }
        this.drillBlock(level, player, stack, eye, view);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!level.isClientSide() && livingEntity instanceof Player player) {
            this.clearDrillState(level, player);
        }
    }

    private LivingEntity findEntityTarget(Level level, Player player, Vec3 eye, Vec3 view) {
        Vec3 end = eye.add(view.scale(ENTITY_REACH));
        AABB search = player.getBoundingBox().expandTowards(view.scale(ENTITY_REACH)).inflate(0.5D);
        LivingEntity nearest = null;
        double nearestDistSqr = ENTITY_REACH * ENTITY_REACH;
        for (Entity entity : level.getEntities(player, search, e -> e instanceof LivingEntity && e.isAlive() && e.isPickable())) {
            Optional<Vec3> clip = entity.getBoundingBox().inflate(0.3D).clip(eye, end);
            if (clip.isPresent()) {
                double distSqr = eye.distanceToSqr(clip.get());
                if (distSqr < nearestDistSqr) {
                    nearest = (LivingEntity) entity;
                    nearestDistSqr = distSqr;
                }
            }
        }
        return nearest;
    }

    private void drillEntity(Player player, ItemStack stack, LivingEntity target) {
        target.invulnerableTime = 0;
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (target.hurt(player.damageSources().playerAttack(player), damage)) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            player.swing(player.getUsedItemHand());
        }
    }

    private void drillBlock(Level level, Player player, ItemStack stack, Vec3 eye, Vec3 view) {
        BlockHitResult hit = level.clip(new ClipContext(eye, eye.add(view.scale(BLOCK_REACH)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            this.clearDrillState(level, player);
            return;
        }
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!this.canDrill(level, pos, state)) {
            this.clearDrillState(level, player);
            return;
        }

        DrillState drill = DRILL_STATES.get(player);
        if (drill == null || !drill.pos.equals(pos)) {
            this.clearDrillState(level, player);
            drill = new DrillState(pos);
            DRILL_STATES.put(player, drill);
        }

        float hardness = state.getDestroySpeed(level, pos);
        drill.progress += stack.getDestroySpeed(state) / (Math.max(hardness, 0.05F) * 30.0F);
        if (drill.progress >= 1.0F) {
            level.destroyBlockProgress(-player.getId(), pos, -1);
            DRILL_STATES.remove(player);
            if (level.removeBlock(pos, false)) {
                level.levelEvent(2001, pos, Block.getId(state));
                if (!state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state)) {
                    Block.dropResources(state, level, pos, null, player, stack);
                }
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
                player.swing(player.getUsedItemHand());
            }
        } else {
            level.destroyBlockProgress(-player.getId(), pos, Mth.clamp((int) (drill.progress * 10.0F), 0, 9));
        }
    }

    private boolean canDrill(Level level, BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return false;
        }
        float hardness = state.getDestroySpeed(level, pos);
        return hardness >= 0.0F && hardness <= MAX_DRILL_HARDNESS;
    }

    private void clearDrillState(Level level, Player player) {
        DrillState drill = DRILL_STATES.remove(player);
        if (drill != null) {
            level.destroyBlockProgress(-player.getId(), drill.pos, -1);
        }
    }

    private static class DrillState {

        private final BlockPos pos;
        private float progress;

        private DrillState(BlockPos pos) {
            this.pos = pos;
        }
    }
}
