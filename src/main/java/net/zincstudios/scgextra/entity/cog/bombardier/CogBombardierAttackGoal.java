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

    protected final int reloadTicks;
    protected final int ammoSize;

    protected int reloadEnd;  // tickCount timestamp
    protected int currentAmmo;
    private int aimTicks = 0;

    public CogBombardierAttackGoal(CogBombardierEntity mob, int reloadTicks, int ammoSize) {
        super(mob);
        this.reloadEnd = this.mob.tickCount;
        this.reloadTicks = reloadTicks;
        this.ammoSize = ammoSize;
        this.currentAmmo = ammoSize;
    }

    protected boolean isHoldingGun() {
        return true;
    }

    @Override
    protected void tickAttack(LivingEntity target, double dist) {
        if (this.seeTime >= 10 && dist <= this.maxRange) {
            if (!this.runAndGun) {
                this.mob.getNavigation().stop();
                this.path = null;
            }

            this.aimTicks = this.mob.getNavigation().isDone() ? this.aimTicks + 1 : 0;

            if (this.attackCooldown <= 0 && this.mob.tickCount > this.reloadEnd  && this.aimTicks >= 20) {
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

    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.ROCKET_RIFLE.get());

        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);

            MobUtil.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier(), new Vec3(0, this.mob.getEyeHeight(), 0));
            this.currentAmmo--;
            if (this.currentAmmo <= 0) {
                this.reloadEnd = this.mob.tickCount + this.reloadTicks;
                this.currentAmmo = this.ammoSize;
            }

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
}
