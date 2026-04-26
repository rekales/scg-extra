package net.zincstudios.scgextra.raid;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.EnemyRank;
import net.zincstudios.scgextra.entity.Faction;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

    public boolean verify() {
        Faction faction = Faction.getFaction(this.id);
        if (faction == null) return false;  // TODO: crash cuz no faction
        List<EntityType<?>> factionEntities = Faction.getFactionEntities(faction);

        if (factionEntities.stream().noneMatch(type -> EnemyRank.getEnemyRank(type) == EnemyRank.INFANTRY)) {
            return false;  // TODO: crash cuz no infantry
        }
        if (factionEntities.stream().noneMatch(type -> EnemyRank.getEnemyRank(type) == EnemyRank.ELITE)) {
            return false;  // TODO: crash cuz no elite
        }
        if (factionEntities.stream().noneMatch(type -> EnemyRank.getEnemyRank(type) == EnemyRank.MINIBOSS)) {
            return false;  // TODO: crash cuz no miniboss
        }
        if (factionEntities.stream().noneMatch(type -> EnemyRank.getEnemyRank(type) == EnemyRank.BOSS)) {
            return false;  // TODO: crash cuz no boss
        }

        return true;
    }

    public Component getLabel(String raidId) {
        return Component.translatable(SCGExtra.MOD_ID + ".raid.label." + raidId);
    }

    public List<EntityType<?>> generateSpawnList(int currentWave, RandomSource random) {
        return this.generateSpawnList(this.profile.getWave(currentWave), random);
    }

    public List<EntityType<?>> generateSpawnList(Wave wave, RandomSource random) {
        Faction faction = Faction.getFaction(this.id);
        if (faction == null) return List.of();  // TODO: crash cuz no faction
        List<EntityType<?>> factionEntities = Faction.getFactionEntities(faction);
        List<EntityType<?>> spawnList = new ArrayList<>();
        spawnList.addAll(this.generateRankSpawnList(factionEntities, EnemyRank.INFANTRY, wave.infantry, random));
        spawnList.addAll(this.generateRankSpawnList(factionEntities, EnemyRank.ELITE, wave.elite, random));
        spawnList.addAll(this.generateRankSpawnList(factionEntities, EnemyRank.MINIBOSS, wave.miniboss, random));

        if (wave == this.profile.boss) {
            List<EntityType<?>> bossEntity = this.generateRankSpawnList(factionEntities, EnemyRank.BOSS, 1, random);
            if (bossEntity.isEmpty()) return List.of();  // TODO: crash cuz no boss
            spawnList.addAll(bossEntity);
        }

        return spawnList;  // TODO: crash or something when empty
    }

    private List<EntityType<?>> generateRankSpawnList(List<EntityType<?>> entityTypes, EnemyRank rank, int amount, RandomSource random) {
        List<EntityType<?>> types = entityTypes.stream()
                .filter(type -> EnemyRank.getEnemyRank(type) == rank)
                .toList();
        if (types.isEmpty()) return List.of();  // TODO: decide if warn or ignore

        List<EntityType<?>> spawnList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            spawnList.add(entityTypes.get(random.nextInt(entityTypes.size())));
        }
        return spawnList;
    }

    public record Profile(Wave first, Wave second, Wave third, Wave boss) {
        public Wave getWave(int currentWave) {
            return switch (currentWave) {
                case 2 -> second;
                case 3 -> third;
                case 4 -> boss;
                default -> first;
            };
        }
    }

    public record Wave(int infantry, int elite, int miniboss) {
    }

    public record EntityAdjustment(String entityId, double maxHealth) {
    }
}
