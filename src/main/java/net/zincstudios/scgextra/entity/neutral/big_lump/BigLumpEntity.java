package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.sounds.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class BigLumpEntity extends Zombie implements GeoEntity {
    static final float RANGED_MAX_DISTANCE = 8.0F;
    static final float MELEE_ENGAGE_RANGE = 2.8F;

    static final int GUN_SHOT_INTERVAL_TICKS = 2;
    static final int GUN_MAGAZINE_SIZE = 20;
    static final int GUN_RELOAD_TICKS = 60;
    static final float GUN_DAMAGE = 4.0F;
    static final float GUN_INACCURACY = 8.0F;

    static final int MELEE_COOLDOWN_MIN_TICKS = 32;
    static final int MELEE_COOLDOWN_MAX_TICKS = 45;
    static final int MELEE_HIT_DELAY_TICKS = 5;
    static final int MELEE_ANIMATION_TICKS = 30;

    private static final EntityDataAccessor<Integer> RANGED_ANIM_TICKS =
            SynchedEntityData.defineId(BigLumpEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int meleeAnimationLockTicks;

    public BigLumpEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RANGED_ANIM_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 350.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
        this.setBaby(false);
        this.refreshDimensions();
        return data;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BigLumpMeleeGoal(this));
        this.goalSelector.addGoal(3, new BigLumpRangedAttackGoal(this, 1.05D, GUN_SHOT_INTERVAL_TICKS, RANGED_MAX_DISTANCE));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            this.tickRangedAnimation();
            this.tickMeleeAnimationLock();
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceBigLump) {
            return false;
        }
        if (!(level instanceof Level vanillaLevel)) {
            return false;
        }

        net.minecraft.core.BlockPos pos = this.blockPosition();
        return vanillaLevel.getBrightness(LightLayer.SKY, pos) > 0;
    }

    private void tickRangedAnimation() {
        int ticks = this.entityData.get(RANGED_ANIM_TICKS);
        if (ticks > 0) {
            this.entityData.set(RANGED_ANIM_TICKS, ticks - 1);
        }
    }

    private void tickMeleeAnimationLock() {
        if (this.meleeAnimationLockTicks > 0) {
            this.meleeAnimationLockTicks--;
        }
    }

    public boolean isValidTarget(LivingEntity target) {
        return target != null && target.isAlive() && !target.isRemoved();
    }

    public void startRangedAnimation(int ticks) {
        if (ticks <= 0) return;
        int current = this.entityData.get(RANGED_ANIM_TICKS);
        if (current < ticks) {
            this.entityData.set(RANGED_ANIM_TICKS, ticks);
        }
    }

    public void stopRangedAnimation() {
        this.entityData.set(RANGED_ANIM_TICKS, 0);
    }

    public boolean isRangedAnimationActive() {
        return this.entityData.get(RANGED_ANIM_TICKS) > 0;
    }

    public void startMeleeAnimationLock() {
        this.meleeAnimationLockTicks = MELEE_ANIMATION_TICKS;
    }

    public boolean isMeleeAnimationLocked() {
        return this.meleeAnimationLockTicks > 0;
    }

    public Vec3 getGunSpawnPos() {
        Vec3 look = this.getLookAngle();
        return new Vec3(this.getX(), this.getY() + 1.4D, this.getZ()).add(look.scale(1.2D));
    }

    public void fireMountedGun(LivingEntity target) {
        this.fireMountedGun(target.getEyePosition());
    }

    public void fireMountedGun(Vec3 targetPos) {
        Vec3 spawnVec = this.getGunSpawnPos();
        ArmoredWhaleProjectileEntity projectile = new ArmoredWhaleProjectileEntity(this.level(), this);
        projectile.setPos(spawnVec);
        projectile.setBaseDamage(GUN_DAMAGE);

        double dx = targetPos.x - spawnVec.x;
        double dy = targetPos.y - spawnVec.y;
        double dz = targetPos.z - spawnVec.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.atan2(dz, dx);
        float pitch = (float) Math.atan2(dy, horizontalDistance);

        float yawSpreadDegrees = GUN_INACCURACY * 3.5F;
        float pitchSpreadDegrees = GUN_INACCURACY * 2.2F;
        float spreadYaw = (this.getRandom().nextFloat() - 0.5F) * (float) Math.toRadians(yawSpreadDegrees);
        float spreadPitch = (this.getRandom().nextFloat() - 0.5F) * (float) Math.toRadians(pitchSpreadDegrees);
        float shotYaw = yaw + spreadYaw;
        float shotPitch = Mth.clamp(pitch + spreadPitch, (float) Math.toRadians(-80.0D), (float) Math.toRadians(80.0D));

        double shotX = Math.cos(shotPitch) * Math.cos(shotYaw);
        double shotY = Math.sin(shotPitch);
        double shotZ = Math.cos(shotPitch) * Math.sin(shotYaw);
        projectile.shoot(shotX, shotY, shotZ, 3.0F, 0.25F);

        this.level().addFreshEntity(projectile);
        this.level().playSound(
                null,
                spawnVec.x,
                spawnVec.y,
                spawnVec.z,
                top.ribs.scguns.init.ModSounds.BRUISER_SILENCED_FIRE.get(),
                SoundSource.HOSTILE,
                1.0F,
                0.9F + this.getRandom().nextFloat() * 0.2F
        );
    }

    public boolean isMeleeRange(LivingEntity target) {
        return target != null && target.isAlive() && this.distanceTo(target) <= MELEE_ENGAGE_RANGE;
    }

    public boolean canMeleeHit(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        double reachSqr = this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + target.getBbWidth();
        return this.distanceToSqr(target) <= reachSqr;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (state.isMoving() || this.getNavigation().isInProgress()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(new AnimationController<>(this, "ranged", 0, state -> {
            if (this.isRangedAnimationActive()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("ranged attack"));
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "melee", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("melee_attack", RawAnimation.begin().thenPlay("melee attack")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(3);
        if (roll == 0) return ModSounds.NEUTRAL_BIG_LUMP_IDLE_01.get();
        if (roll == 1) return ModSounds.NEUTRAL_BIG_LUMP_IDLE_02.get();
        return ModSounds.NEUTRAL_BIG_LUMP_IDLE_03.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? ModSounds.NEUTRAL_BIG_LUMP_HURT_01.get()
                : ModSounds.NEUTRAL_BIG_LUMP_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.NEUTRAL_BIG_LUMP_DEATH.get();
    }
}
