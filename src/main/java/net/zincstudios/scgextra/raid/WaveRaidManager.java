package net.zincstudios.scgextra.raid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.config.RaidConfig;
import top.ribs.scguns.entity.raid.ActiveRaid;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WaveRaidManager {


    public static final double RAID_SPAWN_RADIUS = 15;  // NOTE: turned constant from data, make dynammic if needed
    public static final int RAID_SPAWN_RADIUS_MIN = 40;
    public static final double RAID_SPAWN_RADIUS_MULTIPLIER = 2.0;
    private static final boolean DEBUG_RAID_SPAWN_LOGS = false;

    private static final Map<UUID, ActiveRaid> activeRaids = new HashMap<>();
    private static final Map<ResourceLocation, WaveRaidManager> INSTANCES = new HashMap<>();

    @Nullable private UUID currentActiveRaidId = null;

    public static WaveRaidManager get(ServerLevel level) {
        ResourceLocation dimension = level.dimension().location();
        return INSTANCES.computeIfAbsent(dimension, (k) -> new WaveRaidManager());
    }

    public void startRaid(RaidConfig.RaidData config, WaveRaidData raidData, ServerLevel level, Vec3 spawnPos) {
        ServerPlayer targetPlayer = this.findNearestPlayer(level, spawnPos);
        long startTime = level.getGameTime();

        ActiveWaveRaid raid = new ActiveWaveRaid(config.raidLevel(), config, raidData, level, spawnPos, startTime);
        if (targetPlayer != null) {
            raid.setTargetPlayer(targetPlayer.getUUID());
        }
        activeRaids.put(raid.getRaidId(), raid);
        this.currentActiveRaidId = raid.getRaidId();
        spawnCurrentWaveMobs(raid, level);
    }

    // TODO: add more params to make stateless and static
    private void spawnCurrentWaveMobs(ActiveWaveRaid raid, ServerLevel level) {
        Vec3 waveCenter = WaveRaidUtil.findWaveSpawnLocation(level, raid.getSpawnCenter());
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

    @Nullable
    private ServerPlayer findNearestPlayer(ServerLevel level, Vec3 pos) {
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
            ActiveRaid raid = activeRaids.get(this.currentActiveRaidId);
            return raid != null && raid.isActive();
        }
    }

    @Nullable
    public ActiveWaveRaid getCurrentActiveRaid() {
        return this.currentActiveRaidId == null ? null : (ActiveWaveRaid) activeRaids.get(this.currentActiveRaidId);
    }
}

