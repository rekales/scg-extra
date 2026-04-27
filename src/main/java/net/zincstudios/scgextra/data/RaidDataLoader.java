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
import net.zincstudios.scgextra.raid.WaveRaid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.util.*;

@ParametersAreNonnullByDefault
public class RaidDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WaveRaid.class, new RaidDeserializer())
            .setPrettyPrinting()
            .create();
    public static final RaidDataLoader INSTANCE = new RaidDataLoader();
    private static final String DIRECTORY = "raids";

    public RaidDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        WaveRaid.clearWaveRaids();

        jsonMap.forEach((resLoc, jsonElement) -> {
            if (resLoc.getNamespace().equals(SCGExtra.MOD_ID))  {
                try {
                    WaveRaid raid = GSON.fromJson(jsonElement, WaveRaid.class);
                    WaveRaid.addWaveRaid(raid);
                } catch (Exception e) {
                    SCGExtra.LOGGER.warn("Failed to load raid for some reason: " + resLoc);
                }
            }
        });
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    private static class RaidDeserializer implements JsonDeserializer<WaveRaid> {

        @Override
        public WaveRaid deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String originalId = obj.get("raid_original_id").getAsString();
            String id = obj.get("raid_id").getAsString();

            WaveRaid.Profile profile = parseProfile(obj.getAsJsonObject("raid_profile"));

            List<WaveRaid.RaiderEntry> infantry = parseRaiders(obj.getAsJsonArray("infantry"));
            List<WaveRaid.RaiderEntry> elite = parseRaiders(obj.getAsJsonArray("elite"));
            List<WaveRaid.RaiderEntry> miniboss = parseRaiders(obj.getAsJsonArray("miniboss"));
            List<WaveRaid.RaiderEntry> boss = parseRaiders(obj.getAsJsonArray("boss"));

            return new WaveRaid(id, originalId, profile, infantry, elite, miniboss, boss);
        }

        private WaveRaid.Profile parseProfile(JsonObject profileObj) {
            WaveRaid.Wave first = parseWave(profileObj.getAsJsonObject("first_wave"));
            WaveRaid.Wave second = parseWave(profileObj.getAsJsonObject("second_wave"));
            WaveRaid.Wave third = parseWave(profileObj.getAsJsonObject("third_wave"));
            WaveRaid.Wave boss = parseWave(profileObj.getAsJsonObject("boss_wave"));

            return new WaveRaid.Profile(first, second, third, boss);
        }
        private WaveRaid.Wave parseWave(JsonObject waveObj) {
            int infantry = waveObj.has("infantry") ? waveObj.get("infantry").getAsInt() : 0;
            int elite = waveObj.has("elite") ? waveObj.get("elite").getAsInt() : 0;
            int miniboss = waveObj.has("miniboss") ? waveObj.get("miniboss").getAsInt() : 0;
            return new WaveRaid.Wave(infantry, elite, miniboss);
        }

        private List<WaveRaid.RaiderEntry> parseRaiders(com.google.gson.JsonArray array) {
            List<WaveRaid.RaiderEntry> entries = new ArrayList<>();

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

                entries.add(new WaveRaid.RaiderEntry(mobType, maxHealth, weight));
            });

            return entries;
        }
    }
}
