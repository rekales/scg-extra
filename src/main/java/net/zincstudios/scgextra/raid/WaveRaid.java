package net.zincstudios.scgextra.raid;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record WaveRaid(String id, String originalId, Profile profile, List<RaiderEntry> infantry, List<RaiderEntry> elite,
                       List<RaiderEntry> miniboss, List<RaiderEntry> boss) {

    private static final Map<String, WaveRaid> RAIDS = new HashMap<>();  // Key: raid id
    private static final Map<String, WaveRaid> REPLACED_RAIDS = new HashMap<>();  // Key: original raid id

    public static void addWaveRaid(WaveRaid raid) {
        RAIDS.put(raid.id, raid);
        if (!raid.originalId.isEmpty()) {
            RAIDS.put(raid.originalId, raid);
        }
    }

    public static void clearWaveRaids() {
        RAIDS.clear();
        REPLACED_RAIDS.clear();
    }

    public WaveRaid {
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

    public static @Nullable WaveRaid getWaveRaidFromOriginal(String originalId) {
        return REPLACED_RAIDS.get(originalId);
    }

    public static @Nullable WaveRaid getWaveRaid(String id) {
        return RAIDS.get(id);
    }

    public Component getLabel(String raidId) {
        return Component.translatable(SCGExtra.MOD_ID + ".raid.label." + raidId);
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


    private static List<RaiderEntry> sampleRandomRaiders(List<RaiderEntry> entries, int amount, RandomSource random) {
        List<RaiderEntry> spawnList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {  // TODO: weights
            spawnList.add(entries.get(random.nextInt(entries.size())));
        }
        return spawnList;
    }

    public record RaiderEntry(EntityType<? extends Mob> entityType, double maxHealth, double weight) {
        public @Nullable Mob createEntity(ServerLevel level) {
            Mob entity = entityType.create(level);
            if (entity == null) return null;

            if (this.maxHealth != -1) {
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
        }

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
        public int getTotal() {
            return this.infantry + this.elite + this.miniboss;
        }
    }
}
