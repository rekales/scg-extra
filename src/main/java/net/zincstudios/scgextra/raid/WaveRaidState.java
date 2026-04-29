package net.zincstudios.scgextra.raid;

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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//@SuppressWarnings("unused")
public class WaveRaidState {

    private static final int NEXT_WAVE_DELAY = 30;

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
    private int totalWaveSpawned = 0;

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
        this.active = false;

        if (this.isRaidersEliminated() && WaveRaidData.Profile.isFinalWave(this.currentWave)) {
            this.announceToNearbyPlayers(Component.translatable("raid.scguns.defeated"), 64.0F);
        } else {
            this.discardRaiders();
            this.announceToNearbyPlayers(Component.translatable("raid.scguns.failed"), 64.0F);
        }
    }

    public void tick() {
        if (this.active) {
            this.updateRaiders();
            if (this.isRaidersEliminated()) {
                if (WaveRaidData.Profile.isFinalWave(this.currentWave) && !this.hasEnded() && this.nextWaveDelay <= 0) {
                    this.endRaid();
                } else {
                    this.nextWaveDelay--;
                }
            }
        }
    }

    public void announceToNearbyPlayers(Component message, double radius) {
        for(ServerPlayer player : this.level.getPlayers((player) -> player.position().distanceTo(this.spawnCenter) <= radius)) {
            player.sendSystemMessage(message);
        }
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
                && !WaveRaidData.Profile.isFinalWave(this.currentWave)
                && this.isRaidersEliminated()
                && this.nextWaveDelay <= 0 ;
    }

    public boolean hasEnded() {
        return !this.active;
    }

    public float getBossBarProgress() {
        if (WaveRaidData.Profile.isFinalWave(this.currentWave) && this.raiderUUIDs.size() == 1) {
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
        String wave = switch (this.currentWave) {
            case 1 -> "wave_1";
            case 2 -> "wave_2";
            case 3 -> "wave_3";
            case 4 -> "boss";
            default -> "";
        };

//        Component component = Component.translatable(SCGExtra.MOD_ID+".raid.label."+this.raidId)
//                .append(SCGExtra.MOD_ID+".raid.label."+wave);
        if (WaveRaidData.Profile.isFinalWave(this.currentWave) && this.raiderUUIDs.size() == 1) {
            UUID bossId = this.raiderUUIDs.iterator().next();
            Entity entity = this.level.getEntity(bossId);
            if (entity instanceof LivingEntity bossEntity) {
                return bossEntity.getDisplayName();
            }
        }

        return Component.translatable(SCGExtra.MOD_ID+".raid.label."+this.waveRaidData.id())
                .append(" ")
                .append(Component.translatable(SCGExtra.MOD_ID+".raid.label."+wave));
    }

    // NOTE: maybe on WaveRaidData instead?
    public Component getAnnouncement() {
        return Component.translatable(SCGExtra.MOD_ID+".raid.announcement."+this.waveRaidData.id());
    }
}
