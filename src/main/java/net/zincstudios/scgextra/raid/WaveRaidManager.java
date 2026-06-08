package net.zincstudios.scgextra.raid;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;


// TODO: persistence
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WaveRaidManager extends SavedData {

    public static final String SAVED_DATA_NAME = "SCGEWaveRaidData";
    public static final double RAID_SPAWN_RADIUS = 15;  // NOTE: turned constant from data, make dynamic if needed
    public static final double RAID_BOSS_BAR_RADIUS = 128;  // NOTE: turned constant from data, make dynamic if needed

    private static final Map<ResourceLocation, WaveRaidManager> INSTANCES = new HashMap<>();

    @Nullable private WaveRaidState raidState = null;
    @Nullable private ServerBossEvent bossBar = null;

    // NOTE: doesn't check the saved data and might cause inconsistencies, unlikely but possible
    public static WaveRaidManager get(ResourceLocation key) {
        return INSTANCES.computeIfAbsent(key, (k) -> new WaveRaidManager());
    }

    public static WaveRaidManager get(ServerLevel level) {
        ResourceLocation dimension = level.dimension().location();
        if (!INSTANCES.containsKey(dimension)) {
            WaveRaidManager manager = level.getDataStorage().computeIfAbsent(
                    tag->load(level, tag),
                    WaveRaidManager::new,
                    SAVED_DATA_NAME
            );
            INSTANCES.put(dimension, manager);
            return manager;
        } else {
            return INSTANCES.get(dimension);
        }
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
        for(ServerPlayer player : level.getPlayers((player) -> player.position().distanceTo(spawnCenter) <= RAID_BOSS_BAR_RADIUS)) {
            player.sendSystemMessage(raid.getAnnouncement());
        }
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

        updateBossBar(this.raidState, level);
        this.setDirty();
    }

    private void updateBossBar(@Nullable WaveRaidState raid, ServerLevel level) {
        if (raid == null) {
            if (this.bossBar != null) {
                this.bossBar.setProgress(0);
                this.bossBar.setVisible(false);
                this.bossBar = null;
            }
        } else {
            if (this.bossBar == null) {
                this.bossBar = new ServerBossEvent(raid.getBossBarLabel(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
                this.bossBar.setProgress(1);
                this.bossBar.setVisible(true);
            }
            if (!this.bossBar.getName().getString().equals(raid.getBossBarLabel().getString())) {
                this.bossBar.setName(raid.getBossBarLabel());
            }

            this.bossBar.setProgress(raid.getBossBarProgress());

            if (level.getGameTime() % 10 == 1) {
                List<ServerPlayer> nearbyPlayers = level.getPlayers((player) -> {
                    if (player.isAlive() && !player.isRemoved() && !player.isSpectator()) {
                        double distance = player.position().distanceTo(raid.getCenter());
                        return distance <= RAID_BOSS_BAR_RADIUS;
                    } else {
                        return false;
                    }
                });
                // to avoid ConcurrentModificationException, maybe
                Collection<ServerPlayer> bossBarPlayers = new HashSet<>(this.bossBar.getPlayers());
                for(ServerPlayer player : bossBarPlayers) {
                    if (!nearbyPlayers.contains(player) || !player.isAlive() || player.isRemoved()) {
                        this.bossBar.removePlayer(player);
                    }
                }
                for(ServerPlayer player : nearbyPlayers) {
                    if (!bossBarPlayers.contains(player)) {
                        this.bossBar.addPlayer(player);
                    }
                }
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

    public static void onLevelLoad(LevelEvent.Load event) {
        INSTANCES.clear();
    }

    private static void spawnCurrentWaveMobs(WaveRaidState raid, ServerLevel level) {
        Player player = raid.getTargetPlayer();
        Vec3 waveCenter = WaveRaidUtil.findWaveSpawnLocation(level, raid.getCenter(), player == null ? null : player.position());
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
                boolean finalize = raidData.handleRaiderEdgeCase(mob);
                if (finalize) {
                    ForgeEventFactory.onFinalizeSpawn(
                            mob, level,
                            level.getCurrentDifficultyAt(BlockPos.containing(pos)),
                            MobSpawnType.EVENT, null, null
                    );
                }
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

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (this.raidState != null) {
            tag.put("RaidState", this.raidState.serialize());
        }
        return tag;
    }

    public static WaveRaidManager load(ServerLevel level, CompoundTag tag) {
        WaveRaidManager manager = new WaveRaidManager();
        if (tag.contains("RaidState")) {
            manager.raidState = WaveRaidState.deserialize(tag.getCompound("RaidState"), level);
        }
        return manager;
    }
}

