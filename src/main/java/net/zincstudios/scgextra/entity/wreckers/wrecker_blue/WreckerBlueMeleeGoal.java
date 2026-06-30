package net.zincstudios.scgextra.entity.wreckers.wrecker_blue;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class WreckerBlueMeleeGoal extends MeleeAttackGoal {
    private static final int ATTACK_INTERVAL_TICKS = 20;
    private static final int HIT_DELAY_TICKS = 6;

    private final PathfinderMob mob;

    public WreckerBlueMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
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
            this.mob.swing(InteractionHand.MAIN_HAND);
            if (this.mob instanceof WreckerBlueEntity blue) {
                blue.triggerAnim("attack", "attack");
                blue.scheduleDelayedHit(enemy, HIT_DELAY_TICKS);
            }
        }
    }
}
