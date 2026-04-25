package net.zincstudios.scgextra.data;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.raid.Raid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.util.*;

@ParametersAreNonnullByDefault
public class RaidDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Raid.class, new RaidDeserializer())
            .setPrettyPrinting()
            .create();
    public static final RaidDataLoader INSTANCE = new RaidDataLoader();
    private static final String DIRECTORY = "raids";

    public RaidDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
        Raid.clearWaveRaids();

        jsonMap.forEach((resLoc, jsonElement) -> {
            try {
                Raid raid = GSON.fromJson(jsonElement, Raid.class);
                Raid.addWaveRaid(raid);
            } catch (Exception e) {
                SCGExtra.LOGGER.warn("Failed to load raid for some reason: " + resLoc);
            }
        });
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    private static class RaidDeserializer implements JsonDeserializer<Raid> {

        @Override
        public Raid deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String alias = obj.get("raid_original_id").getAsString();
            String name = obj.get("raid_id").getAsString();

            JsonObject profileObj = obj.getAsJsonObject("raid_profile");
            Raid.Wave first = parseWave(profileObj.getAsJsonObject("first_wave"));
            Raid.Wave second = parseWave(profileObj.getAsJsonObject("second_wave"));
            Raid.Wave third = parseWave(profileObj.getAsJsonObject("third_wave"));
            Raid.Wave boss = parseWave(profileObj.getAsJsonObject("boss_wave"));

            Raid.Profile profile = new Raid.Profile(first, second, third, boss);

            List<Raid.EntityAdjustment> adjustments = new ArrayList<>();
            if (obj.has("entity_adjustments")) {
                JsonArray adjArray = obj.getAsJsonArray("entity_adjustments");
                for (JsonElement elem : adjArray) {
                    JsonObject adjObj = elem.getAsJsonObject();
                    adjustments.add(new Raid.EntityAdjustment(
                            adjObj.get("entity_id").getAsString(),
                            adjObj.get("max_health").getAsDouble()
                    ));
                }
            }

            return new Raid(name, alias, profile, adjustments);
        }

        private Raid.Wave parseWave(JsonObject waveObj) {
            int infantry = waveObj.has("infantry") ? waveObj.get("infantry").getAsInt() : 0;
            int elite = waveObj.has("elite") ? waveObj.get("elite").getAsInt() : 0;
            int miniboss = waveObj.has("miniboss") ? waveObj.get("miniboss").getAsInt() : 0;
            return new Raid.Wave(infantry, elite, miniboss);
        }
    }
}
