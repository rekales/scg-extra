package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.entity.cog.FixedMountedGunGoal;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogGigantesMountedGunGoal extends FixedMountedGunGoal<CogGigantesEntity> {

    private int firingTimeOut = 0;

    public CogGigantesMountedGunGoal(CogGigantesEntity mob, GunItem gunItem) {
        super(mob, gunItem);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isStunned();
    }

    @Override
    public void start() {
        super.start();
        this.firingTimeOut = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.setFiring(false);
    }

    @Override
    public void tick() {
        if (this.firingTimeOut > 0) {
            this.mob.setFiring(true);
            this.firingTimeOut--;
        } else {
            this.mob.setFiring(false);
        }
        super.tick();
    }

    @Override
    protected void fireGun(LivingEntity target) {
        super.fireGun(target);
        this.firingTimeOut = this.burstInterval*2;
    }
}
