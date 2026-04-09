package net.zincstudios.scgextra.entity.fac.shovel_knight;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ShovelKnightMeleeAttackGoal extends MeleeAttackGoal {

    private static final int HIT_DELAY_TICKS = 6;

    private final ShovelKnightEntity mob;
    private int windupTicks = 0;
    private LivingEntity pendingTarget;

    public ShovelKnightMeleeAttackGoal(ShovelKnightEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    @Override
    public void stop() {
        super.stop();
        this.windupTicks = 0;
        this.pendingTarget = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.windupTicks > 0) {
            this.windupTicks--;
            if (this.windupTicks == 0 && this.pendingTarget != null && this.pendingTarget.isAlive()) {
                double reachSq = this.getAttackReachSqr(this.pendingTarget);
                if (this.mob.distanceToSqr(this.pendingTarget) <= reachSq + 0.75D) {
                    float damage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    this.pendingTarget.hurt(this.mob.damageSources().mobAttack(this.mob), damage);
                }
                this.pendingTarget = null;
            }
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double reachSq = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= reachSq && this.getTicksUntilNextAttack() <= 0 && this.windupTicks <= 0) {
            this.resetAttackCooldown();
            this.pendingTarget = enemy;
            this.windupTicks = HIT_DELAY_TICKS;
            this.mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }
}
