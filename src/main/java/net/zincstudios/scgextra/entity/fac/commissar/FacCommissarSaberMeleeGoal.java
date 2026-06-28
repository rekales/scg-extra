package net.zincstudios.scgextra.entity.fac.commissar;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.zincstudios.scgextra.item.ModItems;

public class FacCommissarSaberMeleeGoal extends MeleeAttackGoal {

    private final FacCommissarEntity mob;

    public FacCommissarSaberMeleeGoal(FacCommissarEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
    }

    private boolean hasSaber() {
        return this.mob.getMainHandItem().is(ModItems.CAVALRY_SABER.get())
                || this.mob.getOffhandItem().is(ModItems.CAVALRY_SABER.get());
    }

    @Override
    public boolean canUse() {
        return hasSaber() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return hasSaber() && super.canContinueToUse();
    }
}
