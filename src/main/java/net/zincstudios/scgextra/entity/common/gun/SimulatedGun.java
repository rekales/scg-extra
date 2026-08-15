package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

// TODO: remove accuracyModifier, handle it outside the gun.
@SuppressWarnings("unused")
public interface SimulatedGun {

    float BASE_AIM_ERROR = 5.0F;

    /**
     * @return true if fired a projectile
     */
    boolean tickFire(LivingEntity shooter, Vec3 targetPos, float accuracyModifier, boolean firing);

    default boolean tickFire(LivingEntity shooter, Vec3 targetPos, float accuracyModifier) {
        return this.tickFire(shooter, targetPos, accuracyModifier, true);
    }

    boolean hasChanged(LivingEntity entity);

    float getMaxRange();

    float getIdealRange();

    int getAmmoCapacity();

    int getAmmoCount();

    void setAmmoCount(int ammoCount);

    default void reloadAmmo() {
        this.setAmmoCount(this.getAmmoCapacity());
    }

    static Vec3 getCenterMassPos(LivingEntity target) {
        return target.position().add(0, target.getBbHeight()*0.7, 0);
    }

    static Vec3 getDirectionVector(Vec3 startPos, Vec3 targetPos) {
        return targetPos.subtract(startPos).normalize();
    }

    static Vec3 getVectorFromRotation(float pitch, float yaw) {
        float f = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f1 = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f2 = -Mth.cos(-pitch * ((float)Math.PI / 180F));
        float f3 = Mth.sin(-pitch * ((float)Math.PI / 180F));
        return new Vec3(f1 * f2, f3, f * f2);
    }

}
