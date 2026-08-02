package net.zincstudios.scgextra.entity.common.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

// An implementation separate from base scguns because it's not great
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GunFlashHandler {

    // entity id to set of gun indexes that has flash, TO_RENDER to hold the flashes for a single tick
    private static final Map<Integer, Set<FlashData>> TO_FLASH = new HashMap<>();
    private static final Map<Integer, Set<FlashData>> TO_RENDER = new HashMap<>();

    public static void addToFlash(int entityId, FlashData flashData) {
        TO_FLASH.putIfAbsent(entityId, new HashSet<>());
        if (TO_FLASH.get(entityId) != null) {
            TO_FLASH.get(entityId).add(flashData);
        }
    }

    public static Set<FlashData> getFlashesToRender(int entityId) {
        if (TO_RENDER.get(entityId) == null) return new HashSet<>();
        return TO_RENDER.get(entityId);
    }

    public static boolean hasFlashToRender(int entityId) {
        if (TO_RENDER.get(entityId) == null) return false;
        return !TO_RENDER.get(entityId).isEmpty();
    }

    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        TO_RENDER.clear();
        TO_RENDER.putAll(TO_FLASH);
        TO_FLASH.clear();
    }

    public record FlashData(int posIndex, ResourceLocation flashLoc, boolean enchanted, float scale) { }

    //TODO: maybe clear to_render on 0.5 partial tick?
}
