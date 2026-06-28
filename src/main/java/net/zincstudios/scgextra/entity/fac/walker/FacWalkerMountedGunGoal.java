package net.zincstudios.scgextra.entity.fac.walker;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import top.ribs.scguns.init.ModSounds;

import java.util.EnumSet;

public class FacWalkerMountedGunGoal extends Goal {

    private static final float MAX_TURN_PER_TICK = 5.0F;
    private static final float FIRE_ANGLE_TOLERANCE = 3.0F;
    private static final int AIM_LOCK_REQUIRED_TICKS = 4;
    private static final int RANGED_POSE_BUFFER_TICKS = 6;

    private final FacWalkerEntity entity;
    private final int fireInterval;
    private final float range;
    private int cooldown = 0;
    private int aimLockTicks = 0;
    private boolean leftGunShot = true;

    public FacWalkerMountedGunGoal(FacWalkerEntity entity, int fireInterval, float range) {
        this.entity = entity;
        this.fireInterval = fireInterval;
        this.range = range;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null && target.isAlive() && !this.entity.isActionLocked();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.cooldown = 1;
        this.aimLockTicks = 0;
        this.leftGunShot = this.entity.getRandom().nextBoolean();
    }

    @Override
    public void stop() {
        this.cooldown = 0;
        this.aimLockTicks = 0;
        this.entity.stopRangedAnimation();
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.entity.isActionLocked()) {
            this.entity.stopRangedAnimation();
            return;
        }

        LivingEntity target = this.entity.getTarget();
        if (target == null || !target.isAlive()) {
            this.entity.stopRangedAnimation();
            this.entity.getNavigation().stop();
            return;
        }

        double rangeSq = this.range * this.range;
        double distanceSq = this.entity.distanceToSqr(target);
        if (!this.entity.canUseMountedGun()) {
            this.aimLockTicks = 0;
            this.entity.stopRangedAnimation();
            if (distanceSq > rangeSq * 0.7D) {
                this.entity.getNavigation().moveTo(target, 1.25D);
            } else {
                this.entity.getNavigation().stop();
            }
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            return;
        }

        float angleDiff = this.rotateBodyToward(target);
        this.entity.getLookControl().setLookAt(target, 20.0F, 20.0F);
        Vec3 previewSpawn = this.leftGunShot ? this.entity.getLeftGunPos() : this.entity.getRightGunPos();
        boolean clearShot = this.entity.hasClearShot(previewSpawn, target);
        boolean inFireRange = distanceSq <= rangeSq;
        boolean readyToFire = inFireRange && clearShot && angleDiff <= FIRE_ANGLE_TOLERANCE;

        if (readyToFire) {
            this.entity.getNavigation().stop();
            this.aimLockTicks++;
            this.entity.startRangedAnimation(RANGED_POSE_BUFFER_TICKS);
        } else {
            this.aimLockTicks = 0;
            if (distanceSq > rangeSq * 0.9D || !clearShot) {
                this.entity.getNavigation().moveTo(target, 1.25D);
            } else {
                this.entity.getNavigation().stop();
            }
            this.entity.stopRangedAnimation();
        }

        if (this.cooldown > 0) {
            this.cooldown--;
        }

        if (!readyToFire) {
            return;
        }
        if (this.aimLockTicks < AIM_LOCK_REQUIRED_TICKS) {
            return;
        }
        if (this.cooldown > 0) {
            return;
        }

        Vec3 spawnVec = this.leftGunShot ? this.entity.getLeftGunPos() : this.entity.getRightGunPos();
        ArmoredWhaleProjectileEntity projectile = new ArmoredWhaleProjectileEntity(this.entity.level(), this.entity);
        projectile.setPos(spawnVec);
        projectile.setBaseDamage(4.0D);
        double dx = target.getX() - spawnVec.x;
        double dy = target.getEyeY() - spawnVec.y;
        double dz = target.getZ() - spawnVec.z;
        projectile.shoot(dx, dy, dz, 3.0F, 0.35F);
        this.entity.level().addFreshEntity(projectile);
        this.entity.level().playSound(
                null,
                spawnVec.x,
                spawnVec.y,
                spawnVec.z,
                ModSounds.BRUISER_SILENCED_FIRE.get(),
                SoundSource.HOSTILE,
                1.0F,
                0.9F + this.entity.getRandom().nextFloat() * 0.2F
        );

        this.leftGunShot = !this.leftGunShot;
        this.entity.startRangedAnimation(RANGED_POSE_BUFFER_TICKS);
        this.cooldown = this.fireInterval;
    }

    private float rotateBodyToward(LivingEntity target) {
        double dx = target.getX() - this.entity.getX();
        double dz = target.getZ() - this.entity.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.entity.getYRot(), desiredYaw, MAX_TURN_PER_TICK);
        this.entity.setYRot(nextYaw);
        this.entity.setYBodyRot(nextYaw);
        return Math.abs(Mth.degreesDifference(nextYaw, desiredYaw));
    }
}
