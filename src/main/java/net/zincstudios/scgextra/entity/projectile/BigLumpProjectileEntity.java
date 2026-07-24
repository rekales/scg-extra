package net.zincstudios.scgextra.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BigLumpProjectileEntity extends ArmoredWhaleProjectileEntity{

    public BigLumpProjectileEntity(Level world, LivingEntity shooter) {
        super(world, shooter);
    }
    @Override
    public double getBaseDamage() {
        return 4;
    }
}
