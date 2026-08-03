package net.zincstudios.scgextra.item;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.zincstudios.scgextra.network.HoldAttackMessage;
import net.zincstudios.scgextra.network.SCGEPacketHandler;

import java.util.HashMap;

public class HoldAttackHandler {

    private static final HashMap<ServerPlayer, Boolean> SERVER_HELD_ATTACK = new HashMap<>();
    // NOTE: maybe needs some sort of timeout?

    // Client-side only
    private static boolean sentHeld = false;

    public static void setServerHeld(ServerPlayer player, boolean held) {
        SERVER_HELD_ATTACK.put(player, held);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        boolean held = mc.screen == null && mc.options.keyAttack.isDown();
        if (held != sentHeld) {
            sentHeld = held;
            SCGEPacketHandler.sendToServer(new HoldAttackMessage(held));
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        ItemStack heldStack = player.getMainHandItem();
        if (!(heldStack.getItem() instanceof HoldAttack holdAttackItem)) return;

        if (event.player instanceof ServerPlayer serverPlayer) {
            if (SERVER_HELD_ATTACK.getOrDefault(serverPlayer, false)) {
                holdAttackItem.onPlayerAttackTick(heldStack, player.level(), player);
            }
        } else {
            if (sentHeld) {
                holdAttackItem.onPlayerAttackTick(heldStack, player.level(), player);
            }
        }
    }

}
