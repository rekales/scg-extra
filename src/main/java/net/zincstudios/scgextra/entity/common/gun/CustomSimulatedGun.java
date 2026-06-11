package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;

public class CustomSimulatedGun implements SimulatedGun {

    @Override
    public void tickFire(LivingEntity entity, LivingEntity target, float accuracyModifier, boolean firing) {

    }

    @Override
    public boolean hasChanged(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMaxRange() {
        return 0;
    }

    @Override
    public float getIdealRange() {
        return 0;
    }
}
