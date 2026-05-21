package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BigLumpRangedAttackGoal extends Goal {
    private final BigLumpEntity mob;
    private final int shotIntervalTicks;

    private int shootAnimationTicks;
    private int shotCooldownTicks;
    private int shotsFired;

    public BigLumpRangedAttackGoal(BigLumpEntity mob, int shotIntervalTicks) {
        this.mob = mob;
        this.shotIntervalTicks = Math.max(1, shotIntervalTicks);
        this.shotCooldownTicks = this.shotIntervalTicks;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isRangedReloading()) {
            return false;
        }
        return this.mob.shouldUseRangedGoal();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return this.shootAnimationTicks > 0 && this.mob.isValidTarget(target);
    }

    @Override
    public void start() {
        this.shootAnimationTicks = BigLumpEntity.RANGED_SHOOT_WINDOW_TICKS;
        this.shotCooldownTicks = 0;
        this.shotsFired = 0;
        this.mob.startRangedAnimation(BigLumpEntity.RANGED_SHOOT_WINDOW_TICKS);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.shootAnimationTicks = 0;
        this.mob.stopRangedAnimation();
        this.mob.getNavigation().stop();
        this.mob.startRangedReload(BigLumpEntity.GUN_RELOAD_TICKS);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (!this.mob.isValidTarget(target)) {
            return;
        }
        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.mob.getNavigation().stop();

        if (this.shootAnimationTicks <= 0) {
            return;
        }

        int elapsed = BigLumpEntity.RANGED_SHOOT_WINDOW_TICKS - this.shootAnimationTicks;
        boolean inFireWindow = elapsed >= BigLumpEntity.RANGED_FIRE_START_TICK
                && elapsed <= BigLumpEntity.RANGED_FIRE_END_TICK;
        if (inFireWindow && this.shotsFired < BigLumpEntity.RANGED_SHOTS_PER_BURST) {
            int fireWindowTicks = BigLumpEntity.RANGED_FIRE_END_TICK - BigLumpEntity.RANGED_FIRE_START_TICK + 1;
            int ticksInWindow = elapsed - BigLumpEntity.RANGED_FIRE_START_TICK + 1;
            int expectedShotsByNow = (ticksInWindow * BigLumpEntity.RANGED_SHOTS_PER_BURST) / fireWindowTicks;

            if (this.shotCooldownTicks > 0) {
                this.shotCooldownTicks--;
            }

            while (this.shotsFired < expectedShotsByNow && this.shotCooldownTicks <= 0) {
                Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
                this.mob.fireMountedGun(targetPos);
                this.shotsFired++;
                this.shotCooldownTicks = this.shotIntervalTicks;
            }
        }

        this.shootAnimationTicks--;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
