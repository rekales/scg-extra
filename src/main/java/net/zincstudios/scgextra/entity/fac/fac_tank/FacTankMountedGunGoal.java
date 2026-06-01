package net.zincstudios.scgextra.entity.fac.fac_tank;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import top.ribs.scguns.init.ModSounds;

import java.util.EnumSet;

public class FacTankMountedGunGoal extends Goal {
    private static final float MAX_TURN_PER_TICK = 4.0F;
    private static final float FIRE_ANGLE_TOLERANCE = 2.0F;
    private static final int AIM_LOCK_REQUIRED_TICKS = 6;

    private final FacTankEntity entity;
    private final int fireInterval;
    private final float range;
    private final boolean leftGun;
    private int cooldown = 0;
    private int aimLockTicks = 0;

    public FacTankMountedGunGoal(FacTankEntity entity, int fireInterval, float range, boolean leftGun) {
        this.entity = entity;
        this.fireInterval = fireInterval;
        this.range = range;
        this.leftGun = leftGun;
    }

    @Override
    public boolean canUse() {
        return this.entity.getTarget() != null && !this.entity.isStunned() && !this.entity.isActionLocked();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = this.fireInterval;
        this.aimLockTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.cooldown = 0;
        this.aimLockTicks = 0;
    }

    @Override
    public void tick() {
        if (this.entity.isStunned() || this.entity.isActionLocked()) {
            return;
        }

        LivingEntity target = this.entity.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        if (!this.entity.canUseMountedGuns()) {
            this.aimLockTicks = 0;
            this.cooldown = Math.max(this.cooldown, 1);
            return;
        }

        Vec3 spawnVec = this.leftGun ? this.entity.getLeftGunPos() : this.entity.getRightGunPos();
        if (spawnVec.closerThan(target.position(), this.range)) {
            this.entity.getNavigation().stop();
        }
        float angleDiff;
        if (this.leftGun) {
            angleDiff = this.rotateBodyToward(target);
        } else {
            angleDiff = this.getBodyAngleDiff(target);
        }
        boolean clearShot = this.entity.hasClearShot(spawnVec, target);
        if (angleDiff <= FIRE_ANGLE_TOLERANCE && clearShot) {
            this.aimLockTicks++;
        } else {
            this.aimLockTicks = 0;
        }
        if (spawnVec.closerThan(target.position(), this.range) && this.aimLockTicks >= AIM_LOCK_REQUIRED_TICKS) {
            this.entity.markSideGunFiring();
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }

        if (!spawnVec.closerThan(target.position(), this.range)) {
            this.cooldown = this.fireInterval;
            return;
        }
        if (angleDiff > FIRE_ANGLE_TOLERANCE) {
            this.cooldown = 1;
            return;
        }
        if (!clearShot || this.aimLockTicks < AIM_LOCK_REQUIRED_TICKS) {
            this.cooldown = 2;
            return;
        }

        ArmoredWhaleProjectileEntity projectile = new ArmoredWhaleProjectileEntity(this.entity.level(), this.entity);
        projectile.setPos(spawnVec);
        projectile.setBaseDamage(6.0D);
        double dx = target.getX() - spawnVec.x;
        double dy = target.getEyeY() - spawnVec.y;
        double dz = target.getZ() - spawnVec.z;
        projectile.shoot(dx, dy, dz, 3.0F, 0.3F);
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
        this.entity.triggerSideGunAnimation();

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

    private float getBodyAngleDiff(LivingEntity target) {
        double dx = target.getX() - this.entity.getX();
        double dz = target.getZ() - this.entity.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        return Math.abs(Mth.degreesDifference(this.entity.getYRot(), desiredYaw));
    }
}
