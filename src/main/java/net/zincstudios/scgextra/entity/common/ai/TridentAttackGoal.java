package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;

public class TridentAttackGoal<T extends Monster & RangedAttackMob> extends RangedAttackGoal {
    private final T mob;

    public TridentAttackGoal(T pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
        this.mob = pRangedAttackMob;
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return super.canUse() && this.mob.getMainHandItem().is(Items.TRIDENT);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        super.start();
        this.mob.setAggressive(true);
        this.mob.startUsingItem(InteractionHand.MAIN_HAND);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        super.stop();
        this.mob.stopUsingItem();
        this.mob.setAggressive(false);
    }
}
