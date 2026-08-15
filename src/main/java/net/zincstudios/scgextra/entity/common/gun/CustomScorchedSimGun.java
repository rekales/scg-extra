package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.interfaces.IProjectileFactory;

import java.util.Objects;
import java.util.function.Function;

public class CustomScorchedSimGun extends ScorchedSimGun {

    private final boolean hasGunFlash;

    CustomScorchedSimGun(Gun gun, int fireRate, int burstAmount, int burstInterval, float projectileDamage,
                          double projectileSpeed, boolean reloads, int ammoCapacity, int reloadTime, float idealRange,
                          float maxRange, int gunIndex, boolean hasGunFlash,
                          ProjectileFactory projectileFactory, Function<Vec3, Vec3> velocityModifier) {
        super(
                gun,
                fireRate,
                burstAmount,
                burstInterval,
                projectileDamage,
                projectileSpeed,
                reloads,
                ammoCapacity,
                reloadTime,
                idealRange,
                maxRange,
                gunIndex,
                projectileFactory,
                velocityModifier
        );
        this.hasGunFlash = hasGunFlash;
    }

    @Override
    protected void gunShotFlash(LivingEntity shooter) {
        if (!this.hasGunFlash) return;
        super.gunShotFlash(shooter);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private final Gun gunBase;
        private int fireRate;
        private int burstAmount;
        private int burstInterval;
        private float projectileDamage;
        private double projectileSpeed;
        private boolean reloads;
        private int ammoCapacity;
        private int reloadTime;
        private float idealRange;
        private float maxRange;
        private int gunIndex;
        private boolean hasGunFlash;
        private ProjectileFactory projectileFactory;
        private Function<Vec3, Vec3> velocityModifier;

        public Builder(Gun gun) {
            this.gunBase = gun;
            this.fireRate = gun.getGeneral().getRate();
            this.burstAmount = gun.getGeneral().getBurstAmount();
            this.burstInterval = gun.getGeneral().getBurstCooldown();
            this.projectileDamage = gun.getProjectile().getDamage();
            this.projectileSpeed = gun.getProjectile().getSpeed();
            this.reloads = false;
            this.ammoCapacity = gun.getReloads().getMaxAmmo();
            this.reloadTime = gun.getReloads().getReloadTimer() *
                    (gun.getReloads().getReloadAmount() / gun.getReloads().getMaxAmmo());
            this.idealRange = -1;
            this.maxRange = -1;
            this.gunIndex = 0;
            this.hasGunFlash = true;
            IProjectileFactory projFac = ProjectileManager.getInstance().getFactory(
                    ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(gunBase.getProjectile().getItem())));
            this.projectileFactory = (level, entity, gunBase) ->
                    projFac.create(level, entity, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), gunBase);
            this.velocityModifier = vec3 -> vec3;
        }

        public CustomScorchedSimGun.Builder fireRate(int fireRate) {
            this.fireRate = fireRate; return this;
        }
        public CustomScorchedSimGun.Builder burstAmount(int burstAmount) {
            this.burstAmount = burstAmount; return this;
        }
        public CustomScorchedSimGun.Builder burstInterval(int burstInterval) {
            this.burstInterval = burstInterval; return this;
        }
        public CustomScorchedSimGun.Builder projectileDamage(float projectileDamage) {
            this.projectileDamage = projectileDamage; return this;
        }
        public CustomScorchedSimGun.Builder projectileSpeed(double projectileSpeed) {
            this.projectileSpeed = projectileSpeed; return this;
        }
        public CustomScorchedSimGun.Builder reloads() {
            this.reloads = true; return this;
        }
        public CustomScorchedSimGun.Builder ammoCapacity(int ammoCapacity) {
            this.ammoCapacity = ammoCapacity; return this;
        }
        public CustomScorchedSimGun.Builder reloadTime(int reloadTime) {
            this.reloadTime = reloadTime; return this;
        }
        public CustomScorchedSimGun.Builder idealRange(float idealRange) {
            this.idealRange = idealRange; return this;
        }
        public CustomScorchedSimGun.Builder maxRange(float maxRange) {
            this.maxRange = maxRange; return this;
        }
        public CustomScorchedSimGun.Builder gunIndex(int gunIndex) {
            this.gunIndex = gunIndex; return this;
        }
        public CustomScorchedSimGun.Builder noGunFlash() {
            this.hasGunFlash = false; return this;
        }
        public CustomScorchedSimGun.Builder projectileFactory(ProjectileFactory projectileFactory) {
            this.projectileFactory = projectileFactory; return this;
        }
        public CustomScorchedSimGun.Builder velocityModifier(Function<Vec3, Vec3> velocityModifier) {
            this.velocityModifier = velocityModifier; return this;
        }

        public CustomScorchedSimGun build() {
            if (this.idealRange < 0 && this.maxRange < 0) {
                this.idealRange = (float) gunBase.getIdealAttackRange();
                this.maxRange = this.idealRange * 1.4f;
            } else if (this.idealRange < 0 && this.maxRange >= 0) {
                this.idealRange = this.maxRange / 1.4f;
            } else if (this.idealRange >= 0 && this.maxRange < 0) {
                this.maxRange = this.idealRange * 1.4f;
            }

            return new CustomScorchedSimGun(
                    gunBase, fireRate, burstAmount, burstInterval, projectileDamage,
                    projectileSpeed, reloads, ammoCapacity, reloadTime, idealRange,
                    maxRange, gunIndex, hasGunFlash, projectileFactory, velocityModifier
            );
        }
    }
}
