package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.mrcrayfish.framework.api.network.LevelLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import top.ribs.scguns.Config;
import top.ribs.scguns.client.util.PropertyHelper;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.interfaces.IProjectileFactory;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.network.PacketHandler;
import top.ribs.scguns.network.message.S2CMessageBulletTrail;
import top.ribs.scguns.network.message.S2CMessageEntityMuzzleFlash;
import top.ribs.scguns.util.GunEnchantmentHelper;
import top.ribs.scguns.util.GunModifierHelper;

// TODO: use CustomSimulatedGun instead
public class RocketBarrageSimGun implements SimulatedGun {

    protected final ItemStack gunStack;
    protected final int fireRate;
    private final IProjectileFactory projectileFactory;
    protected int nextAttack = 0;  // tickCount timestamp

    public RocketBarrageSimGun() {
        this.gunStack = new ItemStack(ModItems.ROCKET_RIFLE.get());
        this.fireRate = 5;
        this.projectileFactory = RocketBarrageProjectileEntity::new;
    }

    @Override
    public boolean tickFire(LivingEntity shooter, LivingEntity target, float accuracyModifier, boolean firing) {
        int tickCount = shooter.tickCount;

        if (this.nextAttack <= tickCount) {
            fireProjectiles(shooter, target, accuracyModifier);
            this.nextAttack = tickCount + this.fireRate;
            return true;
        }

        return false;
    }

    public void fireProjectiles(LivingEntity shooter, LivingEntity target, float accuracyModifier) {
        Level level = shooter.level();
        GunItem gunItem = (GunItem) this.gunStack.getItem();
        Gun gun = gunItem.getGun();

        float projectileDamage = 5;
        double speedModifier = GunEnchantmentHelper.getProjectileSpeedModifier(this.gunStack);
        double speed = GunModifierHelper.getModifiedProjectileSpeed(this.gunStack, gun.getProjectile().getSpeed() * speedModifier);
        speed = speed/2;

        Vec3 startPos = shooter instanceof BulletSpawnOffset bso
                ? shooter.position().add(bso.getBulletSpawnOffset())
                : shooter.getEyePosition();
        Vec3 targetPos = SimulatedGun.getCenterMassPos(target);
        Vec3 aimDir = SimulatedGun.getDirectionVector(startPos, targetPos);
        aimDir = SimulatedGun.addAimError(shooter, aimDir, accuracyModifier);

        int count = gun.getProjectile().getProjectileAmount();
        ProjectileEntity[] projectiles = new ProjectileEntity[count];

        for (int i = 0; i < count; ++i) {
            ProjectileEntity projectileEntity = this.projectileFactory.create(level, shooter, this.gunStack, gunItem, gun);
            projectileEntity.setWeapon(this.gunStack);
            projectileEntity.setAdditionalDamage(projectileDamage);
            projectileEntity.getPersistentData().putFloat("AIDamageScale", 1.0F);

            Vec3 dir = SimulatedGun.addWeaponSpread(shooter, aimDir, gun.getProjectile().getSpread());
            projectileEntity.setDeltaMovement(dir.x * speed, dir.y * speed, dir.z * speed);
            projectileEntity.updateHeading();
            projectileEntity.setPos(startPos);

            level.addFreshEntity(projectileEntity);
            projectiles[i] = projectileEntity;
            projectileEntity.tick();
        }

        int radius = (int)shooter.getX();
        int y1 = (int)(shooter.getY() + (double)1.0F);
        int z1 = (int)shooter.getZ();
        double r = Config.COMMON.network.projectileTrackingRange.get();
        ParticleOptions data = GunEnchantmentHelper.getParticle(this.gunStack);
        boolean isVisible = !gun.getProjectile().shouldHideTrail();
        S2CMessageBulletTrail messageBulletTrail = new S2CMessageBulletTrail(projectiles, gun.getProjectile(), shooter.getId(), data, isVisible);
        PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), messageBulletTrail);
        if (gun.getDisplay().getFlash() != null) {
            float randomValue = level.random.nextFloat();
            Vec3 weaponOrigin = PropertyHelper.getModelOrigin(this.gunStack, PropertyHelper.GUN_DEFAULT_ORIGIN);
            Vec3 flashPosition = PropertyHelper.getMuzzleFlashPosition(this.gunStack, gun).subtract(weaponOrigin);
            S2CMessageEntityMuzzleFlash flashMessage = new S2CMessageEntityMuzzleFlash(shooter.getId(), randomValue, flashPosition, false);
            PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create(level, radius, y1, z1, r), flashMessage);
        }
    }

    @Override
    public boolean hasChanged(LivingEntity shooter) {
        return false;
    }

    @Override
    public float getMaxRange() {
        return 25;
    }

    @Override
    public float getIdealRange() {
        return 15;
    }
}
