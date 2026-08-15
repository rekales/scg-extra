package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class HeadAttachedMountedGun extends MountedGun{

    public HeadAttachedMountedGun(LivingEntity parent, SimulatedGun gun, Vec3 gunPosition, Predicate<LivingEntity> canShoot) {
        super(parent, gun, gunPosition, canShoot);
    }

    @Override
    public boolean tick(LivingEntity target, float accuracyModifier) {
        if (!isActive.test(this.parent)) return false;

        Vec3 targetPos = SimulatedGun.getCenterMassPos(target);
        this.pointToTarget(targetPos);

        return this.gun.tickFire(this.parent,
                targetPos,
                accuracyModifier,
                this.shouldShoot(targetPos)
        );
    }

    @Override
    protected void pointToTarget(Vec3 targetPos) {
        this.yaw = this.parent.getYHeadRot();
        this.pitch = this.parent.getXRot();
    }

    @Override
    protected Vec3 getGunPos() {
        return this.parent.position().add(this.spawnOffset.yRot(-this.parent.yHeadRot * Mth.DEG_TO_RAD));
    }
}
