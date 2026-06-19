package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: Placeholder at best, implement when needed

@ParametersAreNonnullByDefault
public class ReloadingHeldSimulatedGun extends HeldSimulatedGun {

    protected final int reloadTime;  // -1 means doesn't need reload
    protected final int magSize;

    public ReloadingHeldSimulatedGun(GunItem gunItem) {
        super(gunItem);
        Gun gun = gunItem.getGun();
        this.reloadTime = gun.getReloads().getReloadTimer();
        this.magSize = gun.getReloads().getMaxAmmo();
    }

    @Override
    public boolean tickFire(LivingEntity shooter, Vec3 targetPos, float accuracyModifier, boolean firing) {
        return super.tickFire(shooter, targetPos, accuracyModifier, firing);
    }
}
