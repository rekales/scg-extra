package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.LivingEntity;

public interface Gunner {
    // Currently just an interface to tag classes to not automatically get GunAttackGoal

    default void onGunFire(LivingEntity target) {}

    default boolean isFiring() {
        return false;
    }

    default void setFiring() {}

    default boolean isGunVisible() {
        return true;
    }
}
