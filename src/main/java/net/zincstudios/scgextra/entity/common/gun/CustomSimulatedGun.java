package net.zincstudios.scgextra.entity.common.gun;

import com.mrcrayfish.framework.api.network.LevelLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.item.ModItems;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.interfaces.IProjectileFactory;
import top.ribs.scguns.network.PacketHandler;
import top.ribs.scguns.network.message.S2CMessageBulletTrail;
import top.ribs.scguns.network.message.S2CMessageEntityCasingEject;
import top.ribs.scguns.particles.TrailData;

import java.util.Objects;
import java.util.function.Function;

public class CustomSimulatedGun implements SimulatedGun {

    protected final Gun gunBase;
    protected final int fireRate;
    protected final int burstAmount;
    protected final int burstInterval;
    protected final double projectileSpeed;
    protected final float additionalDamage;
    protected final float idealRange;
    protected final float maxRange;
    protected final ProjectileFactory projectileFactory;
    protected final Function<Vec3, Vec3> velocityModifier;

    protected int burstCooldown = 0;
    protected int burstLeft = 0;
    protected int nextAttack = 0;  // tickCount timestamp

    protected CustomSimulatedGun(Gun gunBase, int fireRate, int burstAmount, int burstInterval,
                              double projectileSpeed, float projectileDamage, float idealRange,
                              float maxRange, ProjectileFactory projectileFactory, Function<Vec3, Vec3> velocityModifier) {
        this.gunBase = gunBase;
        this.fireRate = fireRate;
        this.burstAmount = burstAmount;
        this.burstInterval = burstInterval;
        this.projectileSpeed = projectileSpeed;
        this.additionalDamage = projectileDamage - gunBase.getProjectile().getDamage();
        this.idealRange = idealRange;
        this.maxRange = maxRange;
        this.projectileFactory = projectileFactory;
        this.velocityModifier = velocityModifier;
    }

    @Override
    public boolean tickFire(LivingEntity shooter, Vec3 targetPos, float accuracyModifier, boolean firing) {
        int tickCount = shooter.tickCount;

        if (this.burstLeft > 0 && this.burstCooldown-- <= 0) {
            fireProjectiles(shooter, targetPos, accuracyModifier);
            this.burstLeft--;
            this.burstCooldown = this.burstInterval;

            if (shooter instanceof Gunner gunner) {
                gunner.onGunFire(this ,targetPos);
            }
            return true;
        }

        if (this.nextAttack <= tickCount && firing) {
            fireProjectiles(shooter, targetPos, accuracyModifier);
            this.nextAttack = tickCount + this.fireRate;

            if (shooter instanceof Gunner gunner) {
                gunner.onGunFire(this ,targetPos);
            }
            return true;
        }

        return false;
    }

    public void fireProjectiles(LivingEntity shooter, Vec3 targetPos, float accuracyModifier) {
        Level level = shooter.level();
        Gun gun = this.gunBase;

        Vec3 startPos = shooter instanceof BulletSpawnOffset bso
                ? shooter.position().add(bso.getBulletSpawnOffset())
                : shooter.getEyePosition();
        Vec3 aimDir = SimulatedGun.getDirectionVector(startPos, targetPos);
        aimDir = SimulatedGun.addAimError(shooter, aimDir, accuracyModifier);

        int count = gun.getProjectile().getProjectileAmount();
        ProjectileEntity[] projectiles = new ProjectileEntity[count];

        for (int i = 0; i < count; ++i) {
            ProjectileEntity projectileEntity = this.projectileFactory.create(level, shooter, gun);
            projectileEntity.setAdditionalDamage(this.additionalDamage);

            Vec3 vec = SimulatedGun.addWeaponSpread(shooter, aimDir, gun.getProjectile().getSpread());
            vec = vec.scale(this.projectileSpeed);
            vec = this.velocityModifier.apply(vec);
            projectileEntity.setDeltaMovement(vec);
            projectileEntity.updateHeading();
            projectileEntity.setPos(startPos);

            level.addFreshEntity(projectileEntity);
            projectiles[i] = projectileEntity;
            projectileEntity.tick();
        }

        gunFireSound(level, startPos);

        int radius = (int)shooter.getX();
        int y1 = (int)(shooter.getY() + (double)1.0F);
        int z1 = (int)shooter.getZ();
        double r = Config.COMMON.network.projectileTrackingRange.get();
        ParticleOptions data = new TrailData(false);
        boolean isVisible = !gun.getProjectile().shouldHideTrail();
        S2CMessageBulletTrail messageBulletTrail = new S2CMessageBulletTrail(projectiles, gun.getProjectile(), shooter.getId(), data, isVisible);
        PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), messageBulletTrail);

