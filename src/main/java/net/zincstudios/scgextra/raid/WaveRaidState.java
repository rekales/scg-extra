package net.zincstudios.scgextra.raid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import java.util.*;

// TODO: remove logic and possible reference to serverlevel
@SuppressWarnings("unused")
public class WaveRaidState {

    public static final int RAID_TIMEOUT_TICKS = 12000;  // 10 minutes, TODO: config
    private static final int NEXT_WAVE_DELAY = 30;

    private final UUID raidId;
    private final ServerLevel level;
    private final Set<UUID> raiderUUIDs = new HashSet<>();
    private final long startTime;  // level gametime timestamp
    private final WaveRaidData waveRaidData;
    private Vec3 spawnCenter;
    private int currentWave;
    private UUID targetPlayerUUID = null;
    private boolean active;
    private int nextWaveDelay;
    private int totalWaveSpawned = 0;

    public WaveRaidState(WaveRaidData waveRaidData, ServerLevel level, Vec3 spawnCenter) {
        this.raidId = UUID.randomUUID();
        this.startTime = level.getGameTime();
        this.level = level;
        this.currentWave = 0;
        this.waveRaidData = waveRaidData;
        this.spawnCenter = spawnCenter;
        this.active = true;
        this.nextWaveDelay = NEXT_WAVE_DELAY;
    }

    // For nbt deserialization
    private WaveRaidState(ServerLevel level, UUID raidId, WaveRaidData waveRaidData, long startTime, Vec3 spawnCenter) {
        this.level = level;
        this.raidId = raidId;
        this.waveRaidData = waveRaidData;
        this.startTime = startTime;
        this.spawnCenter = spawnCenter;
    }

    public int getCurrentWave() {
        return this.currentWave;
    }

    public WaveRaidData getWaveRaidData() {
        return this.waveRaidData;
    }

    public void advanceWave() {
        if (!this.waveRaidData.isFinalWave(this.currentWave)) {
            this.currentWave++;
            this.nextWaveDelay = NEXT_WAVE_DELAY;
            this.totalWaveSpawned = 0;
        }
    }

