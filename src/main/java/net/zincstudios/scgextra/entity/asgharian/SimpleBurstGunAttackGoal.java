package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class SimpleBurstGunAttackGoal<T extends PathfinderMob> extends SimpleGunAttackGoal<T> {

    protected final int burstAmount;
    protected final int burstInterval;
    protected int burstCooldown = 0;
    protected int burstLeft = 0;

    public SimpleBurstGunAttackGoal(T mob, int burstAmount, int burstIntervalTicks) {
        super(mob);
        this.burstAmount = burstAmount;
        this.burstInterval = Math.max(burstIntervalTicks, 1);
    }

    @Override
    public void start() {
        super.start();
        burstLeft = 0;
        burstCooldown = 0;
    }

    protected boolean handleAttack(LivingEntity target) {
        if (this.burstLeft == 0) {
            this.burstLeft = this.burstAmount;
        }

        this.burstCooldown--;
        if (this.burstLeft > 0 && this.burstCooldown <= 0) {
            this.burstCooldown = this.burstInterval;
            this.burstLeft--;
            this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            fireGun(target);
        }

        return this.burstLeft > 0;
    }
}
