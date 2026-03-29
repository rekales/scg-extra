package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

// Designed around FlamingHeadEntity, make generic later if needed using behaviour augmentation
public class RammingAttackGoal extends Goal {

    protected final FlamingHeadEntity mob;
    private final int cooldownDuration;
    private final float ramMaxDistance;
    private long cooldownEnd = 0;  // level timestamp

    public RammingAttackGoal(FlamingHeadEntity mob, int cooldownDuration, float ramMaxDistance) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.ramMaxDistance = ramMaxDistance;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration/2;  // Half cooldown at start
    }

    @Override
    public void stop() {
        if (this.mob.getBehaviorState() == FlamingHeadEntity.BehaviorState.RAMMING) {
            this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.NONE);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.level().getGameTime() > this.cooldownEnd) {
            if (this.mob.getBehaviorState() == FlamingHeadEntity.BehaviorState.NONE) {
                this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownDuration;
                this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.RAMMING);
                ramTarget();
            }
        }
    }

    public void ramTarget() {

    }
}
