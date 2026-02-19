package com.daragetsu.scgextra.entity.guardian_statue;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class GuardianLaserAttackGoal extends Goal {
    private final GuardianStatueEntity mob;
    private int attackTime;

    public GuardianLaserAttackGoal(GuardianStatueEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    public boolean canContinueToUse() {
        return super.canContinueToUse()
                && this.mob.getTarget() != null
                && this.mob.distanceToSqr(this.mob.getTarget()) > 9.0F;
    }

    @Override
    public void start() {
        this.attackTime = -10;
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 90.0F, 90.0F);
        }
        this.mob.hasImpulse = true;  // TODO: figure out what impulse is
    }

    @Override
    public void stop() {
        this.mob.setActiveAttackTarget(0);
        this.mob.setTarget(null);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.mob.getNavigation().stop();
            this.mob.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            if (!this.mob.hasLineOfSight(livingentity)) {
                this.mob.setTarget(null);
            } else {
                ++this.attackTime;
                if (this.attackTime == 0) {
                    this.mob.setActiveAttackTarget(livingentity.getId());
                    if (!this.mob.isSilent()) {
                        this.mob.level().broadcastEntityEvent(this.mob, (byte)21);
                    }
                } else if (this.attackTime >= this.mob.getAttackDuration()) {
                    float f = 1.0F;
                    if (this.mob.level().getDifficulty() == Difficulty.HARD) {
                        f += 2.0F;
                    }

                    livingentity.hurt(this.mob.damageSources().indirectMagic(this.mob, this.mob), f);
                    livingentity.hurt(this.mob.damageSources().mobAttack(this.mob), (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.mob.setTarget(null);
                }

                super.tick();
            }
        }
    }
}
