package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

final class BigLumpMeleeGoal extends Goal {
    private final BigLumpEntity mob;
    private int cooldownTicks;
    private int hitDelayTicks;
    private boolean hitApplied;
    private LivingEntity attackTarget;

    BigLumpMeleeGoal(BigLumpEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
        }

        LivingEntity target = this.mob.getTarget();
        return this.mob.isValidTarget(target)
                && this.cooldownTicks <= 0
                && this.mob.isMeleeRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.hitApplied && this.hitDelayTicks >= 0 && this.mob.isValidTarget(this.attackTarget);
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.stopRangedAnimation();
        this.mob.startMeleeAnimationLock();
        this.mob.triggerAnim("melee", "melee_attack");
        this.attackTarget = this.mob.getTarget();
        this.hitApplied = false;
        this.hitDelayTicks = BigLumpEntity.MELEE_HIT_DELAY_TICKS;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        if (this.mob.isValidTarget(this.attackTarget)) {
            this.mob.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
            float desiredYaw = (float) Math.toDegrees(
                    Math.atan2(this.attackTarget.getZ() - this.mob.getZ(), this.attackTarget.getX() - this.mob.getX())
            ) - 90.0F;
            float nextYaw = Mth.approachDegrees(this.mob.getYRot(), desiredYaw, 14.0F);
            this.mob.setYRot(nextYaw);
            this.mob.setYBodyRot(nextYaw);
            this.mob.setYHeadRot(nextYaw);
        }

        if (this.hitDelayTicks > 0) {
            this.hitDelayTicks--;
            return;
        }

        if (!this.hitApplied && this.mob.canMeleeHit(this.attackTarget)) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(this.attackTarget);
        }

        this.hitApplied = true;
        this.cooldownTicks = this.mob.getRandom().nextInt(
                BigLumpEntity.MELEE_COOLDOWN_MAX_TICKS - BigLumpEntity.MELEE_COOLDOWN_MIN_TICKS + 1
        ) + BigLumpEntity.MELEE_COOLDOWN_MIN_TICKS;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.attackTarget = null;
        this.hitDelayTicks = -1;
        this.hitApplied = true;
    }
}

