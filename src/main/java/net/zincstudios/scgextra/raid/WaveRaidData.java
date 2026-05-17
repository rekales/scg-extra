package net.zincstudios.scgextra.raid;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.ribs.scguns.entity.monster.DissidentEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

// Should entries be a Set instead?
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record WaveRaidData(String id, String originalId, Profile profile, List<RaiderEntry> infantry, List<RaiderEntry> elite,
                           List<RaiderEntry> miniboss, List<RaiderEntry> boss) {

    private static final Map<String, WaveRaidData> RAIDS = new HashMap<>();  // Key: raid id
    private static final Map<String, WaveRaidData> REPLACED_RAIDS = new HashMap<>();  // Key: original raid id

    public static void addWaveRaid(WaveRaidData raid) {
        RAIDS.put(raid.id, raid);
        if (!raid.originalId.isEmpty()) {
            REPLACED_RAIDS.put(raid.originalId, raid);
        }
    }

    public static void clearWaveRaids() {
        RAIDS.clear();
        REPLACED_RAIDS.clear();
    }

    public WaveRaidData {
        if (infantry.isEmpty() || elite.isEmpty() || miniboss.isEmpty() || boss.isEmpty()) {
            throw new IllegalArgumentException("entries cannot be empty");
        }
    }

    public enum Rank {
        INFANTRY, ELITE, MINIBOSS, BOSS
    }

    public List<RaiderEntry> getRaiderEntries(Rank rank) {
        return switch (rank) {
            case INFANTRY -> infantry;
            case ELITE -> elite;
            case MINIBOSS -> miniboss;
            case BOSS -> boss;
        };
    }

    public static @Nullable WaveRaidData getWaveRaidFromOriginal(String originalId) {
        return REPLACED_RAIDS.get(originalId);
    }

    public static @Nullable WaveRaidData getWaveRaid(String id) {
        return RAIDS.get(id);
    }

    public List<RaiderEntry> generateRaiders(int currentWave, RandomSource random) {
        return this.generateRaiders(this.profile.getWave(currentWave), random);
    }

    public List<RaiderEntry> generateRaiders(Wave wave, RandomSource random) {
        List<RaiderEntry> spawnList = new ArrayList<>();
        spawnList.addAll(sampleRandomRaiders(this.getRaiderEntries(Rank.INFANTRY), wave.infantry, random));
        spawnList.addAll(sampleRandomRaiders(this.getRaiderEntries(Rank.ELITE), wave.elite, random));
        spawnList.addAll(sampleRandomRaiders(this.getRaiderEntries(Rank.MINIBOSS), wave.miniboss, random));

        if (wave == this.profile.boss) {
            spawnList.addAll(sampleRandomRaiders(this.getRaiderEntries(Rank.BOSS), 1, random));
        }

        return spawnList;
    }

    private static List<RaiderEntry> sampleRandomRaiders(List<RaiderEntry> entries, int value, RandomSource random) {
        Map<RaiderEntry, Integer> availableEntries = new HashMap<>();  // entry to spawns remaining pair
        for (RaiderEntry entry : entries) {
            availableEntries.put(entry, entry.maxCount);
        }

        List<RaiderEntry> spawnList = new ArrayList<>();
        int SCALE = 10000;  // scale so I can do fractions while not dealing with floating point imprecision
        int valueLeft = value * SCALE;

        while (valueLeft > 0) {
            double totalWeight = availableEntries.keySet().stream()
                    .mapToDouble(e-> e.weight)
                    .sum();

            double randomWeight = random.nextDouble() * totalWeight;
            for (RaiderEntry entry : availableEntries.keySet()) {
                randomWeight -= entry.weight;
                if (randomWeight < 0) {
                    spawnList.add(entry);
                    valueLeft -= (int) (entry.value * SCALE);

                    availableEntries.put(entry, availableEntries.get(entry)-1);
                    if (availableEntries.get(entry) <= 0) {
                        availableEntries.remove(entry);
                    }
                    break;
                }
            }
        }
        return spawnList;
    }

    public record RaiderEntry(EntityType<? extends Mob> entityType, double maxHealth, double weight, double value, int maxCount) {
        public static final int DEFAULT_MAX_SPAWNS = 1000;

        public RaiderEntry {
            if (weight <= 0) {
                throw new IllegalArgumentException("weight cannot zero or lower");
            }
            if (value <= 0) {
                throw new IllegalArgumentException("value cannot zero or lower");
            }
            if (maxCount <= 0) {
                maxCount = DEFAULT_MAX_SPAWNS;
            }
        }

        public @Nullable Mob createEntity(ServerLevel level) {
            Mob entity = entityType.create(level);
            if (entity == null) return null;

            if (this.maxHealth > 0) {
                AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    double currentHealth = healthAttr.getBaseValue();
                    healthAttr.addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "Raid fixed health", this.maxHealth - currentHealth, AttributeModifier.Operation.ADDITION));
                    entity.setHealth(entity.getMaxHealth());
                }
            }

            return entity;
        }
    }

    public record Profile(Wave first, Wave second, Wave third, Wave boss) {
        public Profile {
            if (first.getTotal() == 0 || second.getTotal() == 0 || third.getTotal() == 0) {
                throw new IllegalArgumentException("non-boss waves cannot be empty");
            }
            // TODO: complex check if there's enough max_spawns for the wave.
        }

        public Wave getWave(int currentWave) {
            return switch (currentWave) {
                case 2 -> second;
                case 3 -> third;
                case 4 -> boss;
                default -> first;
            };
        }

        public static boolean isFinalWave(int currentWave) {
            return currentWave == 4;
        }
    }

    public record Wave(int infantry, int elite, int miniboss) {
        public int getTotal() {
            return this.infantry + this.elite + this.miniboss;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean handleRaiderEdgeCase(Mob mob) {
        if (mob instanceof DissidentEntity) {
            return false;
        }

        return true;
    }
}
