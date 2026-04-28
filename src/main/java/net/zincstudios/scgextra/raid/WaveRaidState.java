package net.zincstudios.scgextra.raid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WaveRaidState {

    private final UUID raidId = UUID.randomUUID();
    private final Set<UUID> raiderUUIDs = new HashSet<>();
    private final long startTime;  // level gametime timestamp
    private final Vec3 spawnCenter;
    private final WaveRaidData waveRaidData;
    private int currentWave;
    private UUID targetPlayerUUID = null;


    public WaveRaidState(WaveRaidData waveRaidData, ServerLevel level, Vec3 spawnCenter) {
        this.startTime = level.getGameTime();
        this.currentWave = 1;
        this.waveRaidData = waveRaidData;
        this.spawnCenter = spawnCenter;
    }

    public int getCurrentWave() {
        return this.currentWave;
    }

    public WaveRaidData getWaveRaidData() {
        return this.waveRaidData;
    }

    public void advanceWave() {
        this.currentWave++;
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

}
