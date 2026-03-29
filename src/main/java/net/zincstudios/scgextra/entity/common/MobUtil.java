package net.zincstudios.scgextra.entity.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class MobUtil {

    public static SoundEvent getSound(RandomSource random, SoundEvent... sounds){
        if(sounds.length<=0){
            return SoundEvents.ALLAY_HURT;//cause why not
        }
        return sounds[random.nextInt(sounds.length)];
    }

    /**
     * Used to gradually turn an entity to a direction. Intended to be invoked every tick when being used.
     */
    public static void turnEntityToYaw(LivingEntity entity, float yaw, float turnSpeed) {
        entity.setYRot(Mth.approachDegrees(entity.getYRot(), yaw, turnSpeed));
        entity.setYHeadRot(entity.getYRot());
        entity.setYBodyRot(entity.getYRot());
    }
}
