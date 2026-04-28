package net.zincstudios.scgextra.raid;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WaveRaidState {

    private static final int NEXT_WAVE_DELAY = 50;

    private final UUID raidId = UUID.randomUUID();
    private final ServerLevel level;
    private final Set<UUID> raiderUUIDs = new HashSet<>();
    private final long startTime;  // level gametime timestamp
    private final Vec3 spawnCenter;
    private final WaveRaidData waveRaidData;
    private int currentWave;
    private UUID targetPlayerUUID = null;
    private boolean active;
    private int nextWaveDelay;

    public WaveRaidState(WaveRaidData waveRaidData, ServerLevel level, Vec3 spawnCenter) {
        this.startTime = level.getGameTime();
        this.level = level;
        this.currentWave = 1;
        this.waveRaidData = waveRaidData;
        this.spawnCenter = spawnCenter;
        this.active = true;
        this.nextWaveDelay = NEXT_WAVE_DELAY;
    }

    public int getCurrentWave() {
        return this.currentWave;
    }

    public WaveRaidData getWaveRaidData() {
        return this.waveRaidData;
    }

    public void advanceWave() {
        if (!WaveRaidData.Profile.isFinalWave(this.currentWave)) {
            this.currentWave++;
            this.nextWaveDelay = NEXT_WAVE_DELAY;
        }
    }

    public Vec3 getSpawnCenter() {
        return this.spawnCenter;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void addRaider(Mob mob) {
        this.raiderUUIDs.add(mob.getUUID());
    }

    public void setTargetPlayer(UUID playerUUID) {
        this.targetPlayerUUID = playerUUID;
    }

    @Nullable
    public UUID getTargetPlayerUUID() {
        return this.targetPlayerUUID;
    }

    public UUID getRaidId() {
        return this.raidId;
    }

    public void discardRaiders() {

    }

//    public boolean isActive() {
//        return this.active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }

    public void endRaid() {
        this.active = false;

        if (isRaidersEliminated()) {
            this.announceToNearbyPlayers(Component.translatable("raid.scguns.defeated"), 64.0F);
        } else {
            this.discardRaiders();
            this.announceToNearbyPlayers(Component.translatable("raid.scguns.failed"), 64.0F);
        }
    }

    public void tick() {
        if (this.active) {
            this.updateRaiders();
            if (isRaidersEliminated()) {
                if (WaveRaidData.Profile.isFinalWave(this.currentWave) && !this.hasEnded()) {
                    this.endRaid();
                } else {
                    this.nextWaveDelay--;
                }
            }
        }
    }

    public void announceToNearbyPlayers(Component message, double radius) {
        for(ServerPlayer player : this.level.getPlayers((playerx) -> playerx.position().distanceTo(this.spawnCenter) <= radius)) {
            player.sendSystemMessage(message);
        }
    }

    private void updateRaiders() {
        this.raiderUUIDs.removeIf((uuid) -> {
            Entity entity = this.level.getEntity(uuid);
            if (entity instanceof LivingEntity livingEntity) {
                return !livingEntity.isAlive();
            } else {
                return true;
            }
        });
    }

    public boolean isRaidersEliminated() {
        return this.raiderUUIDs.isEmpty();
    }

    public boolean isNextWaveReady() {
        return this.active && this.isRaidersEliminated() && this.nextWaveDelay <= 0 ;
    }

    public boolean hasEnded() {
        return !this.active;
    }
}
