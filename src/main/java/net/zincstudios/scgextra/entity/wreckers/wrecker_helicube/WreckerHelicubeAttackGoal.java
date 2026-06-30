package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.entity.cog.MountedGunGoal;
import top.ribs.scguns.item.GunItem;

public class WreckerHelicubeAttackGoal extends MountedGunGoal<WreckerHelicubeEntity> {

    private int firingTimeOut = 0;

    public WreckerHelicubeAttackGoal(WreckerHelicubeEntity mob, GunItem gunItem) {
        super(mob, gunItem);
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
        this.firingTimeOut = this.burstInterval * 2;
    }
}
