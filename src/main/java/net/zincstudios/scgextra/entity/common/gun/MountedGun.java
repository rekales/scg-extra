package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.MobUtil;

import java.util.function.Predicate;

// TODO: consider if turning this to a core behavior is better
@SuppressWarnings("SuspiciousNameCombination")
public class MountedGun {

    private final LivingEntity parent;
    private final SimulatedGun gun;
    private final Vec3 spawnOffset;
    private final Predicate<LivingEntity> isActive;

    public float yaw = 0;
    public float pitch = 0;

    public MountedGun(LivingEntity parent, SimulatedGun gun, Vec3 gunPosition, Predicate<LivingEntity> canShoot) {
        this.parent = parent;
        this.gun = gun;
        this.spawnOffset = gunPosition;
        this.isActive = canShoot;
    }

    public boolean tick(LivingEntity target, float accuracyModifier) {
        if (!isActive.test(this.parent)) return false;

        Vec3 targetPos = SimulatedGun.getCenterMassPos(target);
        this.pointToTarget(targetPos);

        return this.gun.tickFire(this.parent,
                MobUtil.toVec(this.yaw, this.pitch).scale(4).add(this.getGunPos()),
                accuracyModifier,
                this.shouldShoot(targetPos)
        );
    }

    protected void pointToTarget(Vec3 targetPos) {
        Vec3 delta = targetPos.subtract(this.getGunPos());
        float targetYaw = -((float)(Mth.atan2(delta.x, delta.z) * 180.0F / Math.PI));
        float targetPitch = -((float)(Mth.atan2(delta.y, delta.horizontalDistance()) * 180.0F / Math.PI));

        this.yaw = Mth.approachDegrees(this.yaw, targetYaw, 2F);
        this.pitch = Mth.approachDegrees(this.pitch, targetPitch, 1F);
    }

    // checks if the mounted gun is currently pointing at the target
    protected boolean shouldShoot(Vec3 targetPos) {
        Vec3 delta = targetPos.subtract(this.getGunPos());
        float targetYaw = -((float)(Mth.atan2(delta.x, delta.z) * 180.0F / Math.PI));
        float targetPitch = -((float)(Mth.atan2(delta.y, delta.horizontalDistance()) * 180.0F / Math.PI));

        float yawDiff = Mth.wrapDegrees(targetYaw - this.yaw);
        float pitchDiff = Mth.wrapDegrees(targetPitch - this.pitch);

        return (Math.abs(yawDiff) < 12.0f && Math.abs(pitchDiff) < 12.0f);
    }

    protected Vec3 getGunPos() {
        return this.parent.position().add(this.spawnOffset.yRot(-this.parent.yBodyRot * Mth.DEG_TO_RAD)) ;  // TODO: override with yHeadRot later
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}
