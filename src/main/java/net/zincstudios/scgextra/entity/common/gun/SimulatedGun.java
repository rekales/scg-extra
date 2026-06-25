package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.Config;

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

    static float getMobDamageMultiplier(Level level) {
        float difficultyDamageMultiplier = getDifficultyDamageMultiplier(level.getDifficulty());
        float configDamageMultiplier = Config.COMMON.gameplay.mobGunDamageMultiplier.get().floatValue();
        return difficultyDamageMultiplier * configDamageMultiplier;
    }

    static float getDifficultyDamageMultiplier(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 0.05F;
            case EASY ->  0.35F;
            case NORMAL -> 0.5F;
            case HARD -> 0.65F;
        };
    }

    static float getDifficultyAimError(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 3.0F;
            case EASY -> 2.0F;
            case NORMAL -> 1.5F;
            case HARD -> 1.0F;
        };
    }

    static Vec3 getCenterMassPos(LivingEntity target) {
        return target.position().add(0, target.getBbHeight()*0.7, 0);
    }

    static Vec3 getDirectionVector(Vec3 startPos, Vec3 targetPos) {
        return targetPos.subtract(startPos).normalize();
    }

    /**
     * @param dir expects a direction unit vector
     */
    static Vec3 addWeaponSpread(LivingEntity shooter, Vec3 dir, float spread) {
        spread = Math.min(spread, 170F) * 0.5F * Mth.DEG_TO_RAD;
        Vec3 spreadUpwards = getVectorFromRotation(shooter.getViewXRot(1F) + 90F, shooter.getViewYRot(1F));
        Vec3 spreadSideways = dir.cross(spreadUpwards);

        float theta2 = shooter.level().random.nextFloat() * 2F * (float) Math.PI;
        float r2 = Mth.sqrt(shooter.level().random.nextFloat()) * (float) Math.tan(spread);

        float b1 = Mth.cos(theta2) * r2;
        float b2 = Mth.sin(theta2) * r2;

        return dir.add(spreadSideways.scale(b1)).add(spreadUpwards.scale(b2)).normalize();
    }

    /**
     * Need to redo the sloppy implementation from original
     *
     * @param dir expects a direction unit vector
     */
    static Vec3 addAimError(LivingEntity shooter, Vec3 dir, float accuracyModifier) {
        float aimError = (BASE_AIM_ERROR * getDifficultyAimError(shooter.level().getDifficulty())) / accuracyModifier;
        aimError = Math.min(aimError, 25F);

        float aimErrorRad = aimError * Mth.DEG_TO_RAD;
        float theta1 = shooter.level().random.nextFloat() * 2F * (float) Math.PI;
        float r1 = Mth.sqrt(shooter.level().random.nextFloat()) * (float) Math.tan(aimErrorRad);

        Vec3 vecUpwards = getVectorFromRotation(shooter.getViewXRot(1F) + 90F, shooter.getViewYRot(1F));
        Vec3 vecSideways = dir.cross(vecUpwards);

        float a1 = Mth.cos(theta1) * r1;
        float a2 = Mth.sin(theta1) * r1;

        return dir.add(vecSideways.scale(a1)).add(vecUpwards.scale(a2)).normalize();
    }

    private static Vec3 getVectorFromRotation(float pitch, float yaw) {
        float f = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f1 = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f2 = -Mth.cos(-pitch * ((float)Math.PI / 180F));
        float f3 = Mth.sin(-pitch * ((float)Math.PI / 180F));
        return new Vec3(f1 * f2, f3, f * f2);
    }

}
