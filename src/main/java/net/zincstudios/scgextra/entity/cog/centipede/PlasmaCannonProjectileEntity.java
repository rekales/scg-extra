package net.zincstudios.scgextra.entity.cog.centipede;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.cog.COGEntities;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModDamageTypes;
import top.ribs.scguns.init.ModParticleTypes;
import top.ribs.scguns.item.GunItem;

import java.util.List;

// Not bothering to extend the PlasmaProjectileEntity because it's all private
@SuppressWarnings("SameParameterValue")
public class PlasmaCannonProjectileEntity extends ProjectileEntity {

    public static final float EXPLOSION_RADIUS = 5;

    public PlasmaCannonProjectileEntity(EntityType<? extends Entity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public PlasmaCannonProjectileEntity(EntityType<? extends Entity> entityType, Level worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun) {
        super(entityType, worldIn, shooter, weapon, item, modifiedGun);
    }

    public PlasmaCannonProjectileEntity(Level worldIn, LivingEntity shooter, ItemStack weapon, GunItem item, Gun modifiedGun) {
        this(COGEntities.PLASMA_CANNON_PROJECTILE.get(), worldIn, shooter, weapon, item, modifiedGun);
    }

    public PlasmaCannonProjectileEntity(Level worldIn, LivingEntity shooter, Gun gun) {
        this(COGEntities.PLASMA_CANNON_PROJECTILE.get(), worldIn, shooter, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), gun);

    }

    @Override
    public float getDamage() {
        return 15;
    }

    @Override
    protected void onProjectileTick() {
        if (this.level().isClientSide && this.tickCount > 1 && this.tickCount < this.life) {
            //noinspection ConstantValue,PointlessArithmeticExpression
            if (this.tickCount % 1 == 0) {
                double offsetX = (this.random.nextDouble() - (double)0.5F) * (double)0.5F;
                double offsetY = (this.random.nextDouble() - (double)0.5F) * (double)0.5F;
                double offsetZ = (this.random.nextDouble() - (double)0.5F) * (double)0.5F;
                this.level().addParticle(
                        ModParticleTypes.GREEN_FLAME.get(),
                        true,
                        this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                        0.0F, 0.0F, 0.0F
                );
            }

            if (this.tickCount % 2 == 0) {
                this.level().addParticle(
                        ModParticleTypes.PLASMA_RING.get(),
                        true,
                        this.getX(), this.getY(), this.getZ(),
                        0.0F, 0.0F, 0.0F
                );
            }
        }
    }

    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z) {
        Vec3 hitPos = new Vec3(x, y, z);
        this.applyDamage(hitPos, this.getDamage(), EXPLOSION_RADIUS);
        this.spawnExplosionParticles(hitPos, EXPLOSION_RADIUS);
    }

    @Override
    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot) {
        this.applyDamage(hitVec, this.getDamage(), EXPLOSION_RADIUS);
        this.spawnExplosionParticles(hitVec, EXPLOSION_RADIUS);
    }

    protected void applyDamage(Vec3 center, float damage, float radius) {
        if (!this.level().isClientSide()) {
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(radius),
                    entity -> entity instanceof LivingEntity && entity.distanceTo(this) <= radius
            );
            DamageSource splashSource = ModDamageTypes.Sources.projectile(this.level().registryAccess(), this, this.getShooter());

            for(LivingEntity target : nearbyEntities) {
                target.hurt(splashSource, damage);
            }
        }
    }

    protected void spawnExplosionParticles(Vec3 position, float radius) {
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(
                    ModParticleTypes.PLASMA_EXPLOSION.get(),
                    position.x, position.y, position.z,
                    (int) (radius * 4),
                    radius/2, radius/2, radius/2,
                    0.1
            );

            level.sendParticles(
                    ModParticleTypes.GREEN_FLAME.get(),
                    this.position().x,
                    this.position().y + 0.5,
                    this.position().z,
                    (int)(radius*8),
                    radius*0.75, radius*0.75, radius*0.75,
                    0.1
            );

            level.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    this.position().x,
                    this.position().y + 0.5,
                    this.position().z,
                    (int)(radius*6),
                    radius*0.75, radius*0.75, radius*0.75,
                    0.1
            );
        }

//        if (!this.level().isClientSide) {
//            ServerLevel serverLevel = (ServerLevel)this.level();
//            serverLevel.sendParticles(
//                    ModParticleTypes.PLASMA_EXPLOSION.get(),
//                    position.x, position.y, position.z,
//                    1,
//                    0.0F, 0.0F, 0.0F,
//                    0.1
//            );

//            for(int i = 0; i < 12; ++i) {
//                double angle = (double)i / (double)12.0F * (double)2.0F * Math.PI;
//                double radius = 0.3 + this.random.nextDouble() * 0.8;
//                double offsetX = Math.cos(angle) * radius;
//                double offsetZ = Math.sin(angle) * radius;
//                double offsetY = (this.random.nextDouble() - (double)0.5F) * 0.3;
//                double speedX = offsetX * 0.08;
//                double speedY = (this.random.nextDouble() - 0.3) * 0.15;
//                double speedZ = offsetZ * 0.08;
//                serverLevel.sendParticles(
//                        ModParticleTypes.GREEN_FLAME.get(),
//                        position.x + offsetX, position.y + offsetY, position.z + offsetZ,
//                        1,
//                        speedX, speedY, speedZ,
//                        0.05
//                );
//            }
//
//            for(int i = 0; i < 6; ++i) {
//                double offsetX = (this.random.nextDouble() - (double)0.5F) * (double)1.5F;
//                double offsetY = (this.random.nextDouble() - (double)0.5F) * (double)0.5F;
//                double offsetZ = (this.random.nextDouble() - (double)0.5F) * (double)1.5F;
//                double speedX = (this.random.nextDouble() - (double)0.5F) * 0.2;
//                double speedY = (this.random.nextDouble() - (double)0.5F) * 0.2;
//                double speedZ = (this.random.nextDouble() - (double)0.5F) * 0.2;
//                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, position.x + offsetX, position.y + offsetY, position.z + offsetZ, 1, speedX, speedY, speedZ, 0.1);
//            }
//        }
    }
}
