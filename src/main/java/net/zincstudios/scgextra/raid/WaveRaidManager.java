package net.zincstudios.scgextra.raid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import java.util.*;


// TODO: persistence
@SuppressWarnings("unused")
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

    public static List<WaveRaidManager> getAll() {
        return INSTANCES.values().stream().toList();
    }

    public void startRaid(WaveRaidData raidData, ServerLevel level, Vec3 spawnCenter) {
        ServerPlayer targetPlayer = WaveRaidUtil.findNearestPlayer(level, spawnCenter);

        WaveRaidState raid = new WaveRaidState(raidData, level, spawnCenter);
        if (targetPlayer != null) {
            raid.setTargetPlayer(targetPlayer);
        }

        this.raidState = raid;
        spawnCurrentWaveMobs(raid, level);
    }

    public void surrenderRaid(ServerLevel level) {
        WaveRaidState raid = this.getCurrentRaidState();
        if (raid == null || raid.hasEnded()) return;
        raid.endRaid();
        this.raidState = null;
    }

    public void tick(ServerLevel level) {
        if (this.raidState != null) {
            this.raidState.tick();

            if (this.raidState.hasEnded()) {
                this.raidState = null;
            } else if (this.raidState.isNextWaveReady()) {
                this.raidState.advanceWave();
                spawnCurrentWaveMobs(this.raidState, level);
            }
        }
    }

    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.level instanceof ServerLevel level) {
                WaveRaidManager manager = WaveRaidManager.get(level);
                manager.tick(level);
            }
        }
    }

    private static void spawnCurrentWaveMobs(WaveRaidState raid, ServerLevel level) {
        Player player = raid.getTargetPlayer();
        Vec3 waveCenter = WaveRaidUtil.findWaveSpawnLocation(level, raid.getSpawnCenter(), player == null ? null : player.position());
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
                mob.setTarget(player);
                level.addFreshEntity(mob);
                raid.addRaider(mob);
                spawnList.remove(entry);
            }
        }

        if (!spawnList.isEmpty()) {
            for (WaveRaidData.RaiderEntry entry : spawnList) {
                SCGExtra.LOGGER.warn("Failed to spawn {}", entry.entityType());
            }
        }
    }

    public boolean hasActiveRaid() {
        return this.raidState != null;
    }

    public @Nullable WaveRaidState getCurrentRaidState() {
        return this.raidState;
    }
}

