package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;

public interface SimulatedGun {

    void tick(LivingEntity entity, LivingEntity target, boolean firing);

    boolean hasChanged(LivingEntity entity);

    float getMaxRange();

    float getIdealRange();
}
