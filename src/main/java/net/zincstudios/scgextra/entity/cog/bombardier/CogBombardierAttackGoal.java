package net.zincstudios.scgextra.entity.cog.bombardier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.GoalState;
import net.zincstudios.scgextra.entity.asgharian.SimpleGunAttackGoal;
import net.zincstudios.scgextra.entity.common.MobUtil;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

public class CogBombardierAttackGoal extends SimpleGunAttackGoal<CogBombardierEntity> {

    public static final GoalState RELOADING_STATE = new GoalState("bombardier_reload_state");

    protected final int reloadTicks;
    protected final int ammoSize;

    protected int currentAmmo = 6;
    protected Vec3 fleePos = null;

    public CogBombardierAttackGoal(CogBombardierEntity mob, int reloadTicks, int ammoSize) {
        super(mob);
        this.reloadTicks = reloadTicks;
        this.ammoSize = ammoSize;
    }

    protected boolean isHoldingGun() {
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

        if (this.mob.closerThan(target, 5)
                || this.isReloading() && this.mob.closerThan(target, 12)) {
            if (this.fleePos == null) {
                this.fleePos = DefaultRandomPos.getPosAway(this.mob, 12, 3, target.position());
            }
        } else {
            this.fleePos = null;
        }

        if (this.fleePos != null) {
            this.mob.getNavigation().moveTo(this.fleePos.x, this.fleePos.y, this.fleePos.z , this.speedModifier * 1.3);
            if (this.mob.getNavigation().isDone()) {
                this.fleePos = null;
            }
        } else {
            if (distSqr > this.maxRange*this.maxRange || this.seeTime < 10) {
                // NOTE: maybe cache pathfinding if necessary
                this.mob.getNavigation().moveTo(target, this.speedModifier);
                this.setGoalState(APPROACH_STATE);
            } else if (distSqr <= this.approachDist * this.approachDist) {
                this.mob.getNavigation().stop();
            }

            if (this.seeTime >= 10 && distSqr <= this.maxRange*this.maxRange) {
                if (!runAndGun) {
                    this.mob.getNavigation().stop();
                }

                if (this.isReloading()) {
                    this.setGoalState(RELOADING_STATE);
                } else if (this.attackCooldown <= 0) {
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
    }

    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.ROCKET_RIFLE.get());

        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);

            MobUtil.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier(), new Vec3(0, this.mob.getEyeHeight(), 0));
            this.currentAmmo--;

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

    @Override
    protected float getAccuracyModifier() {
        return super.getAccuracyModifier() * 3F;
    }

    protected void resetAttackCooldown() {
        if (this.currentAmmo <= 0) {
            this.currentAmmo = this.ammoSize;
            this.attackCooldown = this.reloadTicks;
        } else {
            this.attackCooldown = this.attackInterval;
        }
    }

    protected boolean isReloading() {
        return this.attackCooldown > this.attackInterval;
    }
}
