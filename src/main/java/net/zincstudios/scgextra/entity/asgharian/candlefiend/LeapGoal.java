package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * Attempts to leap upwards and slightly forward. Used to counter pillar or hole cheese tactics.
 */
public class LeapGoal<T extends PathfinderMob & Leaping> extends Goal {

    public static final int POS_CHECK_INTERVAL = 60;
    public static final double LAST_POS_DISTANCE_THRESHOLD = 1.5;
    public static final double HEIGHT_DIFF_THRESHOLD = 2;

    protected final T mob;
    protected final int cooldownDuration;
    protected final float jumpHeight;
    private int cooldownEnd = 0;  // tickCount timestamp
    private Vec3 lastPos = Vec3.ZERO;
    private int lastPosCheck = 0;  // tickCount timestamp

    public LeapGoal(T mob, int cooldownDuration, float jumpHeight) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
        this.jumpHeight = jumpHeight;
        // TODO: maybe add a position threshold for activation?
    }

    @Override
    public boolean canUse() {
        boolean shouldUse = false;
        LivingEntity target = this.mob.getTarget();
        if (target != null && this.mob.tickCount-this.lastPosCheck > POS_CHECK_INTERVAL) {
            if (this.mob.canLeap()
                    && this.lastPos.closerThan(this.mob.position(), LAST_POS_DISTANCE_THRESHOLD)
                    && target.position().y-this.mob.position().y >= HEIGHT_DIFF_THRESHOLD
                    && this.mob.tickCount-this.cooldownEnd > this.cooldownDuration) {
                shouldUse = true;
            }
            this.lastPos = this.mob.position();
            this.lastPosCheck = this.mob.tickCount;
        }

        return shouldUse;
    }

    @Override
    public void start() {
        this.cooldownEnd = this.mob.tickCount;
        this.leap();
    }

    public void leap() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        Vec3 delta = this.mob.getDeltaMovement();
        delta = delta.add(0, this.getLeapPower(), 0);
        delta = delta.add(this.mob.getLookAngle().scale(0.2));
        this.mob.setDeltaMovement(delta);

        this.mob.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this.mob);
    }

    protected float getLeapPower() {
        return 0.3F * this.jumpHeight;  // TODO: more accurate expression
    }
}
