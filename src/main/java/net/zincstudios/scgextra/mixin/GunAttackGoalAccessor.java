package net.zincstudios.scgextra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.ribs.scguns.entity.ai.GunAttackGoal;

// TODO: pr for additional methods for getting state and deconstructing tick into multiple methods.
@Mixin(value = GunAttackGoal.class, remap = false)
public interface GunAttackGoalAccessor {

    @Accessor("aimingStabilityTimer")
    int getAimingStabilityTimer();

}
