package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

final class BigLumpRangedAttackGoal extends Goal {
    private enum CombatState {
        CHASE,
        FIRE_MOVE,
        FIRE_HOLD
    }

    private static final float HOLD_DISTANCE = 4.6F;
    private static final float HOLD_EXIT_DISTANCE = 6.8F;
    private static final float ENTER_ATTACK_RANGE = 8.0F;
    private static final float EXIT_ATTACK_RANGE = 9.5F;

    private static final float FIRE_YAW_TOLERANCE = 20.0F;
    private static final float FIRE_PITCH_TOLERANCE = 24.0F;
    private static final int AIM_STABLE_REQUIRED_TICKS = 6;
    private static final int PATH_RECALC_INTERVAL_TICKS_NEAR = 12;
    private static final int PATH_RECALC_INTERVAL_TICKS_FAR = 10;
    private static final float MAX_TURN_PER_TICK_NEAR = 4.0F;
    private static final float MAX_TURN_PER_TICK_FAR = 8.0F;
    private static final float MOVE_YAW_BLEND_DISTANCE = 6.6F;
    private static final float HOLD_REPOSITION_YAW_DIFF = 10.0F;
    private static final double HOLD_REPOSITION_LATERAL_SPEED = 0.020D;
    private static final double LEAD_TIME_SECONDS = 0.20D;

    private final BigLumpEntity mob;
    private final double speedModifier;
    private final int attackInterval;

    private CombatState state = CombatState.CHASE;
    private int attackTime = -1;
    private int shotsInMagazine;
    private int reloadTicks;
    private int pathRecalcTicks;
    private int aimStableTicks;
    private long tickCounter;
    private float controlledYaw;
    private float controlledPitch;
    private boolean controlledLookInitialized;

    private Vec3 lastTargetPos;

