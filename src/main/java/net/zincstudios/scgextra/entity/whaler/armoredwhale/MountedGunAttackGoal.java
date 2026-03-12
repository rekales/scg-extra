package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.entity.projectile.EnemyProjectileEntity;
import top.ribs.scguns.init.ModSounds;

public abstract class MountedGunAttackGoal extends Goal {

    private final int fireInterval;
    protected final float range;
    protected final ArmoredWhaleEntity mob;

    private int cooldown = 0;

    public MountedGunAttackGoal(ArmoredWhaleEntity mob, int fireInterval, float range) {
        this.mob = mob;
        this.fireInterval = fireInterval;
        this.range = range;
    }

    public abstract boolean canShootTarget(LivingEntity target);

    public abstract void triggerGunFlash();

    public abstract Vec3 getProjectileSpawnPos();

    public abstract void updateGunAnimations(LivingEntity target);

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = fireInterval;
    }

    @Override
    public void stop() {
        super.stop();
        this.cooldown = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        if (this.cooldown <= 0) {
            if (target != null && canShootTarget(target)) {
                fireGun(target);
            }
            this.cooldown = this.fireInterval;
        } else {
            this.cooldown--;
        }

        if (target != null) {
            updateGunAnimations(target);
        }
    }

    private void fireGun(LivingEntity target) {
        Vec3 spawnVec = this.getProjectileSpawnPos();
        EnemyProjectileEntity bolt = new ArmoredWhaleProjectileEntity(this.mob.level(), this.mob);
        bolt.setPos(spawnVec);
        double dx = target.getX() - spawnVec.x;
        double dy = target.getEyeY() - spawnVec.y;
        double dz = target.getZ() - spawnVec.z;
        bolt.shoot(dx, dy, dz, 3.0F, 1.5F);
        this.mob.level().addFreshEntity(bolt);
        this.mob.level().playSound(null, spawnVec.x, spawnVec.y, spawnVec.z, ModSounds.BRUISER_SILENCED_FIRE.get(), SoundSource.HOSTILE, 0.8F, 1.2F);
        this.triggerGunFlash();
    }

    public static class Left extends MountedGunAttackGoal {

        public Left(ArmoredWhaleEntity mob, int fireInterval, float range) {
            super(mob, fireInterval, range);
        }

        @Override
        public boolean canShootTarget(LivingEntity target) {
            Vec3 spawnVec = this.getProjectileSpawnPos();
            float targetYRot = (Mth.wrapDegrees(
                    (float)Mth.atan2(
                            target.getZ() - spawnVec.z,
                            target.getX() - spawnVec.x
                    ) * Mth.RAD_TO_DEG) + 810 - this.mob.getYRot()) % 360;
            return 180<=targetYRot && targetYRot<360 && spawnVec.closerThan(target.position(), this.range);
        }

        @Override
        public Vec3 getProjectileSpawnPos() {
            return this.mob.getLeftGunPos();
        }

        @Override
        public void updateGunAnimations(LivingEntity target) {
            Vec3 spawnVec = this.getProjectileSpawnPos();
            float relativeYRot = (float)Mth.atan2(
                    target.getZ() - spawnVec.z,
                    target.getX() - spawnVec.x
            );
            this.mob.setLeftGunYRot(relativeYRot - this.mob.getYRot() * Mth.DEG_TO_RAD);
        }

        @Override
        public void triggerGunFlash() {

        }
    }

    public static class Right extends MountedGunAttackGoal {

        public Right(ArmoredWhaleEntity mob, int fireInterval, float range) {
            super(mob, fireInterval, range);
        }

        @Override
        public boolean canShootTarget(LivingEntity target) {
            Vec3 spawnVec = this.getProjectileSpawnPos();
            float targetYRot = (Mth.wrapDegrees(
                    (float)Mth.atan2(
                            target.getZ() - spawnVec.z,
                            target.getX() - spawnVec.x
                    ) * Mth.RAD_TO_DEG) + 810 - this.mob.getYRot()) % 360;
            return 0<=targetYRot && targetYRot<180 && spawnVec.closerThan(target.position(), this.range);
        }

        @Override
        public Vec3 getProjectileSpawnPos() {
            return this.mob.getRightGunPos();
        }

        @Override
        public void updateGunAnimations(LivingEntity target) {
            Vec3 spawnVec = this.getProjectileSpawnPos();
            float relativeYRot = (float)Mth.atan2(
                    target.getZ() - spawnVec.z,
                    target.getX() - spawnVec.x
            );
            this.mob.setRightGunYRot(relativeYRot - this.mob.getYRot() * Mth.DEG_TO_RAD);
        }

        @Override
        public void triggerGunFlash() {

        }
    }
}
