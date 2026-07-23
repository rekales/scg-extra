package net.zincstudios.scgextra.entity.turret;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class TurretAim {
    public static final float MAX_TRAVERSE = 70.0F;
    public static final float MAX_UP = 20.0F;
    public static final float MAX_DOWN = 15.0F;

    private static final float DEG_PER_RAD = 57.2957763671875F;
    private static final float RAD_PER_DEG = (float) (Math.PI / 180.0);

    private TurretAim() {}

    public static float[] clampYawPitch(Vec3 look, float baseYaw) {
        double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float) (-Mth.atan2(look.x, look.z) * DEG_PER_RAD);
        float pitch = (float) (-Mth.atan2(look.y, horizontal) * DEG_PER_RAD);
        yaw = baseYaw + Mth.clamp(Mth.wrapDegrees(yaw - baseYaw), -MAX_TRAVERSE, MAX_TRAVERSE);
        pitch = Mth.clamp(pitch, -MAX_UP, MAX_DOWN);
        return new float[]{yaw, pitch};
    }

    public static Vec3 direction(float yawDeg, float pitchDeg) {
        float yawRad = -yawDeg * RAD_PER_DEG;
        float pitchRad = pitchDeg * RAD_PER_DEG;
        float cosPitch = Mth.cos(pitchRad);
        return new Vec3(Mth.sin(yawRad) * cosPitch, -Mth.sin(pitchRad), Mth.cos(yawRad) * cosPitch);
    }

    public static float relativeYawRadians(float yawDeg, float baseYaw) {
        return -Mth.wrapDegrees(yawDeg - baseYaw) * RAD_PER_DEG;
    }

    public static float pitchRadians(float pitchDeg) {
        return -pitchDeg * RAD_PER_DEG;
    }
}
