package net.zincstudios.scgextra.entity.fac.trench_goblin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class TrenchGoblinMeleeGoal extends MeleeAttackGoal {
    private static final int ATTACK_INTERVAL_TICKS = 26;
    private static final int HIT_DELAY_TICKS = 7;

    private final PathfinderMob mob;

    public TrenchGoblinMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
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
            if (this.mob instanceof TrenchGoblinEntity trenchGoblin) {
                trenchGoblin.triggerAnim("attack", "attack");
                trenchGoblin.scheduleDelayedHit(enemy, HIT_DELAY_TICKS);
            }
        }
    }
}
