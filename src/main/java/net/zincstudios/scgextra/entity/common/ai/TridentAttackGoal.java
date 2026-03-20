package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;

public class TridentAttackGoal extends RangedAttackGoal{
    private final Drowned drowned;

    public TridentAttackGoal(RangedAttackMob pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
        this.drowned = (Drowned)pRangedAttackMob;
    }
    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return super.canUse() && this.drowned.getMainHandItem().is(Items.TRIDENT);
    }
    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        super.start();
        this.drowned.setAggressive(true);
        this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
    }
    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        super.stop();
        this.drowned.stopUsingItem();
        this.drowned.setAggressive(false);
    }
}
