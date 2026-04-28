package net.zincstudios.scgextra.raid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import java.util.*;

public class WaveRaidManager {

    public static final double RAID_SPAWN_RADIUS = 15;  // NOTE: turned constant from data, make dynamic if needed

    private static final Map<ResourceLocation, WaveRaidManager> INSTANCES = new HashMap<>();

    @Nullable private WaveRaidState raidState = null;

    public static WaveRaidManager get(ResourceLocation key) {
        return INSTANCES.computeIfAbsent(key, (k) -> new WaveRaidManager());
    }

    public static WaveRaidManager get(ServerLevel level) {
        ResourceLocation dimension = level.dimension().location();
        return INSTANCES.computeIfAbsent(dimension, (k) -> new WaveRaidManager());
    }

    public void startRaid(WaveRaidData raidData, ServerLevel level, Vec3 spawnCenter) {
        ServerPlayer targetPlayer = findNearestPlayer(level, spawnCenter);

        WaveRaidState raid = new WaveRaidState(raidData, level, spawnCenter);
        if (targetPlayer != null) {
            raid.setTargetPlayer(targetPlayer.getUUID());
        }

//        this.currentActiveRaidId = raid.getRaidId();
//        activeRaids.put(raid.getRaidId(), raid);
        spawnCurrentWaveMobs(raid, level);
    }

    // TODO: add more params to make stateless and static
    private void spawnCurrentWaveMobs(WaveRaidState raid, ServerLevel level) {
        Vec3 waveCenter = WaveRaidUtil.findRaidSpawnLocation(level, raid.getSpawnCenter());
        if (waveCenter == null) return;  // TODO: crash or something
        WaveRaidData raidData = raid.getWaveRaidData();
        List<WaveRaidData.RaiderEntry> spawnList = raidData.generateRaiders(raid.getCurrentWave(), level.getRandom());

        int failedAttemptsLeft = spawnList.size()/2 + 5;
        while (!spawnList.isEmpty() && failedAttemptsLeft > 0) {
            for (WaveRaidData.RaiderEntry entry : List.copyOf(spawnList)) {
                Mob mob = entry.createEntity(level);
                if (mob == null) {
                    failedAttemptsLeft--;
                    continue;
                }
                Vec3 pos = WaveRaidUtil.findMobSpawnPos(level, waveCenter, RAID_SPAWN_RADIUS);
                mob.setPos(pos);
                level.addFreshEntity(mob);
                spawnList.remove(entry);
            }
        }

        if (!spawnList.isEmpty()) {
            for (WaveRaidData.RaiderEntry entry : spawnList) {
                SCGExtra.LOGGER.warn("Failed to spawn {}", entry.entityType());
            }
        }
    }

    private static @Nullable ServerPlayer findNearestPlayer(ServerLevel level, Vec3 pos) {
        ServerPlayer nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for(ServerPlayer player : level.players()) {
            if (!player.isSpectator() && !player.isCreative()) {
                double dist = player.position().distanceTo(pos);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = player;
                }
            }
        }

        return nearest;
    }

    public boolean hasActiveRaid() {
        return this.raidState == null;
    }

    public @Nullable WaveRaidState getCurrentRaidState() {
        return this.raidState;
    }
}

