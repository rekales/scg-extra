package net.zincstudios.scgextra.entity.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;

public interface SoundEntity {
    default SoundEvent getSound(RandomSource random, SoundEvent... sounds){
        if(sounds.length<=0){
            return SoundEvents.ALLAY_HURT;//cause why not
        }
        return sounds[random.nextInt(sounds.length)];
    }
}
