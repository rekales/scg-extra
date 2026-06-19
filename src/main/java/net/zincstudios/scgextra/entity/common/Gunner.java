package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;

public interface Gunner {
    // Currently just an interface to tag classes to not automatically get GunAttackGoal

    default void onGunFire(SimulatedGun gun, Vec3 targetPos) {}

    default boolean isFiring() {
        return false;
    }

    default void setFiring() {}

    default boolean isGunVisible() {
        return true;
    }
}
