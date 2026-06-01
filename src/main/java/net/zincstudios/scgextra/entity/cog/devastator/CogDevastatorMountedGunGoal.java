package net.zincstudios.scgextra.entity.cog.devastator;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.cog.FixedMountedGunGoal;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogDevastatorMountedGunGoal extends FixedMountedGunGoal<CogDevastatorEntity> {

    public CogDevastatorMountedGunGoal(CogDevastatorEntity mob, GunItem gunItem) {
        super(mob, gunItem);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isStunned();
    }

    @Override
    protected Vec3 getSpawnOffset() {
        return this.spawnOffset.yRot(-this.mob.yHeadRot * Mth.DEG_TO_RAD);
    }
}
