package net.zincstudios.scgextra.raid;

import net.minecraft.network.chat.Component;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class Raid {
public record Raid(String name, String alias, Profile profile, List<EntityAdjustment> entityAdjustments) {

    private static final Map<String, Raid> raids = new HashMap<>();

    public static void addWaveRaid(Raid raid) {
        raids.put(raid.alias, raid);
    }

    public static @Nullable Raid getWaveRaid(String aliasId) {
        return raids.get(aliasId);
    }

    public static void clearWaveRaids() {
        raids.clear();
    }

    public Component getLabel(String raidId) {
        return Component.translatable(SCGExtra.MOD_ID + ".raid.label." + raidId);
    }

    public record Profile(Wave first, Wave second, Wave third, Wave boss) {
    }

    public record Wave(int infantry, int elite, int miniboss) {
    }

    public record EntityAdjustment(String entityId, double maxHealth) {
    }
}
