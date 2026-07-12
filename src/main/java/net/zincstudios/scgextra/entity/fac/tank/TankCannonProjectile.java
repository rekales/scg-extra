package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.entity.projectile.RocketEntity;

public class TankCannonProjectile extends RocketEntity {

    public TankCannonProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn) {
        super(entityType, worldIn);
    }

    public TankCannonProjectile(EntityType<? extends ProjectileEntity> entityType, Level worldIn, LivingEntity shooter, Gun modifiedGun) {
        super(entityType, worldIn, shooter, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), modifiedGun);
        this.modifiedGravity = -0.015F;
        this.setItem(new ItemStack(top.ribs.scguns.init.ModItems.SHOTBALL.get()));
    }

    public TankCannonProjectile(Level worldIn, LivingEntity shooter, Gun modifiedGun) {
        this(FACEntities.TANK_CANNON_PROJECTILE.get(), worldIn, shooter, modifiedGun);
    }

    @Override
    public ItemStack getItem() {
        return super.getItem();
    }
}
