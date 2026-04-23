package net.zincstudios.scgextra.entity.fac.fac_lion;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.fac.FACSounds;

import java.util.EnumSet;

public class FacLionShieldBashGoal extends Goal {

    private final FacLionEntity parent;
    private int cooldown = 0;
    private int ticks = 0;
    private boolean attacked = false;

    public FacLionShieldBashGoal(FacLionEntity mob) {
        this.parent = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
        }

        LivingEntity target = this.parent.getTarget();
        return target != null
                && target.isAlive()
                && this.cooldown <= 0
                && this.parent.distanceToSqr(target) <= 9.0D;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.parent.getTarget();
        return target != null && target.isAlive() && this.ticks <= 14;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 50;
        this.ticks = 0;
        this.attacked = false;
        this.parent.setShieldBashing(true);
        this.parent.triggerAnim("shield_bash", "shield_bash");
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        LivingEntity target = this.parent.getTarget();
        if (target == null) {
            return;
        }

        this.parent.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
        if (this.parent.distanceToSqr(target) > 2.89D) {
            this.parent.getNavigation().moveTo(target, 1.0D);
        } else {
            this.parent.getNavigation().stop();
        }

        if (!this.attacked && this.ticks >= 5 && this.parent.distanceToSqr(target) <= 12.25D) {
            this.attacked = true;
            target.hurt(this.parent.damageSources().mobAttack(this.parent), 15.0F);
            this.parent.playSound(FACSounds.FAC_LION_ATTACK_1.get(), 1.0F, 1.0F);
            double dx = this.parent.getX() - target.getX();
            double dz = this.parent.getZ() - target.getZ();
            target.knockback(2.0D, dx, dz);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.parent.getNavigation().stop();
        this.parent.setShieldBashing(false);
    }
}
