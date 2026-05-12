package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BigLumpChaseGoal extends Goal {
    private static final int REPATH_INTERVAL_TICKS = 4;
    private static final double TARGET_MOVE_REPATH_DISTANCE_SQR = 1.0D;

    private final BigLumpEntity mob;
    private final double speed;
    private int repathCooldown;
    private double lastTargetX;
    private double lastTargetY;
    private double lastTargetZ;

    public BigLumpChaseGoal(BigLumpEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.shouldUseChaseGoal();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.shouldUseChaseGoal();
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (!this.mob.isValidTarget(target)) {
            return;
        }

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.mob.markHeadTurnReason("chase_track_target");
        this.mob.markMovementReason("chase_move_to_target");

        boolean canDirectChase = this.mob.hasLineOfSight(target)
                && Math.abs(target.getY() - this.mob.getY()) < 1.5D;

        if (canDirectChase) {
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().setWantedPosition(target.getX(), this.mob.getY(), target.getZ(), this.speed);
            this.mob.markBodyTurnReason("chase_body_by_move_control");
            this.repathCooldown = 0;
            return;
        }

        this.mob.markBodyTurnReason("chase_body_by_navigation");
        if (this.repathCooldown > 0) {
            this.repathCooldown--;
        }

        if (shouldRepath(target)) {
            this.mob.getNavigation().moveTo(target, this.speed);
            this.repathCooldown = REPATH_INTERVAL_TICKS;
            this.lastTargetX = target.getX();
            this.lastTargetY = target.getY();
            this.lastTargetZ = target.getZ();
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.mob.markMovementReason("chase_stop");
        this.repathCooldown = 0;
    }

    private boolean shouldRepath(LivingEntity target) {
        if (!this.mob.getNavigation().isInProgress()) {
            return true;
        }
        if (this.repathCooldown <= 0) {
            return true;
        }
        double dx = target.getX() - this.lastTargetX;
        double dy = target.getY() - this.lastTargetY;
        double dz = target.getZ() - this.lastTargetZ;
        return (dx * dx + dy * dy + dz * dz) >= TARGET_MOVE_REPATH_DISTANCE_SQR;
    }
}
