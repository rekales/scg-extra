package net.zincstudios.scgextra.entity.turret;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class TurretManningHandler {

    private TurretManningHandler() {}

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().getVehicle() instanceof TurretSeatEntity) {
            event.setCanceled(true);
        }
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().getVehicle() instanceof TurretSeatEntity) {
            event.setCanceled(true);
        }
    }
}