    public Vec3 getCenter() {
        return this.spawnCenter;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void addRaider(Mob mob) {
        this.raiderUUIDs.add(mob.getUUID());
        this.totalWaveSpawned = this.raiderUUIDs.size();
    }

    public void setTargetPlayer(Player player) {
        this.targetPlayerUUID = player.getUUID();
    }

    @Nullable
    public ServerPlayer getTargetPlayer() {
        if (this.level == null || this.targetPlayerUUID == null) return null;
        return this.level.getServer().getPlayerList().getPlayer(this.targetPlayerUUID);
    }

    public UUID getRaidId() {
        return this.raidId;
    }

    public void discardRaiders() {
        for (UUID uuid : this.raiderUUIDs) {
            Entity entity = this.level.getEntity(uuid);
            if (entity != null && !entity.isRemoved()) {
                entity.discard();
            }
        }
    }

    public void endRaid() {
        this.endRaid(this.isRaidersEliminated() && this.waveRaidData.isFinalWave(this.currentWave));
    }

    public void endRaid(boolean success) {
        this.active = false;

        if (success) {
            WaveRaidUtil.announceToNearbyPlayers(this.level,
                    Component.translatable("raid.scguns.defeated"), this.spawnCenter, 64);
        } else {
            this.discardRaiders();
            WaveRaidUtil.announceToNearbyPlayers(this.level,
                    Component.translatable("raid.scguns.failed"), this.spawnCenter, 64);
        }
    }

    // Maybe the state shouldn't handle logic at all
    public void tick() {
        if (this.active) {
            this.updateRaiders();
            if (this.isRaidersEliminated()) {
                if (this.waveRaidData.isFinalWave(this.currentWave) && !this.hasEnded() && this.nextWaveDelay <= 0) {
                    this.endRaid(true);
                } else {
                    this.nextWaveDelay--;
                }
            }
            if (this.isTimedOut()) {
                this.endRaid(false);
            }
            ServerPlayer player = this.getTargetPlayer();
            if (player != null) {
                this.spawnCenter = player.position();  // NOTE: centering to player might not be ideal.
            }
        }
    }

    private boolean isTimedOut() {
        return this.level.getGameTime() > this.startTime + RAID_TIMEOUT_TICKS;
    }

    private void updateRaiders() {
        this.raiderUUIDs.removeIf((uuid) -> {
            Entity entity = this.level.getEntity(uuid);
            if (entity != null) {
                return entity.isRemoved();
            } else {
                return true;
            }
        });
    }

    public boolean isRaidersEliminated() {
        return this.raiderUUIDs.isEmpty();
    }

    public boolean isNextWaveReady() {
        return this.active
                && !this.waveRaidData.isFinalWave(this.currentWave)
                && this.isRaidersEliminated()
                && this.nextWaveDelay <= 0 ;
    }

    public boolean hasEnded() {
        return !this.active;
    }

    public float getBossBarProgress() {
        if (this.waveRaidData.isFinalWave(this.currentWave) && this.raiderUUIDs.size() == 1) {
            UUID bossId = this.raiderUUIDs.iterator().next();
            Entity entity = this.level.getEntity(bossId);
            if (entity instanceof LivingEntity bossEntity) {
                return bossEntity.getHealth()/bossEntity.getMaxHealth();
            }
        } else {
            return (float)this.raiderUUIDs.size()/this.totalWaveSpawned;
        }
        return 1f;
    }

    public Component getBossBarLabel() {
        if (this.waveRaidData.isFinalWave(this.currentWave) && this.raiderUUIDs.size() == 1) {
            UUID bossId = this.raiderUUIDs.iterator().next();
            Entity entity = this.level.getEntity(bossId);
            if (entity instanceof LivingEntity bossEntity) {
                return bossEntity.getDisplayName();
            }
        }

        String wave = switch (this.currentWave) {
            case 0 -> "wave_1";
            case 1 -> "wave_2";
            case 2 -> "wave_3";
            case 3 -> "wave_4";
            case 4 -> "wave_5";
            case 5 -> "wave_6";
            case 6 -> "wave_7";
            case 7 -> "wave_8";
            case 8 -> "wave_9";
            default -> "";
        };
        if (this.waveRaidData.isFinalWave(this.currentWave)) wave = "last_wave";

        return Component.translatable(SCGExtra.MOD_ID+".raid.label."+this.waveRaidData.id())
                .append(" ")
                .append(Component.translatable(SCGExtra.MOD_ID+".raid.label."+wave));
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("RaidId", this.raidId);

        ListTag raiderList = new ListTag();
        for (UUID uuid : this.raiderUUIDs) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("UUID", uuid);
            raiderList.add(uuidTag);
        }
        tag.put("Raiders", raiderList);

        tag.putLong("StartTime", this.startTime);
        tag.putDouble("SpawnX", this.spawnCenter.x);
        tag.putDouble("SpawnY", this.spawnCenter.y);
        tag.putDouble("SpawnZ", this.spawnCenter.z);
        tag.putString("WaveRaidData", this.waveRaidData.id());
        tag.putInt("CurrentWave", this.currentWave);

        if (this.targetPlayerUUID != null) {
            tag.putUUID("TargetPlayer", this.targetPlayerUUID);
        }

        tag.putBoolean("Active", this.active);
        tag.putInt("NextWaveDelay", this.nextWaveDelay);
        tag.putInt("TotalWaveSpawned", this.totalWaveSpawned);

        return tag;
    }

    public static @Nullable WaveRaidState deserialize(CompoundTag tag, ServerLevel level) {
        UUID raidId = tag.getUUID("RaidId");

        Set<UUID> raiders = new HashSet<>();
        ListTag raiderList = tag.getList("Raiders", Tag.TAG_COMPOUND);
        for (int i = 0; i < raiderList.size(); i++) {
            raiders.add(raiderList.getCompound(i).getUUID("UUID"));
        }

        long startTime = tag.getLong("StartTime");
        Vec3 spawnCenter = new Vec3(
                tag.getDouble("SpawnX"),
                tag.getDouble("SpawnY"),
                tag.getDouble("SpawnZ")
        );

        String waveRaidDataId = tag.getString("WaveRaidData");
        WaveRaidData waveRaidData = WaveRaidData.getWaveRaid(waveRaidDataId);
        if (waveRaidData == null) {
            SCGExtra.LOGGER.warn("raid id: {}", waveRaidDataId);
            return null;
        }

        int currentWave = tag.getInt("CurrentWave");
        UUID targetPlayer = tag.contains("TargetPlayer") ? tag.getUUID("TargetPlayer") : null;
        boolean active = tag.getBoolean("Active");
        int nextWaveDelay = tag.getInt("NextWaveDelay");
        int totalWaveSpawned = tag.getInt("TotalWaveSpawned");

        WaveRaidState state = new WaveRaidState(level, raidId, waveRaidData, startTime, spawnCenter);
        state.raiderUUIDs.addAll(raiders);
        state.currentWave = currentWave;
        state.targetPlayerUUID = targetPlayer;
        state.active = active;
        state.nextWaveDelay = nextWaveDelay;
        state.totalWaveSpawned = totalWaveSpawned;
        return state;
    }
}
