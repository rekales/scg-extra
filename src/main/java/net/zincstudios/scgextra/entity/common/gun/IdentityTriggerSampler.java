package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.RandomSource;

public class IdentityTriggerSampler implements TriggerStateSampler {

    @Override
    public boolean next(RandomSource random) {
        return true;
    }

    @Override
    public void setState(boolean state) {}

    @Override
    public boolean getState() {
        return true;
    }
}
