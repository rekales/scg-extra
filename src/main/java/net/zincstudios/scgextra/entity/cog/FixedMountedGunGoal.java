package net.zincstudios.scgextra.entity.cog;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Fixed on the position of the head
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FixedMountedGunGoal<T extends Mob> extends MountedGunGoal<T> {

    public FixedMountedGunGoal(T mob, GunItem gunItem) {
        super(mob, gunItem);
    }

    @Override
    public boolean canShoot(LivingEntity target) {
        if (!super.canShoot(target)) return false;

        Vec3 toTarget = target.position().subtract(mob.getEyePosition());
        float targetYaw = (float) Math.toDegrees(Math.atan2(-toTarget.x, toTarget.z));
        float mobYaw = mob.getYHeadRot();
        float diff = Mth.wrapDegrees(mobYaw - targetYaw);
        return Math.abs(diff) < 10.0f;
    }
}
