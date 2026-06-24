package net.zincstudios.scgextra.data;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.raid.WaveRaidData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.util.*;

@ParametersAreNonnullByDefault
public class RaidDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WaveRaidData.class, new RaidDeserializer())
            .setPrettyPrinting()
            .create();
    public static final RaidDataLoader INSTANCE = new RaidDataLoader();
    private static final String DIRECTORY = "raids";

    public RaidDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        WaveRaidData.clearWaveRaids();

        jsonMap.forEach((resLoc, jsonElement) -> {
            if (resLoc.getNamespace().equals(SCGExtra.MOD_ID))  {
                try {
                    WaveRaidData raid = GSON.fromJson(jsonElement, WaveRaidData.class);
                    WaveRaidData.addWaveRaid(raid);
                    SCGExtra.LOGGER.info("Loaded raid: " + raid.id());
                } catch (Exception e) {
                    SCGExtra.LOGGER.warn("Failed to load raid for some reason: " + resLoc);
                    SCGExtra.LOGGER.warn("Reason: " + e.getMessage());
                }
            }
        });
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    private static class RaidDeserializer implements JsonDeserializer<WaveRaidData> {

        @Override
        public WaveRaidData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String originalId = obj.get("raid_original_id").getAsString();
            String id = obj.get("raid_id").getAsString();

            List<WaveRaidData.Wave> waves = parseWaves(obj.getAsJsonArray("waves"));

            Set<WaveRaidData.RaiderEntry> infantry = parseRaiders(obj.getAsJsonArray("infantry"));
            Set<WaveRaidData.RaiderEntry> elite = parseRaiders(obj.getAsJsonArray("elite"));
            Set<WaveRaidData.RaiderEntry> miniboss = parseRaiders(obj.getAsJsonArray("miniboss"));
            Set<WaveRaidData.RaiderEntry> boss = parseRaiders(obj.getAsJsonArray("boss"));

            return new WaveRaidData(id, originalId, waves, infantry, elite, miniboss, boss);
        }

        private List<WaveRaidData.Wave> parseWaves(com.google.gson.JsonArray array) {
            List<WaveRaidData.Wave> waves = new ArrayList<>();

            array.forEach(element -> {
                JsonObject waveObj = element.getAsJsonObject();
                int infantry = waveObj.has("infantry") ? waveObj.get("infantry").getAsInt() : 0;
                int elite = waveObj.has("elite") ? waveObj.get("elite").getAsInt() : 0;
                int miniboss = waveObj.has("miniboss") ? waveObj.get("miniboss").getAsInt() : 0;
                int boss = waveObj.has("boss") ? waveObj.get("boss").getAsInt() : 0;
                waves.add(new WaveRaidData.Wave(infantry, elite, miniboss, boss));
            });

            return waves;
        }

        private Set<WaveRaidData.RaiderEntry> parseRaiders(com.google.gson.JsonArray array) {
            Set<WaveRaidData.RaiderEntry> entries = new HashSet<>();

            array.forEach(element -> {
                JsonObject raiderObj = element.getAsJsonObject();
                ResourceLocation entityId = ResourceLocation.parse(raiderObj.get("entity_id").getAsString());

                EntityType<?> rawType = ForgeRegistries.ENTITY_TYPES.getValue(entityId);

                if (rawType == null || !rawType.getBaseClass().isAssignableFrom(Mob.class)) {
                    SCGExtra.LOGGER.warn("Entity type " + entityId + " is not a Mob");
                    throw new IllegalStateException();
                }

                @SuppressWarnings("unchecked")
                EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) rawType;

                double maxHealth = raiderObj.has("max_health") ? raiderObj.get("max_health").getAsDouble() : -1;
                double weight = raiderObj.has("weight") ? raiderObj.get("weight").getAsDouble() : 1.0;
                double value = raiderObj.has("value") ? raiderObj.get("value").getAsDouble() : 1.0;
                int maxCount = raiderObj.has("max_count") ? raiderObj.get("max_count").getAsInt() : 0;

                entries.add(new WaveRaidData.RaiderEntry(mobType, maxHealth, weight, value, maxCount));
            });

            return entries;
        }
    }
}
