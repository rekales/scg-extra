package net.zincstudios.scgextra.entity.wreckers.wrecker_jumbo;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class WreckerJumboMeleeGoal extends MeleeAttackGoal {
    private static final int ATTACK_INTERVAL_TICKS = 34;
    private static final int HIT_DELAY_TICKS = 12;

    private final PathfinderMob mob;

    public WreckerJumboMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    @Override
    protected int getAttackInterval() {
        return ATTACK_INTERVAL_TICKS;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity target) {
        return this.mob.getBbWidth() * 2.4F * this.mob.getBbWidth() * 2.4F + target.getBbWidth();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        if (distToEnemySqr <= this.getAttackReachSqr(enemy) && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            if (this.mob instanceof WreckerJumboEntity jumbo) {
                jumbo.triggerAnim("attack", "attack");
                jumbo.scheduleDelayedHit(enemy, HIT_DELAY_TICKS);
            }
        }
    }
}
