package net.zincstudios.scgextra.raidwave;

import net.minecraft.util.RandomSource;

public final class RaidWaveState {
    public static final int CATEGORY_INFANTRY = 0;
    public static final int CATEGORY_ELITE = 1;
    public static final int CATEGORY_MINIBOSS = 2;

    private int waveIndex;
    private int infantrySpawned;
    private int eliteSpawned;
    private int minibossSpawned;
    private boolean bossReleased;
    private int waveSpawnedTotal;
    private int bossRetryCount;

    public int getWaveIndex() {
        return waveIndex;
    }

    public boolean isBossReleased() {
        return bossReleased;
    }

    public int getBossRetryCount() {
        return bossRetryCount;
    }

    public void incrementBossRetryCount() {
        bossRetryCount++;
    }

    public void clearBossRetryCount() {
        bossRetryCount = 0;
    }

    public void setBossReleased(int totalStrictWaves) {
        waveIndex = totalStrictWaves;
        bossReleased = true;
    }

    public void advanceWave() {
        waveIndex++;
        resetCurrentWaveSpawnCounters();
    }

    public boolean isCurrentWaveComplete(int[] wavePlan) {
        return infantrySpawned >= wavePlan[0]
            && eliteSpawned >= wavePlan[1]
            && minibossSpawned >= wavePlan[2];
    }

    public int getRemainingInWave(int[] wavePlan) {
        return Math.max(0, wavePlan[0] - infantrySpawned)
            + Math.max(0, wavePlan[1] - eliteSpawned)
            + Math.max(0, wavePlan[2] - minibossSpawned);
    }

    public int nextCategoryToSpawn(int[] wavePlan, RandomSource random) {
        int infantryLeft = Math.max(0, wavePlan[0] - infantrySpawned);
        int eliteLeft = Math.max(0, wavePlan[1] - eliteSpawned);
        int minibossLeft = Math.max(0, wavePlan[2] - minibossSpawned);
        int totalLeft = infantryLeft + eliteLeft + minibossLeft;
        if (totalLeft <= 0) return -1;
        int roll = random.nextInt(totalLeft);
        if (roll < infantryLeft) return CATEGORY_INFANTRY;
        roll -= infantryLeft;
        if (roll < eliteLeft) return CATEGORY_ELITE;
        return CATEGORY_MINIBOSS;
    }

    public void onSpawned(int category) {
        if (category == CATEGORY_INFANTRY) {
            infantrySpawned++;
        } else if (category == CATEGORY_ELITE) {
            eliteSpawned++;
        } else if (category == CATEGORY_MINIBOSS) {
            minibossSpawned++;
        }
        waveSpawnedTotal++;
    }

    private void resetCurrentWaveSpawnCounters() {
        infantrySpawned = 0;
        eliteSpawned = 0;
        minibossSpawned = 0;
        waveSpawnedTotal = 0;
    }
}
