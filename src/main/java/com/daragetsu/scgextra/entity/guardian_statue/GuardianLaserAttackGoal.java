package com.daragetsu.scgextra.entity.guardian_statue;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

// NOTE: Look control will be handled on the entity itself
public class GuardianLaserAttackGoal extends Goal {

    protected final GuardianStatueEntity mob;
    protected int cooldown = 0;

    public GuardianLaserAttackGoal(GuardianStatueEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null
                && livingentity.isAlive()
                && this.mob.getBeamActiveTimer() <= 0;
    }

    @Override
    public void start() {
        LivingEntity target = this.mob.getTarget();
        this.cooldown = 30;
        this.mob.startGuardianLaserActiveTimer(0);
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 90.0F, 90.0F);
        }
    }

    @Override
    public void stop() {
        this.mob.startGuardianLaserActiveTimer(0);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        if (this.cooldown > 0) {
            this.cooldown--;
        } else {
            int timer = this.mob.getGuardianLaserAttackTimer();
            if (!this.mob.hasLineOfSight(target)) {
                if (timer > 0) {
                    this.mob.startGuardianLaserActiveTimer(0);
                    timer = 0;
                }
            } else {
                if (timer <= 0) {
                    this.mob.startGuardianLaserActiveTimer(this.mob.getAttackDuration());
                    timer = this.mob.getAttackDuration();
                }
            }

            if (timer == 1) {
                float mult = this.mob.level().getDifficulty() == Difficulty.HARD ? 3.0F : 1.0F;
                target.hurt(this.mob.damageSources().indirectMagic(this.mob, this.mob), mult);
                target.hurt(this.mob.damageSources().mobAttack(this.mob), (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                this.cooldown = 30;
            }
        }
    }
}
