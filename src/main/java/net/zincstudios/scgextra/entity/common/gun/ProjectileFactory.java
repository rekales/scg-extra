package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

/**
 * A simplified version of IProjectileFactory from scguns
  */
public interface ProjectileFactory {
    ProjectileEntity create(Level level, LivingEntity entity, Gun gun);
}
