package net.zincstudios.scgextra.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.raidwave.RaidWaveBalanceUtil;
import net.zincstudios.scgextra.raidwave.RaidWaveBossBarUtil;
import net.zincstudios.scgextra.raidwave.RaidWaveState;
import net.zincstudios.scgextra.raidwave.RaidWaveTypeUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.config.RaidConfig;
import top.ribs.scguns.entity.raid.ActiveRaid;
import top.ribs.scguns.entity.raid.RaidManager;
import top.ribs.scguns.entity.raid.RaidSaveData;

@Mixin(value = RaidManager.class, remap = false)
public abstract class RaidManagerWaveMixin {
    @Shadow @Final private static Map<UUID, ActiveRaid> activeRaids;
    @Shadow private UUID currentActiveRaidId;

    @Unique private static final Map<UUID, RaidWaveState> SCGEXTRA_WAVE_PROGRESS;
    @Unique private static final Map<UUID, Map<UUID, Integer>> SCGEXTRA_UNRESOLVED_HENCHMEN;
    @Unique private static final Map<UUID, Vec3> SCGEXTRA_FIXED_BOSS_SPAWNS;
    @Unique private static final Map<UUID, Vec3> SCGEXTRA_RAID_ORIGINS;
    @Unique private static final Set<UUID> SCGEXTRA_SESSION_STARTED_STRICT_RAIDS;

    @Unique private static final Map<String, int[][]> WAVE_PLANS;

    @Unique private static final String SCGEXTRA_NO_AUTO_RIDER_TAG = "SCGExtraRaidNoAutoRider";

    @Unique private static final int TOTAL_STRICT_WAVES = 3;
    @Unique private static final int UNRESOLVED_GRACE_TICKS = 6;
    @Unique private static final int UNRESOLVED_REMOVE_TICKS = 60;
    @Unique private static final int WAVE_BLOCKED_RETRY_TICKS = 10;
    @Unique private static final int WAVE_CLEANUP_RETRY_TICKS = 20;
    @Unique private static final int BOSS_RETRY_TICKS = 1;
    @Unique private static final int BOSS_HOLD_SPAWN_TIMER = 0x1FFFFFFF;
    @Unique private static final int GROUP_HALF_SIZE_BLOCKS = 6;
    @Unique private static final int GROUP_MIN_DISTANCE_FROM_ORIGIN = 25;
    @Unique private static final int GROUP_MAX_DISTANCE_FROM_ORIGIN = 50;
    @Unique private static final int GROUP_MIN_DISTANCE_FROM_PLAYER = 25;
    @Unique private static final int GROUP_CENTER_FIND_ATTEMPTS = 24;
    @Unique private static final boolean DEBUG_RAID_SPAWN_LOGS = false;
    @Unique private static final boolean DEBUG_ALLOW_CREATIVE_TARGETS = false;
    @Unique private static final int RAID_SPAWN_RADIUS_MIN = 40;
    @Unique private static final double RAID_SPAWN_RADIUS_MULTIPLIER = 2.0;

