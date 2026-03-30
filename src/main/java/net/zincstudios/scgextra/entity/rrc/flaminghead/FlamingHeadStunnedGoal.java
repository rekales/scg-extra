package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;

public class FlamingHeadStunnedGoal<T extends FlamingHeadEntity> extends StunnedWithVisualGoal<T> {

    public FlamingHeadStunnedGoal(T mob) {
        super(mob);
    }

    @Override
    public void start() {
        super.start();
        this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.STUNNED);
    }

    @Override
    public void stop() {
        super.stop();
        if (this.mob.getBehaviorState() == FlamingHeadEntity.BehaviorState.STUNNED) {
            this.mob.setBehaviorState(FlamingHeadEntity.BehaviorState.NONE);
        }
    }
}
