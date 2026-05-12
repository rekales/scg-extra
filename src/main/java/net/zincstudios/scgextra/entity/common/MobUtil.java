package net.zincstudios.scgextra.entity.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

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

    public static void putBlockPosToTag(BlockPos blockPos, String prefix, CompoundTag tag) {
        tag.putInt(prefix + "X", blockPos.getX());
        tag.putInt(prefix + "Y", blockPos.getY());
        tag.putInt(prefix + "Z", blockPos.getZ());
    }

    public static @Nullable BlockPos getBlocKPosFromTag(String prefix, CompoundTag tag) {
        if (tag.contains(prefix+"X")) {
            return new BlockPos(tag.getInt(prefix+"X"), tag.getInt(prefix+"Y"), tag.getInt(prefix+"Z"));
        }
        return null;
    }

    public static Vec3 vecFromRot(float yRot) {
        float yaw = (float) Math.toRadians(yRot);
        float x = -Mth.sin(yaw);
        float z = Mth.cos(yaw);
        return new Vec3(x, 0, z);
    }

    public static float rotFromVec(Vec3 vec) {
        return (float) Mth.atan2(-vec.x, vec.z) * Mth.RAD_TO_DEG;
    }

}
