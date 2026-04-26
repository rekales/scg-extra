package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class SoulRipperChargeAttackGoal extends Goal {

    protected final SoulRipperEntity mob;

    public SoulRipperChargeAttackGoal(SoulRipperEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null && livingentity.isAlive()
                && !this.mob.getMoveControl().hasWanted()
                && this.mob.getRandom().nextInt(reducedTickDelay(7)) == 0) {
            return this.mob.distanceToSqr(livingentity) > 4.0D;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getMoveControl().hasWanted()
                && this.mob.isCharging()
                && this.mob.getTarget() != null
                && this.mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            Vec3 vec3 = livingentity.getEyePosition();
            this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
        }

        this.mob.setCharging(true);
        this.mob.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
    }

    @Override
    public void stop() {
        this.mob.setCharging(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            if (this.mob.getBoundingBox().inflate(1).intersects(livingentity.getBoundingBox())) {
                this.mob.doHurtTarget(livingentity);
                this.mob.setCharging(false);
            } else {
                double d0 = this.mob.distanceToSqr(livingentity);
                if (d0 < 9.0D) {
                    Vec3 vec3 = livingentity.getEyePosition();
                    this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
                }
            }

        }
    }
}
