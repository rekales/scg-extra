package net.zincstudios.scgextra.entity.neutral.inflicted_boar;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import top.ribs.scguns.init.ModEffects;

public class InflictedBoarAttackGoal extends MeleeAttackGoal{

    private int ticksUntilNextAttack;
    private InflictedBoarEntity entity;
    public InflictedBoarAttackGoal(InflictedBoarEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        entity = mob;
    }
    @Override
    public void start() {
        super.start();
        this.ticksUntilNextAttack = 0;
    }
    @Override
    public void tick() {
        super.tick();
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
    }
    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if(distToEnemySqr-d0<=1 && this.ticksUntilNextAttack <= 0){
            entity.triggerAnim("controller", "attack");
        }
        if (distToEnemySqr <= d0 && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(enemy);
            enemy.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 40));
        }
    }
    @Override
    protected void resetAttackCooldown() {
        super.resetAttackCooldown();
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }
}
