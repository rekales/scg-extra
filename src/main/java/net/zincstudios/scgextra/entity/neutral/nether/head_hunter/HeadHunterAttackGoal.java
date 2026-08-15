package net.zincstudios.scgextra.entity.neutral.nether.head_hunter;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class HeadHunterAttackGoal extends MeleeAttackGoal{

    private int ticksUntilNextAttack;
    private HeadHunterEntity entity;
    private int cooldown = 0;
    public HeadHunterAttackGoal(HeadHunterEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        entity = mob;
    }
    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return super.canUse() && this.cooldown == 0;
    }
    @Override
    public void start() {
        super.start();
        this.ticksUntilNextAttack = 0;
    }
    @Override
    public void tick() {
        super.tick();
        if(entity.getTarget()!=null){
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
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
            enemy.addEffect(new MobEffectInstance(MobEffects.WITHER, 60));
            this.stop();
        }
    }
    @Override
    protected void resetAttackCooldown() {
        super.resetAttackCooldown();
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }
    @Override
    public void stop() {
        super.stop();
        this.cooldown = 20;
    }
}
