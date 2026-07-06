package net.zincstudios.scgextra.entity.common.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.event.TickEvent;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

// An implementation separate from base scguns because it's not great
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GunFlashHandler {

    // entity id to set of gun indexes that has flash, TO_RENDER to hold the flashes for a single tick
    private static final HashMap<Integer, Set<Integer>> TO_FLASH = new HashMap<>();
    private static final HashMap<Integer, Set<Integer>> TO_RENDER = new HashMap<>();

    public static void addToFlash(int entityId, int index) {
        TO_FLASH.putIfAbsent(entityId, new HashSet<>());
        if (TO_FLASH.get(entityId) != null) {
            TO_FLASH.get(entityId).add(index);
        }
    }

    public static Set<Integer> getFlashesToRender(int entityId) {
        if (TO_RENDER.get(entityId) == null) return new HashSet<>();
        return TO_RENDER.get(entityId);
    }

    public static boolean hasFlashToRender(int entityId, int index) {
        if (TO_RENDER.get(entityId) == null) return false;
        return TO_RENDER.get(entityId).contains(index);
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        TO_RENDER.clear();
        TO_RENDER.putAll(TO_FLASH);
        TO_FLASH.clear();
    }

    //TODO: maybe clear to_render on 0.5 partial tick?
}
