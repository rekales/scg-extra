package net.zincstudios.scgextra.raid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
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

    private static final Map<UUID, ActiveRaid> activeRaids = new HashMap<>();
    private static final Map<ResourceLocation, WaveRaidManager> INSTANCES = new HashMap<>();

    @Nullable private UUID currentActiveRaidId = null;
    private WaveRaid currentRaidData;
    private int currentWave = 1;

    public static WaveRaidManager get(ServerLevel level) {
        ResourceLocation dimension = level.dimension().location();
        return INSTANCES.computeIfAbsent(dimension, (k) -> new WaveRaidManager());
    }

    public void startRaid(RaidConfig.RaidData config, WaveRaid raidData, ServerLevel level, Vec3 spawnPos) {
        ServerPlayer targetPlayer = this.findNearestPlayer(level, spawnPos);
        long startTime = level.getGameTime();

        ActiveRaid raid = new ActiveRaid(config.raidLevel(), config, level, spawnPos, startTime);
        if (targetPlayer != null) {
            raid.setTargetPlayer(targetPlayer.getUUID());
        }
        activeRaids.put(raid.getRaidId(), raid);
        this.currentActiveRaidId = raid.getRaidId();
        this.currentWave = 1;
        this.currentRaidData = raidData;
//        spawnCurrentWaveMobs(level);
    }

//    // TODO: add more params to make stateless and static
//    private void spawnCurrentWaveMobs(ServerLevel level) {
//        List<EntityType<?>> spawnList = this.currentRaidData.generateSpawnList(this.currentWave, level.getRandom());
//        int failedAttemptsLeft = spawnList.size() + 5;
//
//        // TODO: missing impl
//
//    }

//    private Mob spawnMob(EntityType<?> entityType, ServerLevel level, Vec3 spawnPos) {
//        if (!(entityType.create(level) instanceof Mob mob)) return null;
//
//        mob.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
//
//        WaveRaid.EntityAdjustment adjustment = this.currentRaidData.getAdjustment(entityType);
//        if (adjustment != null) {
//            adjustment.adjustMob(mob);
//        }
//
//        // NOTE: might need to dynamically add GunAttackGoal, maybe because could already be handled.
//
//        level.addFreshEntity(mob);
//        return mob;
//    }

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
}

