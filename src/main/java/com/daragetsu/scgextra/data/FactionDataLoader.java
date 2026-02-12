package com.daragetsu.scgextra.data;


import com.daragetsu.scgextra.Faction;
import com.daragetsu.scgextra.SCGExtra;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class FactionDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    public static final FactionDataLoader INSTANCE = new FactionDataLoader();

    public FactionDataLoader() {
        super(GSON, "faction");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Faction.clearData();

        jsonMap.forEach((id, json) -> {
            if (!id.getPath().equals(SCGExtra.MOD_ID)) return;

            try {
                TempFaction factionEntry = GSON.fromJson(json, TempFaction.class);

                @SuppressWarnings("removal")
                List<EntityType<?>> entityTypes = factionEntry.entities.stream()
                        .filter(ResourceLocation::isValidResourceLocation)
                        .map(ResourceLocation::new)
                        .map(ForgeRegistries.ENTITY_TYPES::getValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                Faction.addFaction(new Faction(factionEntry.id, entityTypes));
            } catch (Exception e) {
                SCGExtra.LOGGER.warn("Failed to load for some reason: " + id);
            }
        });
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    // Interim record for loading strings from json
    // TODO: replace with custom type adapters
    private record TempFaction(String id, List<String> entities) { }
}