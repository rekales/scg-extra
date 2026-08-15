package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.RandomSource;

/**
 * Uses a 2 state markov chain to generate trigger patterns.
 * <p>
 * Using bernoulli sampling (like random.nextDouble() > 0.7) would generate a
 * pattern of: 10100111010101101100110010110100111010101101100110010110.
 * This sampling strategy, in theory, should generate a more sensible
 * pattern like: 111111000000000111111111111111100000111111111000000000000
 */
public class MarkovTriggerSampler implements TriggerStateSampler {

    private final float pStayOn;
    private final float pStayOff;
    private final int minStreak;
    private final int maxStreak;

    private boolean state = false;
    private int streak = 1;

    public MarkovTriggerSampler(float pStayOn, float pStayOff) {
        this(pStayOn, pStayOff, 1, 1000);
    }

    public MarkovTriggerSampler(float pStayOn, float pStayOff, int minStreak, int maxStreak) {
        this.pStayOn = pStayOn;
        this.pStayOff = pStayOff;
        this.minStreak = minStreak;
        this.maxStreak = maxStreak;
    }

    @Override
    public boolean next(RandomSource random) {
        if (this.streak < this.minStreak) {
            this.streak++;
            return this.state;
        } else if (this.streak > this.maxStreak) {
            this.streak = 1;
            this.state = !this.state;
            return this.state;
        }

        boolean oldState = this.state;
        if (this.state) {
            this.state = random.nextDouble() < this.pStayOn;
        } else {
            this.state = random.nextDouble() >= this.pStayOff;
        }

        if (this.state == oldState) {
            this.streak++;
        } else {
            this.streak = 1;
        }

        return this.state;
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public boolean getState() {
        return this.state;
    }
}
