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
import net.zincstudios.scgextra.network.GunFlashMessage;
import net.zincstudios.scgextra.network.SCGEPacketHandler;
import top.ribs.scguns.Config;
import top.ribs.scguns.client.util.PropertyHelper;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.interfaces.IProjectileFactory;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.network.PacketHandler;
import top.ribs.scguns.network.message.S2CMessageBulletTrail;
import top.ribs.scguns.network.message.S2CMessageEntityCasingEject;
import top.ribs.scguns.network.message.S2CMessageEntityMuzzleFlash;
import top.ribs.scguns.util.GunEnchantmentHelper;
import top.ribs.scguns.util.GunModifierHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

// Doesn't do reload logic
@ParametersAreNonnullByDefault
public class HeldSimulatedGun implements SimulatedGun {

    protected final ItemStack gunStack;
    protected final int fireRate;
    protected final int burstAmount;
    protected final int burstInterval;
    protected final float idealRange;
    protected final float maxRange;
    private final ProjectileFactory projectileFactory;

    protected int burstCooldown = 0;
    protected int burstLeft = 0;
    protected int nextAttack = 0;  // tickCount timestamp

    public HeldSimulatedGun(ItemStack gunStack) {
        if (!(gunStack.getItem() instanceof GunItem gunItem)) {
            throw new IllegalArgumentException("gunStack is not a GunItem");
        }
        this.gunStack = gunStack;
        Gun gun = gunItem.getGun();
        this.fireRate = gun.getGeneral().getRate();
        this.burstAmount = gun.getGeneral().getBurstAmount();
        this.burstInterval = gun.getGeneral().getBurstCooldown();
        this.idealRange = (float) gun.getIdealAttackRange();
        this.maxRange = this.idealRange * 1.5f;
        IProjectileFactory projFac = ProjectileManager.getInstance().getFactory(
                ForgeRegistries.ITEMS.getKey(Objects.requireNonNull(gun.getProjectile().getItem())));
        this.projectileFactory = (level, entity, gunBase) ->
                projFac.create(level, entity, gunStack, gunItem, gunBase);
    }

    public HeldSimulatedGun(GunItem gunItem) {
        this(new ItemStack(gunItem));
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
            if (this.burstAmount > 1) {
                this.burstLeft = this.burstAmount-1;
                this.burstCooldown = this.burstInterval;
            }

            if (shooter instanceof Gunner gunner) {
                gunner.onGunFire(this ,targetPos);
            }
            return true;
        }

        return false;
    }

    public void fireProjectiles(LivingEntity shooter, Vec3 targetPos, float accuracyModifier) {
        Level level = shooter.level();
        GunItem gunItem = (GunItem) this.gunStack.getItem();
        Gun gun = gunItem.getGun();

        float damageMultiplier = SimulatedGun.getMobDamageMultiplier(level);
        float projectileDamage = Gun.getAdditionalDamage(this.gunStack) * damageMultiplier;
        double speedModifier = GunEnchantmentHelper.getProjectileSpeedModifier(this.gunStack);
        double speed = GunModifierHelper.getModifiedProjectileSpeed(this.gunStack, gun.getProjectile().getSpeed() * speedModifier);

        Vec3 startPos = shooter instanceof BulletSpawnOffset bso
                ? shooter.position().add(bso.getBulletSpawnOffset())
                : shooter.getEyePosition();
        Vec3 aimDir = SimulatedGun.getDirectionVector(startPos, targetPos);
        aimDir = SimulatedGun.addAimError(shooter, aimDir, accuracyModifier);

        int count = gun.getProjectile().getProjectileAmount();
        ProjectileEntity[] projectiles = new ProjectileEntity[count];

        for (int i = 0; i < count; ++i) {
            ProjectileEntity projectileEntity = this.projectileFactory.create(level, shooter, gun);
            projectileEntity.setWeapon(this.gunStack);
            projectileEntity.setAdditionalDamage(projectileDamage);
            projectileEntity.getPersistentData().putFloat("AIDamageScale", damageMultiplier);

            Vec3 dir = SimulatedGun.addWeaponSpread(shooter, aimDir, gun.getProjectile().getSpread());
            projectileEntity.setDeltaMovement(dir.x * speed, dir.y * speed, dir.z * speed);
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
        ParticleOptions data = GunEnchantmentHelper.getParticle(this.gunStack);
        boolean isVisible = !gun.getProjectile().shouldHideTrail();
        S2CMessageBulletTrail messageBulletTrail = new S2CMessageBulletTrail(projectiles, gun.getProjectile(), shooter.getId(), data, isVisible);
        PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), messageBulletTrail);
        if (gun.getDisplay().getFlash() != null) {
            SCGEPacketHandler.sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), new GunFlashMessage(shooter.getId(), 0));
        }

        if (Config.COMMON.gameplay.spawnCasings.get() && gun.getProjectile().ejectsCasing() && !gun.getProjectile().ejectDuringReload()) {
            ResourceLocation particleLocation = gun.getProjectile().getCasingParticle();
            if (particleLocation != null) {
                S2CMessageEntityCasingEject casingMessage = new S2CMessageEntityCasingEject(shooter.getId(), particleLocation);
                PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), casingMessage);
            }
        }
    }

    protected final void gunFireSound(Level level, Vec3 origin) {
        GunItem gunItem = (GunItem) this.gunStack.getItem();
        Gun gun = gunItem.getGun();
        ResourceLocation fireSound = gun.getSounds().getFire();
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
        return !ItemStack.matches(entity.getMainHandItem(), this.gunStack);
    }

    @Override
    public float getMaxRange() {
        return this.maxRange;
    }

    @Override
    public float getIdealRange() {
        return this.idealRange;
    }

    @Override
    public int getAmmoCapacity() {
        return 1;
    }

    @Override
    public int getAmmoCount() {
        return 1;
    }

    @Override
    public void setAmmoCount(int ammoCount) {}
}