//        if (gun.getDisplay().getFlash() != null) {
//            float randomValue = level.random.nextFloat();
//            Vec3 weaponOrigin = PropertyHelper.getModelOrigin(this.gunStack, PropertyHelper.GUN_DEFAULT_ORIGIN);
//            Vec3 flashPosition = PropertyHelper.getMuzzleFlashPosition(this.gunStack, gun).subtract(weaponOrigin);
//            S2CMessageEntityMuzzleFlash flashMessage = new S2CMessageEntityMuzzleFlash(shooter.getId(), randomValue, flashPosition, false);
//            PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), flashMessage);
//        }

        if (Config.COMMON.gameplay.spawnCasings.get() && gun.getProjectile().ejectsCasing() && !gun.getProjectile().ejectDuringReload()) {
            ResourceLocation particleLocation = gun.getProjectile().getCasingParticle();
            if (particleLocation != null) {
                S2CMessageEntityCasingEject casingMessage = new S2CMessageEntityCasingEject(shooter.getId(), particleLocation);
                PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), casingMessage);
            }
        }
    }

    protected final void gunFireSound(Level level, Vec3 origin) {
        ResourceLocation fireSound = this.gunBase.getSounds().getFire();
        if (fireSound != null) {
            float volume = (float) Config.COMMON.gameplay.mobGunfireVolume.get();
            float pitch = 0.9F + level.getRandom().nextFloat() * 0.2F;
            level.playSound(
                    null,
                    origin.x, origin.y, origin.z,
                    SoundEvent.createVariableRangeEvent(fireSound),
                    SoundSource.HOSTILE,
                    volume - 0.5F,
                    pitch
            );
        }
    }

    @Override
    public boolean hasChanged(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMaxRange() {
        return this.maxRange;
    }

    @Override
    public float getIdealRange() {
        return this.idealRange;
    }

    // because java is shit and without named default parameters
    @SuppressWarnings("unused")
    public static class Builder {
        private final Gun gunBase;
        private int fireRate;
        private int burstAmount;
        private int burstInterval;
        private double projectileSpeed;
        private float projectileDamage;
        private float idealRange;
        private float maxRange;
        private ProjectileFactory projectileFactory;
        private Function<Vec3, Vec3> velocityModifier;

        public Builder(Gun gunBase) {
            this.gunBase = gunBase;
            this.fireRate = gunBase.getGeneral().getRate();
            this.burstAmount = gunBase.getGeneral().getBurstAmount();
            this.burstInterval = gunBase.getGeneral().getBurstCooldown();
            this.projectileSpeed = gunBase.getProjectile().getSpeed();
            this.projectileDamage = gunBase.getProjectile().getDamage();
            this.idealRange = (float) gunBase.getIdealAttackRange();
            this.maxRange = this.idealRange * 1.5f;
            IProjectileFactory projFac = ProjectileManager.getInstance().getFactory(
                    ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(gunBase.getProjectile().getItem())));
            this.projectileFactory = (level, entity, gun) ->
                    projFac.create(level, entity, ItemStack.EMPTY, ModItems.PLACEHOLDER_GUN.get(), gun);
            this.velocityModifier = vec3 -> vec3;
        }

        public Builder fireRate(int fireRate) {
            this.fireRate = fireRate; return this;
        }
        public Builder burstAmount(int burstAmount) {
            this.burstAmount = burstAmount; return this;
        }
        public Builder burstInterval(int burstInterval) {
            this.burstInterval = burstInterval; return this;
        }
        public Builder projectileSpeed(double projectileSpeed) {
            this.projectileSpeed = projectileSpeed; return this;
        }
        public Builder projectileDamage(float projectileDamage) {
            this.projectileDamage = projectileDamage; return this;
        }
        public Builder idealRange(float idealRange) {
            this.idealRange = idealRange; return this;
        }
        public Builder maxRange(float maxRange) {
            this.maxRange = maxRange; return this;
        }
        public Builder projectileFactory(ProjectileFactory projectileFactory) {
            this.projectileFactory = projectileFactory; return this;
        }
        public Builder velocityModifier(Function<Vec3, Vec3> velocityModifier) {
            this.velocityModifier = velocityModifier; return this;
        }

        public CustomSimulatedGun build() {
            return new CustomSimulatedGun(this.gunBase, this.fireRate, this.burstAmount, this.burstInterval,
                    this.projectileSpeed, this.projectileDamage, this.idealRange, this.maxRange, this.projectileFactory, this.velocityModifier);
        }
    }
}
