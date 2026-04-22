package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.ai.AIGunEvent;
import top.ribs.scguns.item.GunItem;

import java.security.InvalidParameterException;
import java.util.EnumSet;

// Relying heavily on the builder pattern because overloads fucking suck for multiple optionals

/**
 * A much more simplified and dumbed down version of the GunAttackGoal. No reloading, going into cover, or panicking.
 *
 * @see net.zincstudios.scgextra.mixin.GunnerMobSpawnerMixin for mixin that disables dynamic addition of GunAttackGoal.
 */
public class SimpleGunAttackGoal<T extends PathfinderMob> extends Goal {

    public static final String FIRING_STATE = "simple_gun_firing_state";
    public static final String AIMING_STATE = "simple_gun_aiming_state";
    public static final String IDLE_STATE = "simple_gun_idle_state";
    public static final String APPROACH_STATE = "simple_gun_approach_state";

    protected final T mob;
    protected double speedModifier = 1;
    protected int attackInterval = 20;
    protected double approachDist = 6;
    protected double maxRange = 12;
    protected boolean runAndGun = false;

    protected int attackCooldown = 0;
    protected int seeTime = 0;
    protected String goalState = IDLE_STATE;

    public SimpleGunAttackGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() != null
                && this.isHoldingGun()
                && !this.mob.getTarget().isDeadOrDying();
    }

    public void start() {
        this.mob.setAggressive(true);
        this.seeTime = 0;
        resetAttackCooldown();
    }

    public void stop() {
        this.mob.setAggressive(false);
        this.setGoalState(IDLE_STATE);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.attackCooldown -= this.attackCooldown > 0 ? 1 : 0;

        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        double distSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean lineOfSight = this.mob.getSensing().hasLineOfSight(target);

        if (lineOfSight) {
            this.seeTime += seeTime < 40 ? 1 : 0;
        } else {
            this.seeTime -= seeTime > 0 ? 1 : 0;
        }

        if (distSqr > this.maxRange*this.maxRange || this.seeTime < 10) {
            // NOTE: maybe cache pathfinding if necessary
            this.mob.getNavigation().moveTo(target, this.speedModifier);
            this.setGoalState(APPROACH_STATE);
        }  if (distSqr <= this.approachDist) {
            this.mob.getNavigation().stop();
        }

        if (this.seeTime >= 10 && distSqr <= this.maxRange*this.maxRange) {
            if (!runAndGun) {
                this.mob.getNavigation().stop();
            }

            if (this.attackCooldown <= 0) {
                this.setGoalState(FIRING_STATE);
                boolean continueAttack = handleAttack(target);
                if (!continueAttack) {
                    resetAttackCooldown();
                    this.setGoalState(AIMING_STATE);
                }
            } else {
                this.setGoalState(AIMING_STATE);
            }
        }
    }

    /**
     * @return false to stop the attack and reset the cooldown
     */
    protected boolean handleAttack(LivingEntity target) {
        this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        fireGun(target);
        return false;
    }

    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = this.mob.getMainHandItem();
        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);
            SCGExtra.LOGGER.debug("fired: " + this.mob.tickCount);
            AIGunEvent.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier());

            ResourceLocation fireSound = gun.getSounds().getFire();
            if (fireSound != null) {
                double posX = this.mob.getX();
                double posY = this.mob.getY() + (double)this.mob.getEyeHeight();
                double posZ = this.mob.getZ();
                float volume = (float) Config.COMMON.gameplay.mobGunfireVolume.get();
                float pitch = 0.9F + this.mob.level().random.nextFloat() * 0.2F;
                this.mob.level().playSound(null, posX, posY, posZ, SoundEvent.createVariableRangeEvent(fireSound), SoundSource.HOSTILE, volume - 0.5F, pitch);
            }
        }


    }

    protected float getAccuracyModifier() {
        return runAndGun ? 2f : 3.2f;
    }

    protected boolean isHoldingGun() {
        return this.mob.isHolding((itemStack) -> itemStack.getItem() instanceof GunItem);
    }

    protected void resetAttackCooldown() {
        int randRange = this.attackInterval/4;
        this.attackCooldown = this.mob.getRandom().nextIntBetweenInclusive(this.attackInterval - randRange, this.attackInterval + randRange);
    }

    protected void setGoalState(String goalState) {
        this.goalState = goalState;
        if (this.mob instanceof GoalStateHandler goalStateHandler) {
            goalStateHandler.onGoalStateChanged(this, goalState);
        }
    }

    public String getGoalState() {
        return this.goalState;
    }

    // Factory methods
    public SimpleGunAttackGoal<T> speedModifier(double speedModifier) {
        this.speedModifier = speedModifier;
        return this;
    }

    public SimpleGunAttackGoal<T> attackInterval(int intervalTicks) {
        this.attackInterval = intervalTicks;
        return this;
    }

    /**
     * The mob will try to pathfind to the target until it reaches the approachDist
     */
    public SimpleGunAttackGoal<T> approachDist(double approachDist) {
        if (approachDist > this.maxRange)
            throw new InvalidParameterException("approachDist must be smaller or equal to maxRange");

        this.approachDist = approachDist;
        return this;
    }

    /**
     * The mob will try to get closer when farther than the maxRange
     */
    public SimpleGunAttackGoal<T> maxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    public SimpleGunAttackGoal<T> runAndGun() {
        this.runAndGun = true;
        return this;
    }
}