    @Inject(method = "onLevelLoad", at = @At("HEAD"))
    private static void scgextra$resetWaveStateOnOverworldLoad(LevelEvent.Load event, CallbackInfo ci) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel level) || level != level.getServer().overworld()) return;
        RaidSaveData raidSaveData = RaidSaveData.get(level);
        Iterator<Map.Entry<UUID, ActiveRaid>> activeRaidIterator = activeRaids.entrySet().iterator();
        while (activeRaidIterator.hasNext()) {
            Map.Entry<UUID, ActiveRaid> entry = activeRaidIterator.next();
            ActiveRaid raid = entry.getValue();
            if (raid != null) {
                raid.setActive(false);
                if (raid.getBossBar() != null) {
                    raid.getBossBar().setVisible(false);
                    raid.getBossBar().removeAllPlayers();
                }
            }
            raidSaveData.removeActiveRaid(entry.getKey());
            activeRaidIterator.remove();
        }
        RaidSaveDataAccessor raidSaveDataAccessor = (RaidSaveDataAccessor) (Object) raidSaveData;
        raidSaveDataAccessor.scgextra$getActiveRaidDataMap().clear();
        raidSaveDataAccessor.scgextra$getScheduledRaidsMap().clear();
        raidSaveDataAccessor.scgextra$getLastRaidDayByDimensionMap().clear();
        raidSaveData.setDirty();
        activeRaids.clear();
        SCGEXTRA_WAVE_PROGRESS.clear();
        SCGEXTRA_UNRESOLVED_HENCHMEN.clear();
        SCGEXTRA_FIXED_BOSS_SPAWNS.clear();
        SCGEXTRA_RAID_ORIGINS.clear();
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS.clear();
    }

    @Inject(method = "startRaid", at = @At("HEAD"), cancellable = true)
    private void scgextra$startStrictRaidWithoutBoss(RaidConfig.RaidData config, ServerLevel level, Vec3 spawnPos, CallbackInfo ci) {
        RaidConfig.RaidData selectedConfig = this.selectRaidConfigVariant(config, level);
        String raidId = this.normalizeRaidId(selectedConfig.raidId());
        if (this.getWavePlans(raidId) == null) return;
        RaidManager manager = (RaidManager) (Object) this;
        if (manager.hasActiveRaid()) {
            ci.cancel();
            return;
        }
        ServerPlayer targetPlayer = this.findNearestPlayer(level, spawnPos);
        ActiveRaid raid = new ActiveRaid(selectedConfig.raidLevel(), selectedConfig, level, spawnPos, level.getGameTime());
        if (targetPlayer != null) raid.setTargetPlayer(targetPlayer.getUUID());
        raid.setBossUUID(raid.getRaidId());
        raid.setBossConfirmed(false);
        activeRaids.put(raid.getRaidId(), raid);
        this.currentActiveRaidId = raid.getRaidId();
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS.add(raid.getRaidId());
        SCGEXTRA_WAVE_PROGRESS.put(raid.getRaidId(), new RaidWaveState());
        SCGEXTRA_UNRESOLVED_HENCHMEN.put(raid.getRaidId(), new HashMap<>());
        SCGEXTRA_RAID_ORIGINS.put(raid.getRaidId(), spawnPos);
        if (DEBUG_RAID_SPAWN_LOGS) {
            SCGExtra.LOGGER.info("[SCGEXTRA RAID] start raidId={} uuid={} origin={} targetPlayer={}",
                raidId, raid.getRaidId(), this.scgextra$fmtVec(spawnPos), targetPlayer == null ? "none" : targetPlayer.getName().getString());
        }
        Vec3 fixedBossPos = this.toStableBossSpawnPos(level, raid, raid.getSpawnCenter());
        SCGEXTRA_FIXED_BOSS_SPAWNS.put(raid.getRaidId(), fixedBossPos);
        String announcement = selectedConfig.spawnConditions().announcementMessage();
        MutableComponent announcementComponent = announcement.startsWith("translation:") ? Component.translatable(announcement.substring(12)) : Component.literal(announcement);
        raid.announceToNearbyPlayers(announcementComponent, selectedConfig.spawnConditions().searchRadius());
        ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(0);
        RaidSaveData.get(level).saveActiveRaid(raid);
        ci.cancel();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ltop/ribs/scguns/entity/raid/RaidManager;checkForNightlyRaidSpawn(Lnet/minecraft/server/level/ServerLevel;)V"))
    private void scgextra$disableNightlyAutoRaid(RaidManager instance, ServerLevel level) {
    }

    @Inject(method = "tickActiveRaids", at = @At("HEAD"))
    private void scgextra$removeRestoredStrictRaids(ServerLevel level, CallbackInfo ci) {
    }

    @Redirect(method = "tickActiveRaids", at = @At(value = "INVOKE", target = "Ltop/ribs/scguns/entity/raid/ActiveRaid;tick()V"))
    private void scgextra$redirectRaidTick(ActiveRaid raid, ServerLevel level) {
        String raidId = this.normalizeRaidId(raid.getConfig().raidId());
        if (this.getWavePlans(raidId) == null) {
            raid.tick();
            return;
        }
        RaidWaveState state = this.ensureState(raid.getRaidId());
        if (state.isBossReleased()) {
            raid.tick();
            return;
        }
        int timer = ((ActiveRaidAccessor) raid).scgextra$getSpawnTimer();
        if (timer > 0) ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(timer - 1);
    }

    @Redirect(method = "tickActiveRaids", at = @At(value = "INVOKE", target = "Ltop/ribs/scguns/entity/raid/ActiveRaid;shouldSpawnHenchmen()Z"))
    private boolean scgextra$redirectShouldSpawnHenchmen(ActiveRaid raid) {
        String raidId = this.normalizeRaidId(raid.getConfig().raidId());
        if (this.getWavePlans(raidId) == null) return raid.shouldSpawnHenchmen();
        RaidWaveState state = this.ensureState(raid.getRaidId());
        if (state.isBossReleased()) return false;
        int timer = ((ActiveRaidAccessor) raid).scgextra$getSpawnTimer();
        return raid.isActive() && timer <= 0;
    }

    @Inject(method = "tickActiveRaids", at = @At("TAIL"))
    private void scgextra$cleanupWaveProgress(ServerLevel level, CallbackInfo ci) {
        SCGEXTRA_WAVE_PROGRESS.entrySet().removeIf(entry -> {
            ActiveRaid raid = activeRaids.get(entry.getKey());
            return raid == null || !raid.isActive();
        });
        SCGEXTRA_UNRESOLVED_HENCHMEN.entrySet().removeIf(entry -> {
            ActiveRaid raid = activeRaids.get(entry.getKey());
            return raid == null || !raid.isActive();
        });
        SCGEXTRA_FIXED_BOSS_SPAWNS.entrySet().removeIf(entry -> {
            ActiveRaid raid = activeRaids.get(entry.getKey());
            return raid == null || !raid.isActive();
        });
        SCGEXTRA_RAID_ORIGINS.entrySet().removeIf(entry -> {
            ActiveRaid raid = activeRaids.get(entry.getKey());
            return raid == null || !raid.isActive();
        });
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS.removeIf(uuid -> {
            ActiveRaid raid = activeRaids.get(uuid);
            return raid == null || !raid.isActive();
        });
        for (Map.Entry<UUID, ActiveRaid> entry : activeRaids.entrySet()) {
            ActiveRaid raid = entry.getValue();
            if (raid == null || !raid.isActive()) continue;
            String raidId = this.normalizeRaidId(raid.getConfig().raidId());
            int[][] plans = this.getWavePlans(raidId);
            if (plans == null) continue;
            RaidWaveState state = this.ensureState(raid.getRaidId());
            int aliveHenchmen = this.getAliveRaidHenchmenCount(raid, level);
            RaidWaveBossBarUtil.updateWaveBossBar(raid, raidId, plans, state, aliveHenchmen, TOTAL_STRICT_WAVES);
            RaidWaveBossBarUtil.syncWaveBossBarPlayers(level, raid);
            if (!state.isBossReleased() && aliveHenchmen == 0) {
                int timer = ((ActiveRaidAccessor) raid).scgextra$getSpawnTimer();
                if (timer > 0) ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(0);
            }
        }
    }

    @Inject(method = "spawnHenchmen", at = @At("HEAD"), cancellable = true)
    private void scgextra$spawnStrictWaves(ActiveRaid raid, ServerLevel level, CallbackInfo ci) {
        String raidId = this.normalizeRaidId(raid.getConfig().raidId());
        int[][] plans = this.getWavePlans(raidId);
        if (plans == null) return;
        ci.cancel();
        if (!raid.isActive()) {
            this.clearWaveState(raid.getRaidId());
            return;
        }
        RaidWaveState state = this.ensureState(raid.getRaidId());
        if (state.isBossReleased()) return;
        int aliveNow = this.getAliveRaidHenchmenCount(raid, level);
        int waveIndex = state.getWaveIndex();
        if (waveIndex >= TOTAL_STRICT_WAVES) {
            if (aliveNow == 0) {
                this.releaseBossForFinalWave(raid, level, state);
            } else {
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(WAVE_BLOCKED_RETRY_TICKS);
            }
            return;
        }
        int[] currentPlan = plans[waveIndex];
        if (state.isCurrentWaveComplete(currentPlan)) {
            if (aliveNow > 0) {
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(WAVE_CLEANUP_RETRY_TICKS);
                return;
            }
            this.advanceToNextWave(state, raid);
            if (state.getWaveIndex() >= TOTAL_STRICT_WAVES) {
                this.releaseBossForFinalWave(raid, level, state);
            } else {
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(0);
            }
            return;
        }
        this.spawnWaveHenchmen(raid, level, raidId, state, currentPlan);
        if (state.isCurrentWaveComplete(currentPlan)) {
            if (this.getAliveRaidHenchmenCount(raid, level) > 0) {
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(WAVE_CLEANUP_RETRY_TICKS);
            } else {
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(0);
            }
        } else {
            ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(WAVE_BLOCKED_RETRY_TICKS);
        }
    }

    @Unique
    private void spawnWaveHenchmen(ActiveRaid raid, ServerLevel level, String raidId, RaidWaveState state, int[] currentPlan) {
        RaidConfig.HenchmenData henchmenData = raid.getConfig().henchmen();
        if (henchmenData == null || henchmenData.types() == null || henchmenData.types().isEmpty()) return;
        RaidManagerInvoker invoker = (RaidManagerInvoker) (Object) this;
        RandomSource random = level.getRandom();
        Vec3 raidOrigin = SCGEXTRA_RAID_ORIGINS.getOrDefault(raid.getRaidId(), raid.getSpawnCenter());
        Vec3 groupCenter = this.findWaveGroupCenter(invoker, raid, level, raidOrigin, henchmenData, random);
        if (groupCenter == null) {
            if (DEBUG_RAID_SPAWN_LOGS) {
                SCGExtra.LOGGER.warn("[SCGEXTRA RAID] no group center raidId={} uuid={} origin={} wave={}",
                    raidId, raid.getRaidId(), this.scgextra$fmtVec(raidOrigin), state.getWaveIndex() + 1);
            }
            return;
        }
        if (DEBUG_RAID_SPAWN_LOGS) {
            SCGExtra.LOGGER.info("[SCGEXTRA RAID] group center selected raidId={} uuid={} center={} dist={}m",
                raidId, raid.getRaidId(), this.scgextra$fmtVec(groupCenter),
                this.scgextra$fmtDistance(this.scgextra$distance2D(groupCenter, raidOrigin)));
        }
        int remainingInWave = state.getRemainingInWave(currentPlan);
        if (remainingInWave <= 0) return;
        int maxAttempts = Math.max(remainingInWave * 12, Math.max(1, henchmenData.spawnAttemptsPerWave()) * 4);
        int attempts = 0;
        while (!state.isCurrentWaveComplete(currentPlan) && attempts < maxAttempts) {
            attempts++;
            int aliveBeforeSpawn = this.getAliveRaidHenchmenCount(raid, level);
            if (aliveBeforeSpawn >= Math.max(1, henchmenData.maxAlive())) break;
            int category = state.nextCategoryToSpawn(currentPlan, random);
            if (category < 0) break;
            RaidConfig.HenchmanType henchmanType = RaidWaveTypeUtil.pickTypeForCategory(henchmenData.types(), raidId, category, random);
            if (henchmanType == null) {
                henchmanType = RaidWaveTypeUtil.pickAnyType(henchmenData.types(), random);
                if (henchmanType == null) break;
            }
            Vec3 spawnPos = this.findSpawnPosInWaveGroup(invoker, raid, level, groupCenter, raidOrigin, random);
            if (spawnPos == null) continue;
            if (DEBUG_RAID_SPAWN_LOGS) {
                SCGExtra.LOGGER.debug("[SCGEXTRA RAID] spawn point accepted raidId={} uuid={} pos={} dist={}m",
                    raidId, raid.getRaidId(), this.scgextra$fmtVec(spawnPos),
                    this.scgextra$fmtDistance(this.scgextra$distance2D(spawnPos, raidOrigin)));
            }
            Mob henchman = invoker.scgextra$invokeSpawnHenchman(raid, henchmanType, level, spawnPos);
            if (henchman == null) continue;
            if (DEBUG_RAID_SPAWN_LOGS) {
                Vec3 actualPos = henchman.position();
                double requestedDist = this.scgextra$distance2D(spawnPos, raidOrigin);
                double actualDist = this.scgextra$distance2D(actualPos, raidOrigin);
                double shift = this.scgextra$distance2D(actualPos, spawnPos);
                SCGExtra.LOGGER.info(
                    "[SCGEXTRA RAID] spawned entity={} raidId={} uuid={} requestedPos={} requestedDist={}m actualPos={} actualDist={}m shift={}m",
                    henchman.getType().toShortString(),
                    raidId,
                    raid.getRaidId(),
                    this.scgextra$fmtVec(spawnPos),
                    this.scgextra$fmtDistance(requestedDist),
                    this.scgextra$fmtVec(actualPos),
                    this.scgextra$fmtDistance(actualDist),
                    this.scgextra$fmtDistance(shift)
                );
            }
            if (!this.prepareSpawnedHenchmanForRole(invoker, raid, level, henchmenData, raidId, category, henchmanType, henchman)) {
                henchman.remove(Entity.RemovalReason.DISCARDED);
                continue;
            }
            raid.addHenchman(henchman.getUUID());
            state.onSpawned(category);
        }
    }

    @Unique
    private void advanceToNextWave(RaidWaveState state, ActiveRaid raid) {
        state.advanceWave();
        this.ensureUnresolvedHenchmenMap(raid.getRaidId()).clear();
    }

    @Unique
    private void releaseBossForFinalWave(ActiveRaid raid, ServerLevel level, RaidWaveState state) {
        if (state.isBossReleased()) return;
        if (this.getAliveRaidHenchmenCount(raid, level) > 0) {
            ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(WAVE_BLOCKED_RETRY_TICKS);
            return;
        }
        LivingEntity boss = raid.getBoss();
        if (boss == null || !boss.isAlive()) {
            Mob spawnedBoss = this.spawnBossWithFallback(raid, level);
            if (spawnedBoss == null) {
                state.incrementBossRetryCount();
                ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(BOSS_RETRY_TICKS);
                return;
            }
            raid.setBossUUID(spawnedBoss.getUUID());
            Entity mount = spawnedBoss.getVehicle();
            if (mount != null) raid.setMountUUID(mount.getUUID());
            ServerPlayer targetPlayer = raid.getTargetPlayer(level);
            if (this.isValidRaidTarget(targetPlayer) && spawnedBoss instanceof PathfinderMob pathfinderMob) pathfinderMob.setTarget(targetPlayer);
            spawnedBoss.setDeltaMovement(Vec3.ZERO);
            boss = spawnedBoss;
        }
        LivingEntity trackedBoss = raid.getBoss();
        if (trackedBoss == null || !trackedBoss.isAlive()) {
            state.incrementBossRetryCount();
            ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(BOSS_RETRY_TICKS);
            return;
        }
        boss = trackedBoss;
        this.prepareReleasedBoss(raid, boss, state);
    }
    @Unique
    private Mob spawnBossWithFallback(ActiveRaid raid, ServerLevel level) {
        RaidManagerInvoker invoker = (RaidManagerInvoker) (Object) this;
        RandomSource random = level.getRandom();
        Vec3 fixedPos = SCGEXTRA_FIXED_BOSS_SPAWNS.computeIfAbsent(raid.getRaidId(), key -> this.toStableBossSpawnPos(level, raid, raid.getSpawnCenter()));
        ArrayList<Vec3> candidates = new ArrayList<>();
        RaidWaveBalanceUtil.addSpawnCandidate(candidates, fixedPos);
        RaidWaveBalanceUtil.addSpawnCandidate(candidates, this.toStableBossSpawnPos(level, raid, raid.getSpawnCenter()));
        ServerPlayer anchorPlayer = raid.getTargetPlayer(level);
        if (anchorPlayer != null) {
            RaidWaveBalanceUtil.addSpawnCandidate(candidates, this.toStableBossSpawnPos(level, raid, anchorPlayer.position()));
        }
        int radius = 16;
        RaidConfig.HenchmenData henchmenData = raid.getConfig().henchmen();
        if (henchmenData != null) radius = RaidWaveBalanceUtil.scaledRaidSpawnRadius(henchmenData, RAID_SPAWN_RADIUS_MIN, RAID_SPAWN_RADIUS_MULTIPLIER);
        Vec3 center = raid.getSpawnCenter();
        for (int i = 0; i < 8; i++) RaidWaveBalanceUtil.addSpawnCandidate(candidates, invoker.scgextra$invokeFindHenchmanSpawnPos(level, center, radius));
        for (int i = 0; i < 12; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = 2.0 + random.nextDouble() * Math.max(6.0, radius);
            Vec3 probe = new Vec3(center.x + Math.cos(angle) * distance, center.y, center.z + Math.sin(angle) * distance);
            RaidWaveBalanceUtil.addSpawnCandidate(candidates, invoker.scgextra$invokeFindHenchmanSpawnPos(level, probe, Math.max(4, radius / 2)));
        }
        for (Vec3 candidate : candidates) {
            Mob spawnedBoss = invoker.scgextra$invokeSpawnBoss(raid, level, candidate);
            if (spawnedBoss == null) continue;
            Entity tracked = level.getEntity(spawnedBoss.getUUID());
            if (!(tracked instanceof Mob trackedMob) || !trackedMob.isAlive()) continue;
            SCGEXTRA_FIXED_BOSS_SPAWNS.put(raid.getRaidId(), candidate);
            return trackedMob;
        }
        return null;
    }

    @Unique
    private void prepareReleasedBoss(ActiveRaid raid, LivingEntity boss, RaidWaveState state) {
        raid.setBossUUID(boss.getUUID());
        boss.setInvisible(false);
        boss.setInvulnerable(false);
        boss.setNoGravity(false);
        boss.setSilent(false);
        boss.setCustomNameVisible(true);
        if (boss instanceof Mob mobBoss) {
            mobBoss.setNoAi(false);
            mobBoss.addTag("RaidBoss");
            mobBoss.addTag("RaidMobRaid_" + raid.getRaidId());
        }
        RaidWaveBalanceUtil.applyRaidBossHealthWithoutMultiplier(boss);
        raid.setBossConfirmed(true);
        state.setBossReleased(TOTAL_STRICT_WAVES);
        state.clearBossRetryCount();
        SCGEXTRA_UNRESOLVED_HENCHMEN.remove(raid.getRaidId());
        SCGEXTRA_FIXED_BOSS_SPAWNS.remove(raid.getRaidId());
        ((ActiveRaidAccessor) raid).scgextra$setSpawnTimer(BOSS_HOLD_SPAWN_TIMER);
        raid.updateBossBarPlayers();
    }

    @Unique
    private void clearWaveState(UUID raidId) {
        SCGEXTRA_WAVE_PROGRESS.remove(raidId);
        SCGEXTRA_UNRESOLVED_HENCHMEN.remove(raidId);
        SCGEXTRA_FIXED_BOSS_SPAWNS.remove(raidId);
        SCGEXTRA_RAID_ORIGINS.remove(raidId);
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS.remove(raidId);
    }
    @Unique
    private Vec3 toStableBossSpawnPos(ServerLevel level, ActiveRaid raid, Vec3 preferredCenter) {
        Vec3 center = preferredCenter;
        ServerPlayer targetPlayer = raid.getTargetPlayer(level);
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        if (center.y <= minY + 4 || center.y >= maxY - 4) {
            if (targetPlayer != null) {
                center = targetPlayer.position();
            } else {
                center = raid.getSpawnCenter();
            }
        }
        int x = Mth.floor(center.x);
        int z = Mth.floor(center.z);
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (surfaceY <= minY + 1 && targetPlayer != null) {
            x = Mth.floor(targetPlayer.getX());
            z = Mth.floor(targetPlayer.getZ());
            surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        }
        double y = Math.max(center.y, surfaceY + 1.0);
        y = Mth.clamp(y, minY + 2.0, maxY - 2.0);
        return new Vec3(x + 0.5, y, z + 0.5);
    }

    @Unique
    private Vec3 findSpawnPosWithFallback(RaidManagerInvoker invoker, ActiveRaid raid, ServerLevel level, Vec3 center, int radius, RandomSource random) {
        Vec3 pos = invoker.scgextra$invokeFindHenchmanSpawnPos(level, center, radius);
        if (pos != null) return pos;
        for (int i = 0; i < 8; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = 2.0 + random.nextDouble() * 4.0;
            Vec3 probe = new Vec3(center.x + Math.cos(angle) * distance, center.y, center.z + Math.sin(angle) * distance);
            pos = invoker.scgextra$invokeFindHenchmanSpawnPos(level, probe, Math.max(4, radius / 2));
            if (pos != null) return pos;
        }
        return null;
    }

    @Unique
    private Vec3 findWaveGroupCenter(
        RaidManagerInvoker invoker,
        ActiveRaid raid,
        ServerLevel level,
        Vec3 raidOrigin,
        RaidConfig.HenchmenData henchmenData,
        RandomSource random
    ) {
        int raidSpawnRadius = RaidWaveBalanceUtil.scaledRaidSpawnRadius(henchmenData, RAID_SPAWN_RADIUS_MIN, RAID_SPAWN_RADIUS_MULTIPLIER);
        int outerRadius = Math.max(GROUP_MAX_DISTANCE_FROM_ORIGIN, raidSpawnRadius);
        for (int i = 0; i < GROUP_CENTER_FIND_ATTEMPTS; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = GROUP_MIN_DISTANCE_FROM_ORIGIN + random.nextDouble() * (GROUP_MAX_DISTANCE_FROM_ORIGIN - GROUP_MIN_DISTANCE_FROM_ORIGIN);
            Vec3 probe = new Vec3(
                raidOrigin.x + Math.cos(angle) * distance,
                raidOrigin.y,
                raidOrigin.z + Math.sin(angle) * distance
            );
            Vec3 center = this.findSpawnPosWithFallback(invoker, raid, level, probe, GROUP_HALF_SIZE_BLOCKS, random);
            if (this.isWithinGroupBand(center, raidOrigin) && this.isOutsidePlayerSafetyRadius(level, center)) return center;
            if (DEBUG_RAID_SPAWN_LOGS && i < 8) {
                SCGExtra.LOGGER.debug("[SCGEXTRA RAID] group center reject (ring attempt {}) raid={} uuid={} probe={} got={} dist={} nearestPlayerDist={}",
                    i + 1, raid.getConfig().raidId(), raid.getRaidId(),
                    this.scgextra$fmtVec(probe), this.scgextra$fmtVec(center),
                    center == null ? "null" : this.scgextra$fmtDistance(this.scgextra$distance2D(center, raidOrigin)),
                    center == null ? "null" : this.scgextra$fmtDistance(this.nearestValidPlayerDistance2D(level, center)));
            }
        }
        for (int i = 0; i < 8; i++) {
            Vec3 fallback = this.findSpawnPosWithFallback(invoker, raid, level, raidOrigin, outerRadius, random);
            if (this.isWithinGroupBand(fallback, raidOrigin) && this.isOutsidePlayerSafetyRadius(level, fallback)) return fallback;
            if (DEBUG_RAID_SPAWN_LOGS) {
                SCGExtra.LOGGER.debug("[SCGEXTRA RAID] group center reject (fallback {}) raid={} uuid={} got={} dist={} nearestPlayerDist={}",
                    i + 1, raid.getConfig().raidId(), raid.getRaidId(),
                    this.scgextra$fmtVec(fallback),
                    fallback == null ? "null" : this.scgextra$fmtDistance(this.scgextra$distance2D(fallback, raidOrigin)),
                    fallback == null ? "null" : this.scgextra$fmtDistance(this.nearestValidPlayerDistance2D(level, fallback)));
            }
        }
        return null;
    }

    @Unique
    private Vec3 findSpawnPosInWaveGroup(
        RaidManagerInvoker invoker,
        ActiveRaid raid,
        ServerLevel level,
        Vec3 groupCenter,
        Vec3 raidOrigin,
        RandomSource random
    ) {
        for (int i = 0; i < 12; i++) {
            double x = groupCenter.x + (random.nextDouble() * 2.0 - 1.0) * GROUP_HALF_SIZE_BLOCKS;
            double z = groupCenter.z + (random.nextDouble() * 2.0 - 1.0) * GROUP_HALF_SIZE_BLOCKS;
            Vec3 probe = new Vec3(x, groupCenter.y, z);
            Vec3 pos = this.findSpawnPosWithFallback(invoker, raid, level, probe, 3, random);
            if (pos == null) continue;
            if (!this.isWithinGroupBand(pos, raidOrigin)) {
                if (DEBUG_RAID_SPAWN_LOGS && i < 4) {
                    SCGExtra.LOGGER.debug("[SCGEXTRA RAID] spawn reject band uuid={} pos={} dist={}",
                        raid.getRaidId(), this.scgextra$fmtVec(pos), this.scgextra$fmtDistance(this.scgextra$distance2D(pos, raidOrigin)));
                }
                continue;
            }
            if (!this.isOutsidePlayerSafetyRadius(level, pos)) {
                if (DEBUG_RAID_SPAWN_LOGS && i < 4) {
                    SCGExtra.LOGGER.debug("[SCGEXTRA RAID] spawn reject player-radius uuid={} pos={} nearestPlayerDist={} minRequired={}m",
                        raid.getRaidId(),
                        this.scgextra$fmtVec(pos),
                        this.scgextra$fmtDistance(this.nearestValidPlayerDistance2D(level, pos)),
                        GROUP_MIN_DISTANCE_FROM_PLAYER);
                }
                continue;
            }
            if (Math.abs(pos.x - groupCenter.x) > GROUP_HALF_SIZE_BLOCKS || Math.abs(pos.z - groupCenter.z) > GROUP_HALF_SIZE_BLOCKS) {
                if (DEBUG_RAID_SPAWN_LOGS && i < 4) {
                    SCGExtra.LOGGER.debug("[SCGEXTRA RAID] spawn reject box uuid={} pos={} center={}",
                        raid.getRaidId(), this.scgextra$fmtVec(pos), this.scgextra$fmtVec(groupCenter));
                }
                continue;
            }
            return pos;
        }
        Vec3 fallback = this.findSpawnPosWithFallback(invoker, raid, level, groupCenter, GROUP_HALF_SIZE_BLOCKS, random);
        if (!this.isWithinGroupBand(fallback, raidOrigin)) return null;
        if (!this.isOutsidePlayerSafetyRadius(level, fallback)) return null;
        if (Math.abs(fallback.x - groupCenter.x) > GROUP_HALF_SIZE_BLOCKS) return null;
        if (Math.abs(fallback.z - groupCenter.z) > GROUP_HALF_SIZE_BLOCKS) return null;
        return fallback;
    }

    @Unique
    private boolean isWithinGroupBand(Vec3 pos, Vec3 origin) {
        if (pos == null || origin == null) return false;
        double dx = pos.x - origin.x;
        double dz = pos.z - origin.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return dist >= GROUP_MIN_DISTANCE_FROM_ORIGIN && dist <= GROUP_MAX_DISTANCE_FROM_ORIGIN;
    }

    @Unique
    private boolean isOutsidePlayerSafetyRadius(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) return false;
        for (ServerPlayer player : level.players()) {
            if (!this.isValidRaidTarget(player)) continue;
            if (this.scgextra$distance2D(pos, player.position()) < GROUP_MIN_DISTANCE_FROM_PLAYER) return false;
        }
        return true;
    }

    @Unique
    private double nearestValidPlayerDistance2D(ServerLevel level, Vec3 pos) {
        if (level == null || pos == null) return -1.0;
        double nearest = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            if (!this.isValidRaidTarget(player)) continue;
            double dist = this.scgextra$distance2D(pos, player.position());
            if (dist >= 0.0 && dist < nearest) nearest = dist;
        }
        return nearest == Double.MAX_VALUE ? -1.0 : nearest;
    }

    @Unique
    private double scgextra$distance2D(Vec3 a, Vec3 b) {
        if (a == null || b == null) return -1.0;
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    @Unique
    private String scgextra$fmtDistance(double value) {
        if (value < 0.0) return "n/a";
        return String.format("%.2f", value);
    }

    @Unique
    private String scgextra$fmtVec(Vec3 vec) {
        if (vec == null) return "null";
        return String.format("(%.2f, %.2f, %.2f)", vec.x, vec.y, vec.z);
    }

    @Unique
    private boolean prepareSpawnedHenchmanForRole(
        RaidManagerInvoker invoker,
        ActiveRaid raid,
        ServerLevel level,
        RaidConfig.HenchmenData henchmenData,
        String raidId,
        int requestedCategory,
        RaidConfig.HenchmanType spawnedType,
        Mob henchman
    ) {
        if (!"whale".equals(raidId)) return true;
        if (!"salmonsaur".equals(RaidWaveTypeUtil.entityPath(spawnedType))) return true;
        henchman.addTag(SCGEXTRA_NO_AUTO_RIDER_TAG);
        if (requestedCategory == RaidWaveState.CATEGORY_ELITE) {
            return this.attachWhalerEliteRider(invoker, raid, level, henchmenData, henchman);
        }
        if (requestedCategory == RaidWaveState.CATEGORY_INFANTRY) {
            ArrayList<Entity> passengers = new ArrayList<>(henchman.getPassengers());
            for (Entity passenger : passengers) {
                passenger.stopRiding();
                passenger.remove(Entity.RemovalReason.DISCARDED);
                raid.removeHenchman(passenger.getUUID());
            }
        }
        return true;
    }

    @Unique
    private boolean attachWhalerEliteRider(
        RaidManagerInvoker invoker,
        ActiveRaid raid,
        ServerLevel level,
        RaidConfig.HenchmenData henchmenData,
        Mob salmonsaur
    ) {
        if (!salmonsaur.getPassengers().isEmpty()) return true;
        RaidConfig.HenchmanType riderType = RaidWaveTypeUtil.findHenchmanTypeByPath(henchmenData.types(), "fish_folk");
        if (riderType == null) return false;
        Mob rider = invoker.scgextra$invokeSpawnHenchman(raid, riderType, level, salmonsaur.position());
        if (rider == null) return false;
        if (!rider.startRiding(salmonsaur, true)) {
            rider.remove(Entity.RemovalReason.DISCARDED);
            return false;
        }
        raid.addHenchman(rider.getUUID());
        return true;
    }

    @Unique
    private int[][] getWavePlans(String raidId) {
        return WAVE_PLANS.get(raidId);
    }

    @Unique
    private RaidWaveState ensureState(UUID raidUuid) {
        return SCGEXTRA_WAVE_PROGRESS.computeIfAbsent(raidUuid, id -> new RaidWaveState());
    }

    @Unique
    private Map<UUID, Integer> ensureUnresolvedHenchmenMap(UUID raidUuid) {
        return SCGEXTRA_UNRESOLVED_HENCHMEN.computeIfAbsent(raidUuid, id -> new HashMap<>());
    }

    @Unique
    private int getAliveRaidHenchmenCount(ActiveRaid raid, ServerLevel level) {
        Set<UUID> tracked = raid.getHenchmenUUIDs();
        if (tracked == null || tracked.isEmpty()) {
            this.ensureUnresolvedHenchmenMap(raid.getRaidId()).clear();
            return 0;
        }
        Set<UUID> snapshot = new HashSet<>(tracked);
        Map<UUID, Integer> unresolvedMap = this.ensureUnresolvedHenchmenMap(raid.getRaidId());
        int alive = 0;
        for (UUID uuid : snapshot) {
            Entity entity = level.getEntity(uuid);
            if (entity instanceof LivingEntity living) {
                if (living.isAlive()) {
                    alive++;
                    unresolvedMap.remove(uuid);
                } else {
                    raid.removeHenchman(uuid);
                    unresolvedMap.remove(uuid);
                }
                continue;
            }
            if (entity != null) {
                raid.removeHenchman(uuid);
                unresolvedMap.remove(uuid);
                continue;
            }
            int unresolvedTicks = unresolvedMap.getOrDefault(uuid, 0) + 1;
            unresolvedMap.put(uuid, unresolvedTicks);
            if (unresolvedTicks <= UNRESOLVED_GRACE_TICKS) {
                alive++;
            } else if (unresolvedTicks >= UNRESOLVED_REMOVE_TICKS) {
                raid.removeHenchman(uuid);
                unresolvedMap.remove(uuid);
            }
        }
        unresolvedMap.entrySet().removeIf(entry -> !snapshot.contains(entry.getKey()));
        return alive;
    }

    @Inject(method = "onServerStopping", at = @At("TAIL"))
    private static void scgextra$clearWaveSessionOnStop(ServerStoppingEvent event, CallbackInfo ci) {
        SCGEXTRA_WAVE_PROGRESS.clear();
        SCGEXTRA_UNRESOLVED_HENCHMEN.clear();
        SCGEXTRA_FIXED_BOSS_SPAWNS.clear();
        SCGEXTRA_RAID_ORIGINS.clear();
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS.clear();
    }

    @Unique
    private ServerPlayer findNearestPlayer(ServerLevel level, Vec3 pos) {
        ServerPlayer nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            if (!this.isValidRaidTarget(player)) continue;
            double dist = player.position().distanceToSqr(pos);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = player;
            }
        }
        return nearest;
    }

    @Unique
    private boolean isValidRaidTarget(ServerPlayer player) {
        if (player == null || !player.isAlive() || player.isSpectator()) return false;
        if (DEBUG_ALLOW_CREATIVE_TARGETS) return true;
        return !player.isCreative();
    }

    @Unique
    private RaidConfig.RaidData selectRaidConfigVariant(RaidConfig.RaidData inputConfig, ServerLevel level) {
        String raidId = this.normalizeRaidId(inputConfig.raidId());
        if (!"whale".equals(raidId)) return inputConfig;
        RaidConfig.RaidData whaleConfig = RaidConfig.getRaidById("whale_whale");
        if (whaleConfig == null) whaleConfig = RaidConfig.getRaidById("ocean_whale");
        return whaleConfig != null ? whaleConfig : inputConfig;
    }

    @Unique
    private String normalizeRaidId(String raidId) {
        return RaidWaveTypeUtil.canonicalRaidId(raidId);
    }

    static {
        SCGEXTRA_WAVE_PROGRESS = new HashMap<>();
        SCGEXTRA_UNRESOLVED_HENCHMEN = new HashMap<>();
        SCGEXTRA_FIXED_BOSS_SPAWNS = new HashMap<>();
        SCGEXTRA_RAID_ORIGINS = new HashMap<>();
        SCGEXTRA_SESSION_STARTED_STRICT_RAIDS = new HashSet<>();
        WAVE_PLANS = new HashMap<>();
        WAVE_PLANS.put("fac", new int[][] {{8, 0, 0}, {5, 2, 0}, {3, 3, 1}});
        WAVE_PLANS.put("rrc", new int[][] {{4, 0, 0}, {3, 1, 0}, {2, 2, 1}});
        WAVE_PLANS.put("whale", new int[][] {{7, 0, 0}, {2, 4, 0}, {2, 4, 1}});
    }
}
