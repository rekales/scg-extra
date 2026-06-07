package net.zincstudios.scgextra.entity.cog.vulture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.SimpleBurstGunAttackGoal;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.sounds.CogSounds;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

public class CogVultureAttackGoal extends SimpleBurstGunAttackGoal<CogVultureEntity> {

    public CogVultureAttackGoal(CogVultureEntity mob) {
        super(mob);
    }

    protected boolean isHoldingGun() {
        return true;
    }

    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.PRUSH_GUN.get());

        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);

            if (this.mob.getRandom().nextFloat() < 0.3) {
                this.mob.playSound(CogSounds.COG_VENATOR_ATTACK.get());
            }

            Vec3 spawnPosOffset = CogVultureEntity.LEFT_GUN_OFFSET.yRot(-this.mob.yBodyRot * Mth.DEG_TO_RAD);
            MobUtil.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier(), spawnPosOffset);

            spawnPosOffset = CogVultureEntity.RIGHT_GUN_OFFSET.yRot(-this.mob.yBodyRot * Mth.DEG_TO_RAD);
            MobUtil.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier(), spawnPosOffset);

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
        return super.getAccuracyModifier() / 1.2F;
    }
}
