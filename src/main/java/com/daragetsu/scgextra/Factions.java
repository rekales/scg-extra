package com.daragetsu.scgextra;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

// Faction Data Manager
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Factions {

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
