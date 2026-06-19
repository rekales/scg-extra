package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityCenterMassTarget implements GunTarget {

    private final LivingEntity entity;

    public EntityCenterMassTarget(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public Vec3 getPos(SimulatedGun gun, LivingEntity shooter) {
        return SimulatedGun.getCenterMassPos(this.entity);
    }
}
