package net.zincstudios.scgextra.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.sounds.WreckersSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WreckingToolItem extends PickaxeItem implements ArmorPiercing, GeoItem, HoldAttack {

    private static final RawAnimation IDLE = RawAnimation.begin().thenPlayAndHold("idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    private int soundTick = -2;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public WreckingToolItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, double reachModifier, Properties properties) {
        super(tier, attackDamageModifier, attackSpeedModifier, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                attackDamageModifier + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                attackSpeedModifier, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4,
                state -> {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player == null) return state.setAndContinue(IDLE);
                    if (HoldAttackHandler.isHeldAttack(mc.player)) return state.setAndContinue(ATTACK);
                    return state.setAndContinue(IDLE);
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SimpleCustomRenderer(new GeoItemRenderer<>(
                new DefaultedItemGeoModel<>(SCGExtra.asResource("wrecking_tool"))
        )));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity instanceof Player player){
            if(!HoldAttackHandler.isHeldAttack(player)){
                this.soundTick = -2;
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
    
    @Override
    public void onPlayerAttackTick(ItemStack stack, Level level, Player player) {
        if(this.soundTick==-1){
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), WreckersSounds.TOOL_USE.get(), SoundSource.NEUTRAL, 0.5F, 1.0F);
        }else if(this.soundTick==40){
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), WreckersSounds.TOOL_USE.get(), SoundSource.NEUTRAL, 0.5F, 1.0F);
            this.soundTick = 0;
        }
        this.soundTick++;
        if (level.isClientSide) return;
        if (level.getGameTime()%5 != 1) return;

        var reach = player.getAttributeValue(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
        reach *= 1.2F;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        Vec3 end = eyePos.add(lookVec.scale(reach));
        AABB search = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(0.5D);
        LivingEntity nearest = null;
        double nearestDistSqr = reach * reach;
        for (Entity entity : level.getEntities(player, search, e -> e instanceof LivingEntity && e.isAlive() && e.isPickable())) {
            Optional<Vec3> clip = entity.getBoundingBox().inflate(0.3D).clip(eyePos, end);
            if (clip.isPresent()) {
                double distSqr = eyePos.distanceToSqr(clip.get());
                if (distSqr < nearestDistSqr) {
                    nearest = (LivingEntity) entity;
                    nearestDistSqr = distSqr;
                }
            }
        }

        if (nearest != null) {
            player.attack(nearest);
            nearest.invulnerableTime /= 5;
            nearest.hurtTime /= 3;
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
