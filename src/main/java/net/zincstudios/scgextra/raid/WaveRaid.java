package net.zincstudios.scgextra.raid;

import net.minecraft.network.chat.Component;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record WaveRaid(String id, String originalId, Profile profile, List<EntityAdjustment> entityAdjustments) {

    private static final Map<String, WaveRaid> raids = new HashMap<>();  // Key: original raid id

    public static void addWaveRaid(WaveRaid raid) {
        raids.put(raid.originalId, raid);
    }

    public static @Nullable WaveRaid getWaveRaid(String originalId) {
        return raids.get(originalId);
    }

    public static void clearWaveRaids() {
        raids.clear();
    }

    public static String getOriginalId(String originalId) {
        WaveRaid raid = getWaveRaid(originalId);
        if (raid == null) return originalId;
        return raid.id;
    }

    public Component getLabel(String raidId) {
        return Component.translatable(SCGExtra.MOD_ID + ".raid.label." + raidId);
    }

    public record Profile(Wave first, Wave second, Wave third, Wave boss) {
        public Wave getWave(int currentWave) {
            return switch (currentWave) {
                case 1 -> second;
                case 2 -> third;
                case 3 -> boss;
                default -> first;
            };
        }
    }

    public record Wave(int infantry, int elite, int miniboss) {
    }

    public record EntityAdjustment(String entityId, double maxHealth) {
    }
}
