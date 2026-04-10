package net.zincstudios.scgextra.entity.fac.fac_walker;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.Faction;

import java.util.EnumSet;

public class FacWalkerStompGoal extends Goal {

    private static final int STOMP_ANIMATION_TICKS = 17;
    private static final int STOMP_HIT_TICK = 4;
    private static final float STOMP_DAMAGE = 15.0F;
    private static final double STOMP_RADIUS = 3.8D;

    private final FacWalkerEntity parent;
    private int cooldown = 0;
    private int ticks = 0;
    private boolean attacked = false;

    public FacWalkerStompGoal(FacWalkerEntity mob) {
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
                && this.horizontalDistanceSqr(target) <= 9.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return this.ticks < STOMP_ANIMATION_TICKS && !this.parent.isStunned();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 45;
        this.ticks = 0;
        this.attacked = false;
        this.parent.startStompLock(STOMP_ANIMATION_TICKS);
        this.parent.stopRangedAnimation();
        this.parent.getNavigation().stop();
        this.parent.triggerAnim("attack", "stomp");
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        LivingEntity target = this.parent.getTarget();
        if (target == null) {
            return;
        }

        this.parent.getNavigation().stop();
        this.parent.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

        if (!this.attacked && this.ticks >= STOMP_HIT_TICK && target.isAlive()) {
            this.attacked = true;
            this.dealStompDamage(target);
        }
    }

    private void dealStompDamage(LivingEntity target) {
        double radiusSq = STOMP_RADIUS * STOMP_RADIUS;
        boolean hitAny = false;

        for (LivingEntity nearby : this.parent.level().getEntitiesOfClass(
                LivingEntity.class,
                this.parent.getBoundingBox().inflate(STOMP_RADIUS, 1.5D, STOMP_RADIUS),
                entity -> !entity.is(this.parent) && entity.isAlive() && !Faction.isFriendlies(this.parent, entity))) {
            if (this.horizontalDistanceSqr(nearby) > radiusSq) {
                continue;
            }
            this.applyStompHit(nearby);
            hitAny = true;
        }

        if (!hitAny) {
            this.applyStompHit(target);
        }
    }

    private void applyStompHit(LivingEntity target) {
        target.hurt(this.parent.damageSources().mobAttack(this.parent), STOMP_DAMAGE);
        double dx = this.parent.getX() - target.getX();
        double dz = this.parent.getZ() - target.getZ();
        target.knockback(1.0D, dx, dz);
    }

    private double horizontalDistanceSqr(LivingEntity target) {
        double dx = this.parent.getX() - target.getX();
        double dz = this.parent.getZ() - target.getZ();
        return dx * dx + dz * dz;
    }

    @Override
    public void stop() {
        super.stop();
        this.parent.clearStompLock();
        this.parent.getNavigation().stop();
    }
}
