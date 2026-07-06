package net.zincstudios.scgextra.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.entity.projectile.RocketEntity;
import top.ribs.scguns.item.GunItem;

public class WreckerRocketEntity extends RocketEntity {

    private static final float EXPLOSION_RADIUS = 2.2F;

    public WreckerRocketEntity(EntityType<? extends ProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public WreckerRocketEntity(EntityType<? extends ProjectileEntity> entityType, Level level, LivingEntity shooter,
                               ItemStack weapon, GunItem item, Gun modifiedGun) {
        super(entityType, level, shooter, weapon, item, modifiedGun);
    }

    @Override
    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot) {
        if (entity instanceof Player player) {
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();

            boolean isBlockingMainHand = player.isBlocking() && mainHandItem.getItem() instanceof ShieldItem;
            boolean isBlockingOffHand = player.isBlocking() && offHandItem.getItem() instanceof ShieldItem;

            if (isBlockingMainHand || isBlockingOffHand) {
                ItemStack shield = isBlockingMainHand ? mainHandItem : offHandItem;
                InteractionHand hand = isBlockingMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

                player.getCooldowns().addCooldown(shield.getItem(), 100);
                player.stopUsingItem();
                player.level().broadcastEntityEvent(player, (byte) 30);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);

                shield.hurtAndBreak(15, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }
        createRocketExplosion(this, EXPLOSION_RADIUS, this.getDamage(), false);
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z) {
        createRocketExplosion(this, EXPLOSION_RADIUS, this.getDamage(), false);
    }

    @Override
    public void onExpired() {
        createRocketExplosion(this, EXPLOSION_RADIUS, this.getDamage(), false);
    }
}
