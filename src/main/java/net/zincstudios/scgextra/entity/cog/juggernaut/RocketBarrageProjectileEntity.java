package net.zincstudios.scgextra.entity.cog.juggernaut;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.zincstudios.scgextra.entity.cog.COGEntities;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.entity.projectile.RocketEntity;
import top.ribs.scguns.init.ModParticleTypes;
import top.ribs.scguns.item.GunItem;

public class RocketBarrageProjectileEntity extends RocketEntity {

    private static final float EXPLOSION_RADIUS = 3.0f;

    public RocketBarrageProjectileEntity(EntityType<? extends ProjectileEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public RocketBarrageProjectileEntity(EntityType<? extends ProjectileEntity> entityType, Level worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun) {
        super(entityType, worldIn, shooter, weapon, item, modifiedGun);
    }

    public RocketBarrageProjectileEntity(Level worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun) {
        this(COGEntities.ROCKET_BARRAGE_PROJECTILE.get(), worldIn, shooter, weapon, item, modifiedGun);
    }

    public RocketBarrageProjectileEntity(Level worldIn, LivingEntity shooter, Gun gun) {
        this(COGEntities.ROCKET_BARRAGE_PROJECTILE.get(), worldIn, shooter, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), gun);
    }

    @Override
    protected void onProjectileTick() {
        if (this.level().isClientSide)
        {
            for (int i = 5; i > 0; i--)
            {
                this.level().addParticle(ModParticleTypes.ROCKET_TRAIL.get(), true, this.getX() - (this.getDeltaMovement().x() / i), this.getY() - (this.getDeltaMovement().y() / i), this.getZ() - (this.getDeltaMovement().z() / i), 0, 0, 0);
            }
            if (this.level().random.nextInt(2) == 0)
            {
                this.level().addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.level().addParticle(ParticleTypes.FLAME, true, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
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
                player.level().broadcastEntityEvent(player, (byte)30);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);

                shield.hurtAndBreak(15, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }
        float exactDamage = this.getDamage();
        createRocketExplosion(this, EXPLOSION_RADIUS, exactDamage, false);
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z) {
        float exactDamage = this.getDamage();
        createRocketExplosion(this, EXPLOSION_RADIUS, exactDamage, false);
    }

    @Override
    public void onExpired() {
        float exactDamage = this.getDamage();
        createRocketExplosion(this, EXPLOSION_RADIUS, exactDamage, false);
    }
}
