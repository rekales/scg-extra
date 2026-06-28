package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.sounds.FACSounds;

import java.util.List;

public class FacTankCannonGoal extends Goal {
    private static final float MAX_TURN_PER_TICK = 3.0F;
    private static final float FIRE_ANGLE_TOLERANCE = 2.0F;
    private static final int AIM_LOCK_REQUIRED_TICKS = 8;
    private final FacTankEntity entity;
    private final int cooldownTicks;
    private final int warningDurationTicks;
    private final float range;
    private final float damage;
    private final float radius;
    private int cooldown = 0;
    private int warningTicks = 0;
    private int aimLockTicks = 0;
    private int travelTicks = -1;
    private final int maxTravelTicks = 16;
    private Vec3 targetPos = Vec3.ZERO;
    private Vec3 startPos = Vec3.ZERO;

    public FacTankCannonGoal(FacTankEntity entity, int cooldownTicks, int warningDurationTicks, float range, float damage, float radius) {
        this.entity = entity;
        this.cooldownTicks = cooldownTicks;
        this.warningDurationTicks = warningDurationTicks;
        this.range = range;
        this.damage = damage;
        this.radius = radius;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && !this.entity.isStunned()
                && !this.entity.isActionLocked()
                && this.cooldown <= 0
                && this.entity.distanceToSqr(target) <= this.range * this.range;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.entity.isActionLocked() && (this.warningTicks > 0 || this.travelTicks >= 0);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = this.cooldownTicks;
        this.warningTicks = this.warningDurationTicks;
        this.entity.startCannonWarning(this.warningDurationTicks);
        this.aimLockTicks = 0;
        this.travelTicks = -1;
        this.entity.getNavigation().stop();
        LivingEntity target = this.entity.getTarget();
        if (target != null) {
            this.targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        }
        this.entity.level().playSound(
                null,
                this.entity.getX(),
                this.entity.getY(),
                this.entity.getZ(),
                FACSounds.FAC_TANK_CANNON_CHARGE.get(),
                SoundSource.HOSTILE,
                1.2F,
                1.0F
        );
    }

    @Override
    public void tick() {
        if (this.entity.isActionLocked()) {
            return;
        }
        if (this.warningTicks > 0) {
            this.tickWarning();
            return;
        }

        if (this.travelTicks >= 0) {
            this.tickTravel();
        }
    }

    private void tickWarning() {
        this.warningTicks--;
        this.entity.updateCannonWarning(this.warningTicks);
        this.entity.getNavigation().stop();
        Vec3 cannonPos = this.entity.getCannonPos();

        LivingEntity target = this.entity.getTarget();
        float angleDiff = 0.0F;
        boolean clearShot = false;
        if (target != null && target.isAlive()) {
            this.targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            angleDiff = this.rotateBodyToward(cannonPos, target);
            clearShot = this.entity.hasClearShot(cannonPos, target);
            if (angleDiff <= FIRE_ANGLE_TOLERANCE && clearShot) {
                this.aimLockTicks++;
            } else {
                this.aimLockTicks = 0;
            }
        } else {
            this.aimLockTicks = 0;
        }

        if (this.warningTicks <= 0) {
            if (target == null || !target.isAlive()) {
                this.warningTicks = 1;
                this.entity.updateCannonWarning(this.warningTicks);
                return;
            }
            if (angleDiff > FIRE_ANGLE_TOLERANCE || !clearShot || this.aimLockTicks < AIM_LOCK_REQUIRED_TICKS) {
                this.warningTicks = 1;
                this.entity.updateCannonWarning(this.warningTicks);
                return;
            }
            this.startPos = cannonPos;
            this.travelTicks = this.maxTravelTicks;
            this.entity.clearCannonWarning();
            this.entity.startCannonFireAnimation();
            this.entity.level().playSound(
                    null,
                    this.startPos.x,
                    this.startPos.y,
                    this.startPos.z,
                    SoundEvents.FIREWORK_ROCKET_LAUNCH,
                    SoundSource.HOSTILE,
                    1.5F,
                    0.7F
            );
        }
    }

    private float rotateBodyToward(Vec3 from, LivingEntity target) {
        double dx = target.getX() - from.x;
        double dz = target.getZ() - from.z;
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.entity.getYRot(), desiredYaw, MAX_TURN_PER_TICK);
        this.entity.setYRot(nextYaw);
        this.entity.setYBodyRot(nextYaw);
        return Math.abs(Mth.degreesDifference(nextYaw, desiredYaw));
    }

    private void tickTravel() {
        if (this.entity.level() instanceof ServerLevel level) {
            double progress = 1.0D - (this.travelTicks / (double) this.maxTravelTicks);
            Vec3 pos = this.startPos.lerp(this.targetPos, progress);

            level.sendParticles(
                    ParticleTypes.SMOKE,
                    pos.x,
                    pos.y,
                    pos.z,
                    6,
                    0.15D,
                    0.15D,
                    0.15D,
                    0.01D
            );
            level.sendParticles(
                    ParticleTypes.FLAME,
                    pos.x,
                    pos.y,
                    pos.z,
                    2,
                    0.05D,
                    0.05D,
                    0.05D,
                    0.0D
            );
        }

        if (this.travelTicks == 0) {
            this.explodeAt(this.targetPos);
        }

        this.travelTicks--;
    }

    private void explodeAt(Vec3 pos) {
        if (!(this.entity.level() instanceof ServerLevel level)) {
            return;
        }

        level.explode(
                this.entity,
                pos.x,
                pos.y,
                pos.z,
                this.radius,
                Level.ExplosionInteraction.NONE
        );

        level.sendParticles(
                ParticleTypes.EXPLOSION_EMITTER,
                pos.x,
                pos.y,
                pos.z,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        AABB area = AABB.unitCubeFromLowerCorner(pos).inflate(this.radius);
        List<LivingEntity> nearby = this.entity.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                target -> !target.is(this.entity) && !Faction.isFriendlies(this.entity, target)
        );

        for (LivingEntity target : nearby) {
            if (target.position().distanceTo(pos) > this.radius + 0.5D) {
                continue;
            }
            target.hurt(target.damageSources().mobAttack(this.entity), this.damage);
            double dx = target.getX() - pos.x;
            double dz = target.getZ() - pos.z;
            target.knockback(1.0D, dx, dz);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.entity.clearCannonWarning();
        this.warningTicks = 0;
        this.aimLockTicks = 0;
        this.travelTicks = -1;
    }
}
