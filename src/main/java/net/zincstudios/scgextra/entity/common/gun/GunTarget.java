package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

// Maybe a class with entity lead tracking could be added when needed.
public interface GunTarget {

    Vec3 getPos(SimulatedGun gun, LivingEntity shooter);
}
