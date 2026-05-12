package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class BigLumpIdleStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final BigLumpEntity mob;

    public BigLumpIdleStrollGoal(BigLumpEntity mob, double speedModifier) {
        super(mob, speedModifier);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return this.mob.shouldUseIdleGoals() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.shouldUseIdleGoals() && super.canContinueToUse();
    }
}
