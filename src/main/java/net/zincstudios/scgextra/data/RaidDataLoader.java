package net.zincstudios.scgextra.data;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
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
            try {
                WaveRaid raid = GSON.fromJson(jsonElement, WaveRaid.class);
                WaveRaid.addWaveRaid(raid);
            } catch (Exception e) {
                SCGExtra.LOGGER.warn("Failed to load raid for some reason: " + resLoc);
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

            JsonObject profileObj = obj.getAsJsonObject("raid_profile");
            WaveRaid.Wave first = parseWave(profileObj.getAsJsonObject("first_wave"));
            WaveRaid.Wave second = parseWave(profileObj.getAsJsonObject("second_wave"));
            WaveRaid.Wave third = parseWave(profileObj.getAsJsonObject("third_wave"));
            WaveRaid.Wave boss = parseWave(profileObj.getAsJsonObject("boss_wave"));

            WaveRaid.Profile profile = new WaveRaid.Profile(first, second, third, boss);

            List<WaveRaid.EntityAdjustment> adjustments = new ArrayList<>();
            if (obj.has("entity_adjustments")) {
                JsonArray adjArray = obj.getAsJsonArray("entity_adjustments");
                for (JsonElement elem : adjArray) {
                    JsonObject adjObj = elem.getAsJsonObject();
                    adjustments.add(new WaveRaid.EntityAdjustment(
                            adjObj.get("entity_id").getAsString(),
                            adjObj.get("max_health").getAsDouble()
                    ));
                }
            }

            return new WaveRaid(id, originalId, profile, adjustments);
        }

        private WaveRaid.Wave parseWave(JsonObject waveObj) {
            int infantry = waveObj.has("infantry") ? waveObj.get("infantry").getAsInt() : 0;
            int elite = waveObj.has("elite") ? waveObj.get("elite").getAsInt() : 0;
            int miniboss = waveObj.has("miniboss") ? waveObj.get("miniboss").getAsInt() : 0;
            return new WaveRaid.Wave(infantry, elite, miniboss);
        }
    }
}
