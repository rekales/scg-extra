package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.zincstudios.scgextra.entity.cog.FixedMountedGunGoal;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogGigantesMountedGunGoal extends FixedMountedGunGoal<CogGigantesEntity> {

    public CogGigantesMountedGunGoal(CogGigantesEntity mob, GunItem gunItem) {
        super(mob, gunItem);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isStunned();
    }
}
