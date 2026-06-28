package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FacTankStompGoal extends Goal {
    private static final int STOMP_ANIMATION_TICKS = 20;
    private static final int STOMP_HIT_TICK = 6;

    private final FacTankEntity parent;
    private int cooldown = 0;
    private int ticks = 0;
    private boolean attacked = false;

    public FacTankStompGoal(FacTankEntity mob) {
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
                && !this.parent.isStunned()
                && !this.parent.isActionLocked()
                && this.parent.distanceToSqr(target) <= 10.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticks < STOMP_ANIMATION_TICKS && !this.parent.isStunned();
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 45;
        this.ticks = 0;
        this.attacked = false;
        this.parent.startStompLock(STOMP_ANIMATION_TICKS);
        this.parent.getNavigation().stop();
        this.parent.triggerAnim("attack", "stomp");
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        LivingEntity target = this.parent.getTarget();
        this.parent.getNavigation().stop();
        if (target != null && target.isAlive()) {
            this.parent.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
        }

        if (!this.attacked
                && this.ticks >= STOMP_HIT_TICK
                && target != null
                && target.isAlive()
                && this.parent.distanceToSqr(target) <= 10.0D) {
            this.attacked = true;
            target.hurt(this.parent.damageSources().mobAttack(this.parent), 20.0F);
            double dx = this.parent.getX() - target.getX();
            double dz = this.parent.getZ() - target.getZ();
            target.knockback(1.0D, dx, dz);
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.parent.clearStompLock();
        this.parent.getNavigation().stop();
    }
}
