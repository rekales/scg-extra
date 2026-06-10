package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.RandomSource;

import java.util.Random;

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

    private boolean state = false;

    public MarkovTriggerSampler(float pStayOn, float pStayOff) {
        this.pStayOn = pStayOn;
        this.pStayOff = pStayOff;
    }

    public boolean next(RandomSource random) {
        if (this.state) {
            this.state = random.nextDouble() < this.pStayOn;
        } else {
            this.state = random.nextDouble() >= this.pStayOff;
        }
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
