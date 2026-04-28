package net.zincstudios.scgextra.raid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WaveRaidManager {

    public static final double RAID_SPAWN_RADIUS = 15;  // NOTE: turned constant from data, make dynamic if needed

    private static final Map<UUID, WaveRaidState> activeRaids = new HashMap<>();
    private static final Map<ResourceLocation, WaveRaidManager> INSTANCES = new HashMap<>();

    @Nullable private UUID currentActiveRaidId = null;  // NOTE: maybe just have a WaveRaidState object per instance

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

        for (WaveRaidData.RaiderEntry entry : spawnList) {
            Mob mob = entry.createEntity(level);
            if (mob == null) continue;  // TODO: handle
            Vec3 pos = WaveRaidUtil.findMobSpawnPos(level, waveCenter, RAID_SPAWN_RADIUS);
            mob.setPos(pos);
            level.addFreshEntity(mob);
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
        if (this.currentActiveRaidId == null) {
            return false;
        } else {
            WaveRaidState raid = activeRaids.get(this.currentActiveRaidId);
            return raid != null;
        }
    }

    public @Nullable WaveRaidState getCurrentRaidState() {
        return this.currentActiveRaidId == null ? null : activeRaids.get(this.currentActiveRaidId);
    }
}

