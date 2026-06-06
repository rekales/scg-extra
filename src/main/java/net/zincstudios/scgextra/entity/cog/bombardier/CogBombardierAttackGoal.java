package net.zincstudios.scgextra.entity.cog.bombardier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.asgharian.SimpleGunAttackGoal;
import net.zincstudios.scgextra.entity.cog.centipede.PlasmaCannonProjectileEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.entity.ai.AIGunEvent;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.interfaces.IProjectileFactory;
import top.ribs.scguns.item.GunItem;

import java.util.Objects;

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

    @Override
    public void start() {
        super.start();
        this.aimTicks = 0;
    }

    protected void fireGun(LivingEntity target) {
        ItemStack itemStack = new ItemStack(ModItems.ROCKET_RIFLE.get());

        if (itemStack.getItem() instanceof GunItem gunItem) {
            Gun gun = gunItem.getModifiedGun(itemStack);
            Level level = this.mob.level();

            Gun.Projectile projectileProps = gun.getProjectile();
            IProjectileFactory factory = ProjectileManager.getInstance().getFactory(ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(projectileProps.getItem())));
            ProjectileEntity projectileEntity = factory.create(level, this.mob, itemStack, (GunItem)itemStack.getItem(), gun);
            projectileEntity.setWeapon(itemStack);
//            ProjectileEntity projectileEntity = new PlasmaCannonProjectileEntity(level, this.mob, itemStack, gunItem, gun);
            Vec3 dir = AIGunEvent.getDirection(this.mob, target, itemStack, (GunItem)itemStack.getItem(), gun, this.getAccuracyModifier());
            double speed = projectileEntity.getProjectile().getSpeed() / 4;
            projectileEntity.setDeltaMovement(dir.x * speed, dir.y * speed * 1.2 + 0.1, dir.z * speed);
            projectileEntity.updateHeading();
            double posX = this.mob.xOld + (this.mob.getX() - this.mob.xOld) / (double)2.0F;
            double posY = this.mob.yOld + (this.mob.getY() - this.mob.yOld) / (double)2.0F + this.mob.getEyeHeight();
            double posZ = this.mob.zOld + (this.mob.getZ() - this.mob.zOld) / (double)2.0F;
            projectileEntity.setPos(posX, posY, posZ);
            level.addFreshEntity(projectileEntity);
            projectileEntity.tick();

//            MobUtil.performGunAttack(this.mob, target, itemStack, gun, this.getAccuracyModifier(), new Vec3(0, this.mob.getEyeHeight(), 0));
            this.currentAmmo--;
            if (this.currentAmmo <= 0) {
                this.reloadEnd = this.mob.tickCount + this.reloadTicks;
                this.currentAmmo = this.ammoSize;
            }

            this.mob.triggerAnim("gun", "fire");

            ResourceLocation fireSound = gun.getSounds().getFire();
            if (fireSound != null) {
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
