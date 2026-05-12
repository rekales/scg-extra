package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class BigLumpIdleLookAtPlayerGoal extends LookAtPlayerGoal {
    private final BigLumpEntity mob;

    public BigLumpIdleLookAtPlayerGoal(BigLumpEntity mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance);
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
