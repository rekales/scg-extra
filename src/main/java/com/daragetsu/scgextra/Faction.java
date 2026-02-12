package com.daragetsu.scgextra;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record Faction(String name, List<EntityType<?>> entities) {

    private static final Map<String, Faction> factions = new HashMap<>();

    public static void clearData() {
        factions.clear();
    }

    public static void addFaction(Faction faction) {
        factions.put(faction.name(), faction);
    }

    public static @Nullable Faction getFaction(String name) {
        return factions.get(name);
    }
}