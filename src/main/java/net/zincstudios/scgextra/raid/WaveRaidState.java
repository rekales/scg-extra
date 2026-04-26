package net.zincstudios.scgextra.raid;

import net.minecraft.util.RandomSource;
import net.zincstudios.scgextra.entity.EnemyRank;

import javax.annotation.Nullable;


// TODO: delete class and reuse code elsewhere
public class WaveRaidState {

    private WaveRaid raidData;
    private int currentWave;
    private int infantrySpawned;
    private int eliteSpawned;
    private int minibossSpawned;
    private boolean bossReleased;
    private int bossRetryCount;

    public WaveRaidState(WaveRaid raidData) {

    }

    public int getCurrentWave() {
        return this.currentWave;
    }

    public boolean isBossReleased() {
        return this.bossReleased;
    }

    public int getBossRetryCount() {
        return this.bossRetryCount;
    }

    public void incrementBossRetryCount() {
        this.bossRetryCount++;
    }

    public void clearBossRetryCount() {
        bossRetryCount = 0;
    }

    public void setBossReleased(int totalStrictWaves) {
        this.currentWave = totalStrictWaves;
        this.bossReleased = true;
    }

    public void onSpawned(EnemyRank rank) {
        switch (rank) {
            case INFANTRY -> this.infantrySpawned++;
            case ELITE -> this.eliteSpawned++;
            case MINIBOSS -> this.minibossSpawned++;
        }
    }

    public void advanceWave() {
        this.currentWave++;
        infantrySpawned = 0;
        eliteSpawned = 0;
        minibossSpawned = 0;
    }

    public int getRemainingInWave() {
        WaveRaid.Wave wave = this.raidData.profile().getWave(this.currentWave);
        return Math.max(0, wave.infantry() - this.infantrySpawned)
                + Math.max(0, wave.elite() - this.eliteSpawned)
                + Math.max(0, wave.miniboss() - this.minibossSpawned);
    }

    public boolean isCurrentWaveComplete() {
        return this.getRemainingInWave() <= 0;
    }

    public @Nullable EnemyRank nextCategoryToSpawn(RandomSource random) {
        WaveRaid.Wave wave = this.raidData.profile().getWave(this.currentWave);
        int infantryLeft = Math.max(0, wave.infantry() - this.infantrySpawned);
        int eliteLeft = Math.max(0, wave.elite() - this.eliteSpawned);
        int minibossLeft = Math.max(0, wave.miniboss() - this.minibossSpawned);
        int totalLeft = infantryLeft + eliteLeft + minibossLeft;
        if (totalLeft <= 0) return null;
        int roll = random.nextInt(totalLeft);
        if (roll < infantryLeft) return EnemyRank.INFANTRY;
        roll -= infantryLeft;
        if (roll < eliteLeft) return EnemyRank.ELITE;
        return EnemyRank.MINIBOSS;
    }
}
