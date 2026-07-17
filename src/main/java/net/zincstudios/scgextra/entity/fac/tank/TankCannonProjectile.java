package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModDamageTypes;
import top.ribs.scguns.init.ModParticleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// TODO: currently sloppy implementation, redo later
public class TankCannonProjectile extends ProjectileEntity {

    private static final Predicate<Entity> PROJECTILE_TARGETS = (input) -> input != null && input.isPickable() && !input.isSpectator();
    private static final EntityDataAccessor<Boolean> HIT =
            SynchedEntityData.defineId(TankCannonProjectile.class, EntityDataSerializers.BOOLEAN);

    private int immunityTicks;
    private int fuse = 20;

    public TankCannonProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public TankCannonProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn, LivingEntity shooter, Gun modifiedGun) {
        super(entityType, worldIn, shooter, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), modifiedGun);
        this.modifiedGravity = -0.015F;
        this.setItem(new ItemStack(top.ribs.scguns.init.ModItems.SHOTBALL.get()));
        this.immunityTicks = 5;
    }

    public TankCannonProjectile(Level worldIn, LivingEntity shooter, Gun modifiedGun) {
        this(FACEntities.TANK_CANNON_PROJECTILE.get(), worldIn, shooter, modifiedGun);
    }

    @Override
    public void tick() {
        if (this.immunityTicks > 0) {
            --this.immunityTicks;
        }

        this.updateHeading();
        this.onProjectileTick();
        if (!this.level().isClientSide()) {
            Vec3 startVec = this.position();
            Vec3 endVec = startVec.add(this.getDeltaMovement());
            this.handleCustomCollisions(startVec, endVec);
        }

        if (!this.isHit()) {
            double nextPosX = this.getX() + this.getDeltaMovement().x();
            double nextPosY = this.getY() + (this.onGround() ? 0 : this.getDeltaMovement().y());
            double nextPosZ = this.getZ() + this.getDeltaMovement().z();
            this.setPos(nextPosX, nextPosY, nextPosZ);
        }

        if (this.projectile.isGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0F, this.modifiedGravity, 0.0F));
        }

        if (this.isHit() && this.fuse-- <= 0) {
            this.onExpired();
            this.remove(RemovalReason.KILLED);
        }

        if (this.tickCount >= this.life) {
            if (this.isAlive()) {
                this.onExpired();
            }

            this.remove(RemovalReason.KILLED);
        }
    }

    private void handleCustomCollisions(Vec3 startVec, Vec3 endVec) {
        BlockHitResult blockResult = this.level().clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        List<EntityResult> entityResults = this.findShotballEntitiesOnPath(startVec, endVec);
        double blockDistance = Double.MAX_VALUE;
        double entityDistance = Double.MAX_VALUE;
        if (blockResult.getType() != HitResult.Type.MISS) {
            blockDistance = startVec.distanceToSqr(blockResult.getLocation());
        }

        ProjectileEntity.EntityResult closestEntity = null;
        if (!entityResults.isEmpty()) {
            for(ProjectileEntity.EntityResult entityResult : entityResults) {
                double dist = startVec.distanceToSqr(entityResult.getHitPos());
                if (dist < entityDistance) {
                    entityDistance = dist;
                    closestEntity = entityResult;
                }
            }
        }

        if (blockDistance < entityDistance && blockResult.getType() != HitResult.Type.MISS) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setHit(true);
        } else if (closestEntity != null) {
            this.setDeltaMovement(Vec3.ZERO);
            DamageSource source = ModDamageTypes.Sources.projectile(this.level().registryAccess(), this, this.shooter);
            if (this.getDamage() > 0.0F) {
                closestEntity.getEntity().hurt(source, this.getDamage()/2);
            }
        }

    }

    protected void onProjectileTick() {
        if (this.level().isClientSide) {
            for(int i = 5; i > 0; --i) {
                this.level().addParticle(ModParticleTypes.ROCKET_TRAIL.get(), true, this.getX() - this.getDeltaMovement().x() / (double)i, this.getY() - this.getDeltaMovement().y() / (double)i, this.getZ() - this.getDeltaMovement().z() / (double)i, 0.0F, 0.0F, 0.0F);
            }

            if (this.level().random.nextInt(2) == 0) {
                this.level().addParticle(ParticleTypes.SMOKE, true, this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F, 0.0F);
                this.level().addParticle(ParticleTypes.FLAME, true, this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F, 0.0F);
            }
        }

    }

    private List<ProjectileEntity.EntityResult> findShotballEntitiesOnPath(Vec3 startVec, Vec3 endVec) {
        List<ProjectileEntity.EntityResult> hitEntities = new ArrayList<>();

        for(Entity entity : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0F), PROJECTILE_TARGETS)) {
            if (!this.isShooterRelatedEntity(entity)) {
                ProjectileEntity.EntityResult result = this.getHitResult(entity, startVec, endVec);
                if (result != null) {
                    hitEntities.add(result);
                }
            }
        }

        return hitEntities;
    }

    private boolean isShooterRelatedEntity(Entity entity) {
        if (this.shooter == null) {
            return false;
        } else {
            if (this.immunityTicks > 0) {
                if (this.shooter.isPassenger() && this.shooter.getVehicle() == entity) {
                    return true;
                }

                if (entity.isPassenger() && entity.getVehicle() == this.shooter) {
                    return true;
                }

                Entity shooterVehicle = this.shooter.getVehicle();
                return shooterVehicle != null && (shooterVehicle.getVehicle() == entity || entity.getVehicle() == shooterVehicle);
            }

            return false;
        }
    }

    @Override
    protected void onHitEntity(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot) {
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z) {
    }

    @Override
    public void onExpired() {
        float exactDamage = this.getDamage();
        createRocketExplosion(this, 4.0F, exactDamage, false);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HIT, false);
    }

    public boolean isHit() {
        return this.entityData.get(HIT);
    }

    public void setHit(boolean hit) {
        this.entityData.set(HIT, hit);
    }

    @Override
    public ItemStack getItem() {
        return super.getItem();
    }
}
