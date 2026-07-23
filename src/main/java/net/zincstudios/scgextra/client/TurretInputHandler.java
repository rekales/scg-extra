package net.zincstudios.scgextra.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.turret.TurretSeatEntity;
import net.zincstudios.scgextra.network.ModNetwork;
import net.zincstudios.scgextra.network.TurretTriggerC2S;

@Mod.EventBusSubscriber(modid = SCGExtra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TurretInputHandler {

    private static boolean sentHeld;

    private TurretInputHandler() {}

    public static boolean isManningTurret() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getVehicle() instanceof TurretSeatEntity;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            sentHeld = false;
            return;
        }
        boolean held = mc.screen == null
                && mc.player.getVehicle() instanceof TurretSeatEntity
                && mc.options.keyAttack.isDown();
        if (held != sentHeld) {
            sentHeld = held;
            ModNetwork.CHANNEL.sendToServer(new TurretTriggerC2S(held));
        }
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack() && isManningTurret()) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
