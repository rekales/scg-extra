package net.zincstudios.scgextra.raidwave;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.ForgeRegistries;
import top.ribs.scguns.config.RaidConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RaidWaveTypeUtil {
    public static final int CATEGORY_INFANTRY = 0;
    public static final int CATEGORY_ELITE = 1;
    public static final int CATEGORY_MINIBOSS = 2;

    private RaidWaveTypeUtil() {
    }

    public static RaidConfig.HenchmanType pickTypeForCategory(List<RaidConfig.HenchmanType> types, String raidId, int wantedCategory, RandomSource random) {
        ArrayList<RaidConfig.HenchmanType> matched = new ArrayList<>();
        for (RaidConfig.HenchmanType type : types) {
            if (matchesCategoryForSpawn(raidId, wantedCategory, type)) matched.add(type);
        }
        if (matched.isEmpty()) return null;
        return matched.get(random.nextInt(matched.size()));
    }

    public static boolean matchesCategoryForSpawn(String raidId, int wantedCategory, RaidConfig.HenchmanType type) {
        String canonicalRaidId = canonicalRaidId(raidId);
        if (classifyType(canonicalRaidId, type) == wantedCategory) return true;
        return "whale".equals(canonicalRaidId) && wantedCategory == CATEGORY_ELITE && "salmonsaur".equals(entityPath(type));
    }

    public static RaidConfig.HenchmanType findHenchmanTypeByPath(List<RaidConfig.HenchmanType> types, String wantedPath) {
        if (types == null || types.isEmpty()) return null;
        for (RaidConfig.HenchmanType type : types) {
            if (wantedPath.equals(entityPath(type))) return type;
        }
        return null;
    }

    public static String entityPath(RaidConfig.HenchmanType type) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type.entityType());
        return key == null ? "" : key.getPath();
    }

    public static RaidConfig.HenchmanType pickAnyType(List<RaidConfig.HenchmanType> types, RandomSource random) {
        if (types == null || types.isEmpty()) return null;
        return types.get(random.nextInt(types.size()));
    }

    public static int classifyType(String raidId, RaidConfig.HenchmanType type) {
        String canonicalRaidId = canonicalRaidId(raidId);
        FactionProfile profile = FACTION_PROFILES.get(canonicalRaidId);
        if (profile == null) return fallbackCategory(type);
        String path = entityPath(type);
        if (profile.infantry.contains(path)) return CATEGORY_INFANTRY;
        if (profile.elite.contains(path)) return CATEGORY_ELITE;
        if (profile.miniboss.contains(path)) return CATEGORY_MINIBOSS;
        return fallbackCategory(type);
    }

    public static int fallbackCategory(RaidConfig.HenchmanType type) {
        int aiDifficulty = type.aiDifficulty();
        if (aiDifficulty <= 1) return CATEGORY_INFANTRY;
        if (aiDifficulty == 2) return CATEGORY_ELITE;
        return CATEGORY_MINIBOSS;
    }

    public static String canonicalRaidId(String raidId) {
        if (raidId == null) return "";
        String canonical = RAID_ID_ALIASES.get(raidId);
        return canonical == null ? raidId : canonical;
    }

    private record FactionProfile(Set<String> infantry, Set<String> elite, Set<String> miniboss) {
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static final Map<String, String> RAID_ID_ALIASES;
    private static final Map<String, FactionProfile> FACTION_PROFILES;

    static {
        RAID_ID_ALIASES = new HashMap<>();
        RAID_ID_ALIASES.put("iron", "fac");
        RAID_ID_ALIASES.put("copper", "rrc");
        RAID_ID_ALIASES.put("rcc", "rrc");
        RAID_ID_ALIASES.put("ocean", "whale");
        RAID_ID_ALIASES.put("ocean_whale", "whale");
        RAID_ID_ALIASES.put("whale_whale", "whale");

        FACTION_PROFILES = new HashMap<>();
        FACTION_PROFILES.put(
            "fac",
            new FactionProfile(
                setOf("fac_trencher", "fac_bluecoat", "trench_goblin"),
                setOf("trench_sniper", "shovel_knight", "fac_tank_buster"),
                setOf("fac_lion", "fac_commissar", "fac_walker")
            )
        );
        FACTION_PROFILES.put(
            "rrc",
            new FactionProfile(
                setOf("copper_knight", "tallman", "scout"),
                setOf("spring_junkie", "scrap_guard", "arc_psycho"),
                setOf("oppressor", "drone")
            )
        );
        FACTION_PROFILES.put(
            "whale",
            new FactionProfile(
                setOf("fish_folk"),
                setOf("turtleman", "tentacliator", "glowing_tentacliator", "finforcer"),
                setOf("pufficus")
            )
        );
    }
}
