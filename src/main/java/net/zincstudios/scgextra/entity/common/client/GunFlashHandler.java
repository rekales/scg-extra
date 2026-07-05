package net.zincstudios.scgextra.entity.common.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.event.TickEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

// An implementation separate from base scguns because it's not great
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GunFlashHandler {

    // entity id to set of gun indexes that has flash
    private static final Map<Integer, Set<Integer>> TO_FLASH = new HashMap<>();

    public static void addToFlash(int entityId, int index) {
        Set<Integer> indexes = TO_FLASH.putIfAbsent(entityId, new HashSet<>());
        if (indexes != null) {
            indexes.add(index);
        }
    }

    public static Set<Integer> getFlashes(int entityId) {
        if (TO_FLASH.get(entityId) == null) return new HashSet<>();
        return TO_FLASH.get(entityId);
    }

    public static boolean hasFlash(int entityId, int index) {
        if (TO_FLASH.get(entityId) == null) return false;
        return TO_FLASH.get(entityId).contains(index);
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        TO_FLASH.clear();
    }
}