    BigLumpRangedAttackGoal(BigLumpEntity mob, double speedModifier, int attackInterval, float attackRadius) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return this.mob.isValidTarget(target) && !this.mob.isMeleeAnimationLocked() && !this.mob.isMeleeRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return this.mob.isValidTarget(target) && !this.mob.isMeleeAnimationLocked() && !this.mob.isMeleeRange(target);
    }

    @Override
    public void start() {
        this.state = CombatState.CHASE;
        this.attackTime = this.attackInterval;
        this.shotsInMagazine = 0;
        this.reloadTicks = 0;
        this.pathRecalcTicks = 0;
        this.aimStableTicks = 0;
        this.lastTargetPos = null;
        this.tickCounter = 0;
        this.controlledYaw = this.mob.getYRot();
        this.controlledPitch = this.mob.getXRot();
        this.controlledLookInitialized = true;
        this.mob.getNavigation().stop();
        this.mob.stopRangedAnimation();
        this.state = CombatState.CHASE;
        this.attackTime = -1;
        this.pathRecalcTicks = 0;
        this.aimStableTicks = 0;
        this.lastTargetPos = null;
        this.controlledLookInitialized = false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.tickCounter++;
        LivingEntity target = this.mob.getTarget();
        if (!this.mob.isValidTarget(target)) {
            return;
        }

        double distanceSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean canSee = this.mob.getSensing().hasLineOfSight(target);
        double enterAttackSqr = ENTER_ATTACK_RANGE * ENTER_ATTACK_RANGE;
        double exitAttackSqr = EXIT_ATTACK_RANGE * EXIT_ATTACK_RANGE;
        double holdSqr = HOLD_DISTANCE * HOLD_DISTANCE;
        double holdExitSqr = HOLD_EXIT_DISTANCE * HOLD_EXIT_DISTANCE;

        CombatState prevState = this.state;
        updateState(distanceSqr, enterAttackSqr, exitAttackSqr, holdSqr, holdExitSqr);
        if (prevState != this.state) {
        }

        Vec3 predictedAimPos = getPredictedAimPos(target);
        lookToward(predictedAimPos, distanceSqr);

        tickMovement(target);
        Vec3 mobVel = this.mob.getDeltaMovement();

        if (this.attackTime > 0) {
            this.attackTime--;
        }

        if (this.reloadTicks > 0) {
            this.reloadTicks--;
            this.mob.startRangedAnimation(3);
            return;
        }

        if (this.state == CombatState.CHASE) {
            this.aimStableTicks = 0;
            return;
        }

        this.aimStableTicks++;

        if (this.attackTime <= 0 && this.aimStableTicks >= AIM_STABLE_REQUIRED_TICKS) {
            this.mob.fireMountedGun(predictedAimPos);
            this.mob.startRangedAnimation(6);
            this.attackTime = this.attackInterval;
            this.shotsInMagazine++;

            if (this.shotsInMagazine >= BigLumpEntity.GUN_MAGAZINE_SIZE) {
                this.shotsInMagazine = 0;
                this.reloadTicks = BigLumpEntity.GUN_RELOAD_TICKS;
            }
        }
    }

    private void updateState(double distanceSqr, double enterAttackSqr, double exitAttackSqr, double holdSqr, double holdExitSqr) {
        switch (this.state) {
            case CHASE -> {
                if (distanceSqr <= enterAttackSqr) {
                    this.state = distanceSqr <= holdSqr ? CombatState.FIRE_HOLD : CombatState.FIRE_MOVE;
                }
            }
            case FIRE_MOVE -> {
                if (distanceSqr > exitAttackSqr) {
                    this.state = CombatState.CHASE;
                } else if (distanceSqr <= holdSqr) {
                    this.state = CombatState.FIRE_HOLD;
                }
            }
            case FIRE_HOLD -> {
                if (distanceSqr > exitAttackSqr) {
                    this.state = CombatState.CHASE;
                } else if (distanceSqr > holdExitSqr) {
                    this.state = CombatState.FIRE_MOVE;
                }
            }
        }
    }

    private void tickMovement(LivingEntity target) {
        if (this.pathRecalcTicks > 0) {
            this.pathRecalcTicks--;
        }

        if (this.state == CombatState.FIRE_HOLD) {
            boolean shouldReposition = shouldRepositionInHold(target);
            if (shouldReposition) {
                this.mob.getNavigation().moveTo(target, this.speedModifier * 0.92D);
            } else {
                this.mob.getNavigation().stop();
            }
            return;
        }

        if (this.pathRecalcTicks <= 0) {
            this.mob.getNavigation().moveTo(target, this.speedModifier);
            this.pathRecalcTicks = getPathRecalcInterval();
        }
    }

    private boolean shouldRepositionInHold(LivingEntity target) {
        double dx = target.getX() - this.mob.getX();
        double dz = target.getZ() - this.mob.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float yawDiff = Math.abs(Mth.degreesDifference(this.controlledYaw, desiredYaw));
        if (yawDiff > HOLD_REPOSITION_YAW_DIFF) {
            return true;
        }

        Vec3 toTarget = new Vec3(dx, 0.0D, dz);
        double toTargetLen = toTarget.length();
        if (toTargetLen < 1.0E-5D) {
            return false;
        }
        Vec3 toNorm = toTarget.scale(1.0D / toTargetLen);
        Vec3 perp = new Vec3(-toNorm.z, 0.0D, toNorm.x);
        Vec3 targetVel = target.getDeltaMovement();
        double lateralSpeed = Math.abs(targetVel.x * perp.x + targetVel.z * perp.z);
        return lateralSpeed > HOLD_REPOSITION_LATERAL_SPEED;
    }

    private Vec3 getPredictedAimPos(LivingEntity target) {
        Vec3 current = target.getEyePosition();
        Vec3 predicted;
        if (this.lastTargetPos == null) {
            predicted = current;
        } else {
            Vec3 velocity = current.subtract(this.lastTargetPos);
            predicted = current.add(velocity.scale(LEAD_TIME_SECONDS * 20.0D));
        }
        this.lastTargetPos = current;
        return predicted;
    }

    private void lookToward(Vec3 aimPos, double distanceSqr) {
        if (this.state == CombatState.CHASE) {
            this.mob.getLookControl().setLookAt(this.mob.getX() + this.mob.getDeltaMovement().x * 4.0D, this.mob.getEyeY(), this.mob.getZ() + this.mob.getDeltaMovement().z * 4.0D, 10.0F, 10.0F);
            return;
        }

        this.mob.getLookControl().setLookAt(aimPos.x, aimPos.y, aimPos.z, 14.0F, 14.0F);

        double dx = aimPos.x - this.mob.getX();
        double dz = aimPos.z - this.mob.getZ();
        float desiredAimYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float desiredYaw = desiredAimYaw;
        float turnStep = getTurnStep(distanceSqr);

        if (this.state == CombatState.FIRE_MOVE && this.mob.getNavigation().isInProgress()) {
            Vec3 vel = this.mob.getDeltaMovement();
            double horizVelSqr = vel.x * vel.x + vel.z * vel.z;
            if (horizVelSqr > 0.0009D) {
                float moveYaw = (float) (Math.atan2(vel.z, vel.x) * Mth.RAD_TO_DEG) - 90.0F;
                float blend = distanceSqr >= (MOVE_YAW_BLEND_DISTANCE * MOVE_YAW_BLEND_DISTANCE) ? 0.70F : 0.45F;
                float deltaToMove = Mth.degreesDifference(desiredAimYaw, moveYaw);
                desiredYaw = Mth.wrapDegrees(desiredAimYaw - deltaToMove * blend);
            }
        }
        if (!this.controlledLookInitialized) {
            this.controlledYaw = this.mob.getYRot();
            this.controlledPitch = this.mob.getXRot();
            this.controlledLookInitialized = true;
        }

        float yawDiff = Mth.degreesDifference(this.controlledYaw, desiredYaw);
        float clampedYawStep = Mth.clamp(yawDiff, -turnStep, turnStep);
        this.controlledYaw = Mth.wrapDegrees(this.controlledYaw + clampedYawStep);

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double dy = aimPos.y - this.mob.getEyeY();
        float desiredPitch = (float) (-(Math.atan2(dy, horizontal) * Mth.RAD_TO_DEG));
        float pitchDiff = Mth.degreesDifference(this.controlledPitch, desiredPitch);
        float clampedPitchStep = Mth.clamp(pitchDiff, -turnStep, turnStep);
        this.controlledPitch = Mth.wrapDegrees(this.controlledPitch + clampedPitchStep);

        if (this.state == CombatState.FIRE_MOVE || this.state == CombatState.CHASE) {
            this.mob.setXRot(this.controlledPitch);
            this.mob.setYHeadRot(this.controlledYaw);
        } else {
            this.mob.setYRot(this.controlledYaw);
            this.mob.setYBodyRot(this.controlledYaw);
            this.mob.setYHeadRot(this.controlledYaw);
            this.mob.setXRot(this.controlledPitch);
        }
    }

    private float getTurnStep(double distanceSqr) {
        if (this.state == CombatState.CHASE || distanceSqr > (double) (EXIT_ATTACK_RANGE * EXIT_ATTACK_RANGE)) {
            return MAX_TURN_PER_TICK_FAR;
        }
        return MAX_TURN_PER_TICK_NEAR;
    }

    private int getPathRecalcInterval() {
        if (this.state == CombatState.CHASE) {
            return PATH_RECALC_INTERVAL_TICKS_FAR;
        }
        return PATH_RECALC_INTERVAL_TICKS_NEAR;
    }

    private boolean isAimStable(Vec3 aimPos) {
        double dx = aimPos.x - this.mob.getX();
        double dz = aimPos.z - this.mob.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float yawDiff = Math.abs(Mth.degreesDifference(this.mob.getYRot(), desiredYaw));

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double dy = aimPos.y - this.mob.getEyeY();
        float desiredPitch = (float) (-(Math.atan2(dy, horizontal) * Mth.RAD_TO_DEG));
        float pitchDiff = Math.abs(Mth.degreesDifference(this.mob.getXRot(), desiredPitch));

        boolean stable = yawDiff <= FIRE_YAW_TOLERANCE && pitchDiff <= FIRE_PITCH_TOLERANCE;
        return stable;
    }
}

