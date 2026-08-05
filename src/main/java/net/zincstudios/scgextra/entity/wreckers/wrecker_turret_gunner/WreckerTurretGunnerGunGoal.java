package net.zincstudios.scgextra.entity.wreckers.wrecker_turret_gunner;

import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.entity.cog.FixedMountedGunGoal;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@ParametersAreNonnullByDefault
public class WreckerTurretGunnerGunGoal extends FixedMountedGunGoal<WreckerTurretGunnerEntity> {

    public WreckerTurretGunnerGunGoal(WreckerTurretGunnerEntity mob, GunItem gunItem) {
        super(mob, gunItem);
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
        super.tick();
    }

    @Override
    protected void fireGun(LivingEntity target) {
        super.fireGun(target);
        this.mob.onGunAttack(target, this.gunStack);
    }
}
