package net.zincstudios.scgextra.entity.neutral.inflicted_wolf;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class InflictedWolfMeleeGoal extends MeleeAttackGoal {
    private static final int ATTACK_INTERVAL_TICKS = 60;
    private static final int ATTACK_WINDUP_TICKS = 10;
    private static final int HIT_DELAY_TICKS = 4;

    private final PathfinderMob mob;

    public InflictedWolfMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    @Override
    protected int getAttackInterval() {
        return ATTACK_INTERVAL_TICKS;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        if (distToEnemySqr <= this.getAttackReachSqr(enemy) && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            if (this.mob instanceof InflictedWolfEntity wolf) {
                wolf.scheduleAttack(enemy, ATTACK_WINDUP_TICKS, HIT_DELAY_TICKS);
            } else {
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(enemy);
            }
        }
    }

    @Override
    protected double getAttackReachSqr(LivingEntity enemy) {
        if (this.mob instanceof InflictedWolfEntity wolf) {
            return wolf.getAttackReachSqr(enemy);
        }
        return super.getAttackReachSqr(enemy);
    }
}


