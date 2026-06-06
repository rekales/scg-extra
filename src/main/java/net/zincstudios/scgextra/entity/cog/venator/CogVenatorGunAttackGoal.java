package net.zincstudios.scgextra.entity.cog.venator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.asgharian.SimpleGunAttackGoal;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.ai.AIGunEvent;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

public class CogVenatorGunAttackGoal extends SimpleGunAttackGoal<CogVenatorEntity> {

    private int aimTicks = 0;

    public CogVenatorGunAttackGoal(CogVenatorEntity mob) {
        super(mob);
    }

    @Override
    protected boolean isHoldingGun() {
        return true;
    }

    @Override
    public void start() {
        this.mob.setAggressive(true);
        this.seeTime = 0;
    }

    @Override
    protected void tickAttack(LivingEntity target, double dist) {
        if (this.seeTime >= 10 && dist <= this.maxRange * 0.85) {
            if (!this.runAndGun) {
                this.mob.getNavigation().stop();
                this.path = null;
            }

            this.aimTicks = this.mob.getNavigation().isDone() ? this.aimTicks + 1 : 0;

            if (this.attackCooldown <= 0 && this.aimTicks >= 20) {
                this.setGoalState(FIRING_STATE);
                boolean continueAttack = handleAttack(target);
                if (!continueAttack) {
                    resetAttackCooldown();
                    this.setGoalState(AIMING_STATE);
                }
            } else {
                this.setGoalState(AIMING_STATE);
            }
        } else {
            this.aimTicks = 0;
        }
    }

    @Override
    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.HOWLER.get());

        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);
            AIGunEvent.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier());
            this.mob.triggerAnim("main", "fire");

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
        return 4.0F;
    }
}
