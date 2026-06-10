package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.RandomSource;

public interface TriggerStateSampler {

    boolean next(RandomSource random);

    void setState(boolean state);
}
