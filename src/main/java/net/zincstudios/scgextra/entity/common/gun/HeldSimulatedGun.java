package net.zincstudios.scgextra.entity.common.gun;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.Config;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.interfaces.IProjectileFactory;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.util.GunEnchantmentHelper;
import top.ribs.scguns.util.GunModifierHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class HeldSimulatedGun implements SimulatedGun {

    protected final GunItem gunItem;
    protected final int fireRate;
    protected final int burstAmount;
    protected final int burstInterval;
    protected final float idealRange;
    protected final float maxRange;
    protected final TriggerStateSampler trigger;

    protected int burstCooldown = 0;
    protected int burstLeft = 0;
    protected int nextAttack = 0;  // tickCount timestamp

    public HeldSimulatedGun(GunItem gunItem) {
        this(gunItem, new MarkovTriggerSampler(0.9f, 0.9f));
    }

    public HeldSimulatedGun(GunItem gunItem, TriggerStateSampler trigger) {
        Gun gun = gunItem.getGun();
        this.gunItem = gunItem;
        this.fireRate = gun.getGeneral().getRate();
        this.burstAmount = gun.getGeneral().getBurstAmount();
        this.burstInterval = gun.getGeneral().getBurstCooldown();
        this.idealRange = (float) gun.getIdealAttackRange();
        this.maxRange = this.idealRange * 1.5f;
        this.trigger = trigger;
    }

    @Override
    public void tick(LivingEntity entity, LivingEntity target, boolean firing) {
        int tickCount = entity.tickCount;

        if (this.burstLeft > 0 && this.burstCooldown-- <= 0) {
//            fireProjectiles(entity, target);
            this.burstLeft--;
            this.burstCooldown = this.burstInterval;
            return;
        }

        if (this.nextAttack <= tickCount && firing && this.trigger.next(entity.getRandom())) {
//            fireProjectiles(entity, target);
            this.nextAttack = tickCount + this.fireRate;
        }
    }

    public void fireProjectiles(LivingEntity entity, LivingEntity target) {
        Level level = entity.level();
        Gun modifiedGun = this.gunItem.getGun();
        ItemStack itemStack = new ItemStack(this.gunItem);

        int count = modifiedGun.getProjectile().getProjectileAmount();
        Gun.Projectile projectileProps = modifiedGun.getProjectile();
        ProjectileEntity[] spawnedProjectiles = new ProjectileEntity[count];
//        if (entity.hasEffect(ModEffects.DEAFENED.get()) || entity.hasEffect(ModEffects.BLINDED.get())) {
//            accuracyModifier *= 0.5F;
//        }
//
//        if (target.hasEffect((MobEffect)ModEffects.DEAFENED.get())) {
//            accuracyModifier *= 0.75F;
//        }

        float difficultyDamageMultiplier = getDifficultyDamageMultiplier(level.getDifficulty());
        float configDamageMultiplier = Config.COMMON.gameplay.mobGunDamageMultiplier.get().floatValue();
        float finalDamageMultiplier = difficultyDamageMultiplier * configDamageMultiplier;
        Vec3 dir = getDirection(entity, target, itemStack, (GunItem)itemStack.getItem(), modifiedGun, 2.0F);


        for(int i = 0; i < count; ++i) {
            IProjectileFactory factory = ProjectileManager.getInstance().getFactory(BuiltInRegistries.ITEM.getKey((Item) Objects.requireNonNull(projectileProps.getItem())));
            ProjectileEntity projectileEntity = factory.create(level, entity, itemStack, (GunItem)itemStack.getItem(), modifiedGun);
            projectileEntity.setWeapon(itemStack);
            float originalDamage = Gun.getAdditionalDamage(itemStack);
            float scaledDamage = originalDamage * finalDamageMultiplier;
            projectileEntity.setAdditionalDamage(scaledDamage);
            projectileEntity.getPersistentData().putFloat("AIDamageScale", finalDamageMultiplier);
            double speedModifier = GunEnchantmentHelper.getProjectileSpeedModifier(itemStack);
            double speed = GunModifierHelper.getModifiedProjectileSpeed(itemStack, projectileEntity.getProjectile().getSpeed() * speedModifier);
            projectileEntity.setDeltaMovement(dir.x * speed, dir.y * speed, dir.z * speed);
            projectileEntity.updateHeading();
            double posX = entity.xOld + (entity.getX() - entity.xOld) / (double)2.0F;
            double posY = entity.yOld + (entity.getY() - entity.yOld) / (double)2.0F + (double)entity.getEyeHeight();
            double posZ = entity.zOld + (entity.getZ() - entity.zOld) / (double)2.0F;
            projectileEntity.setPos(posX, posY, posZ);
            level.addFreshEntity(projectileEntity);
            spawnedProjectiles[i] = projectileEntity;
            projectileEntity.tick();
        }
    }



    @Override
    public boolean hasChanged(LivingEntity entity) {
        return entity.getMainHandItem().getItem() != this.gunItem;
    }

    @Override
    public float getMaxRange() {
        return this.maxRange;
    }

    @Override
    public float getIdealRange() {
        return this.idealRange;
    }

    public static float getDifficultyDamageMultiplier(Difficulty difficulty) {
        float mult;
        switch (difficulty) {
            case PEACEFUL -> mult = 0.05F;
            case EASY -> mult = 0.35F;
            case NORMAL -> mult = 0.5F;
            case HARD -> mult = 0.65F;
            default -> throw new IncompatibleClassChangeError();
        }

        return mult;
    }

    public static Vec3 getDirection(LivingEntity shooter, LivingEntity target, ItemStack weapon, GunItem item, Gun modifiedGun, float accuracyModifier) {
        float gunSpread = GunModifierHelper.getModifiedSpread(weapon, modifiedGun.getProjectile().getSpread());
        float baseAimError = 5.0F;
        float var10000;
        switch (shooter.level().getDifficulty()) {
            case PEACEFUL -> var10000 = 3.0F;
            case EASY -> var10000 = 2.0F;
            case NORMAL -> var10000 = 1.5F;
            case HARD -> var10000 = 1.0F;
            default -> throw new IncompatibleClassChangeError();
        }

        float difficultyMod = var10000;
        float aimError = baseAimError * difficultyMod / accuracyModifier;
        aimError = Math.min(aimError, 25.0F);
        Vec3 baseDirection = getVectorFromRotation(shooter.getViewXRot(1.0F), shooter.getViewYRot(1.0F));
        if (shooter.level().getDifficulty() == Difficulty.HARD && target.getDeltaMovement().lengthSqr() > 0.01) {
            double speed = modifiedGun.getProjectile().getSpeed();
            Vec3 leadDir = getLeadingDirection(shooter, target, speed);
            baseDirection = baseDirection.add(leadDir.scale(0.3)).normalize();
        }

        float aimErrorRad = aimError * ((float)Math.PI / 180F);
        float theta1 = shooter.level().random.nextFloat() * 2.0F * (float)Math.PI;
        float r1 = Mth.sqrt(shooter.level().random.nextFloat()) * (float)Math.tan((double)aimErrorRad);
        Vec3 vecUpwards = getVectorFromRotation(shooter.getViewXRot(1.0F) + 90.0F, shooter.getViewYRot(1.0F));
        Vec3 vecSideways = baseDirection.cross(vecUpwards);
        float a1 = Mth.cos(theta1) * r1;
        float a2 = Mth.sin(theta1) * r1;
        Vec3 aimedDirection = baseDirection.add(vecSideways.scale((double)a1)).add(vecUpwards.scale((double)a2)).normalize();
        if (gunSpread == 0.0F) {
            return aimedDirection;
        } else {
            gunSpread = Math.min(gunSpread, 170.0F) * 0.5F * ((float)Math.PI / 180F);
            Vec3 spreadUpwards = getVectorFromRotation(shooter.getViewXRot(1.0F) + 90.0F, shooter.getViewYRot(1.0F));
            Vec3 spreadSideways = aimedDirection.cross(spreadUpwards);
            float theta2 = shooter.level().random.nextFloat() * 2.0F * (float)Math.PI;
            float r2 = Mth.sqrt(shooter.level().random.nextFloat()) * (float)Math.tan((double)gunSpread);
            float b1 = Mth.cos(theta2) * r2;
            float b2 = Mth.sin(theta2) * r2;
            return aimedDirection.add(spreadSideways.scale((double)b1)).add(spreadUpwards.scale((double)b2)).normalize();
        }
    }

    private static Vec3 getVectorFromRotation(float pitch, float yaw) {
        float f = Mth.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f1 = Mth.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f2 = -Mth.cos(-pitch * ((float)Math.PI / 180F));
        float f3 = Mth.sin(-pitch * ((float)Math.PI / 180F));
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Vec3 getLeadingDirection(LivingEntity shooter, LivingEntity target, double projectileSpeed) {
        Vec3 targetPos = target.position().add(0.0F, (double)target.getEyeHeight() * 0.8, (double)0.0F);
        Vec3 targetVelocity = target.getDeltaMovement();
        Vec3 shooterPos = shooter.position().add(0.0F, shooter.getEyeHeight(), (double)0.0F);
        Vec3 toTarget = targetPos.subtract(shooterPos);
        double distance = toTarget.length();
        double timeToHit = distance / projectileSpeed;
        Vec3 predictedPos = targetPos.add(targetVelocity.scale(timeToHit));
        return predictedPos.subtract(shooterPos).normalize();
    }
}
