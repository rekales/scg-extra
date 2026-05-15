package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class BigLumpEntity extends Zombie implements GeoEntity {
    static final float RANGED_MAX_DISTANCE = 8.0F;
    static final float MELEE_ENGAGE_RANGE = 2.8F;
    static final float MELEE_DISENGAGE_RANGE = 3.6F;
    static final float RANGED_DISENGAGE_RANGE = 10.0F;
    static final int IDLE_RESUME_AFTER_COMBAT_TICKS = 140;

    static final int GUN_SHOT_INTERVAL_TICKS = 2;     
    static final int GUN_MAGAZINE_SIZE = 20;
    static final int GUN_RELOAD_TICKS = 60;
    static final int RANGED_SHOOT_WINDOW_TICKS = 50;  
    static final int RANGED_FIRE_START_TICK = 7;      
    static final int RANGED_FIRE_END_TICK = 40;       
    static final int RANGED_SHOTS_PER_BURST = 20;
    static final float GUN_DAMAGE = 4.0F;
    static final float GUN_INACCURACY = 8.0F;

    static final int MELEE_ATTACK_COOLDOWN_TICKS = 34;
    static final int MELEE_HIT_DELAY_TICKS = 5;
    static final int MELEE_ANIMATION_TICKS = 30;

    private static final EntityDataAccessor<Integer> RANGED_ANIM_TICKS =
            SynchedEntityData.defineId(BigLumpEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MELEE_ANIM_TICKS =
            SynchedEntityData.defineId(BigLumpEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int meleeCooldownTicks;
    private int pendingMeleeHitTicks = -1;
    private int rangedReloadTicks;
    private String headTurnReason = "init";
    private String bodyTurnReason = "init";
    private String movementReason = "init";
    private CombatIntent combatIntent = CombatIntent.NONE;
    private int lastCombatTick = -1000000;

    public BigLumpEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 25;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RANGED_ANIM_TICKS, 0);
        this.entityData.define(MELEE_ANIM_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
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
        net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.markNaturalSpawn(this, spawnType);
        this.setBaby(false);
        this.refreshDimensions();
        return data;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BigLumpMeleeGoal(this));
        this.goalSelector.addGoal(3, new BigLumpRangedAttackGoal(this, 1.05D, GUN_SHOT_INTERVAL_TICKS, RANGED_MAX_DISTANCE));
        this.goalSelector.addGoal(4, new BigLumpChaseGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new BigLumpIdleStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new BigLumpIdleLookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        tickRangedAnimation();
        tickMeleeState();
        updateCombatIntent();

        if (isRangedAnimationActive() || isMeleeAnimationLocked()) {
            this.getNavigation().stop();
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(0.0D, motion.y * 0.2D, 0.0D);
            this.movementReason = isRangedAnimationActive() ? "ranged_lock_stop" : "melee_lock_stop";
            this.bodyTurnReason = isRangedAnimationActive() ? "ranged_lock_hold" : "melee_lock_hold";
        }

    }

    private void tickRangedAnimation() {
        int ticks = this.entityData.get(RANGED_ANIM_TICKS);
        if (ticks > 0) {
            this.entityData.set(RANGED_ANIM_TICKS, ticks - 1);
        }
    }

    private void tickMeleeState() {
        int meleeTicks = this.entityData.get(MELEE_ANIM_TICKS);
        if (meleeTicks > 0) {
            this.entityData.set(MELEE_ANIM_TICKS, meleeTicks - 1);
        }
        if (this.meleeCooldownTicks > 0) {
            this.meleeCooldownTicks--;
        }
        if (this.pendingMeleeHitTicks > 0) {
            this.pendingMeleeHitTicks--;
        } else if (this.pendingMeleeHitTicks == 0) {
            this.pendingMeleeHitTicks = -1;
            LivingEntity target = this.getTarget();
            if (isValidTarget(target) && canMeleeHit(target)) {
                super.doHurtTarget(target);
            }
        }
        if (this.rangedReloadTicks > 0) {
            this.rangedReloadTicks--;
        }
    }

    public boolean isValidTarget(LivingEntity target) {
        return target != null && target.isAlive() && !target.isRemoved();
    }

    private void updateCombatIntent() {
        LivingEntity target = this.getTarget();
        if (!isValidTarget(target)) {
            this.combatIntent = CombatIntent.NONE;
            return;
        }
        this.lastCombatTick = this.tickCount;
        if (isMeleeAnimationLocked()) {
            this.combatIntent = CombatIntent.MELEE;
            return;
        }
        if (isRangedAnimationActive()) {
            this.combatIntent = CombatIntent.RANGED;
            return;
        }

        double dist = this.distanceTo(target);
        if (this.combatIntent == CombatIntent.MELEE) {
            if (dist <= MELEE_DISENGAGE_RANGE) {
                this.combatIntent = CombatIntent.MELEE;
                return;
            }
        }

        if (this.combatIntent == CombatIntent.RANGED) {
            if (dist <= RANGED_DISENGAGE_RANGE && dist > MELEE_DISENGAGE_RANGE && !isRangedReloading()) {
                this.combatIntent = CombatIntent.RANGED;
                return;
            }
        }

        if (dist <= MELEE_ENGAGE_RANGE && canStartMeleeAttack()) {
            this.combatIntent = CombatIntent.MELEE;
            return;
        }
        if (dist <= RANGED_MAX_DISTANCE && dist > MELEE_ENGAGE_RANGE && !isRangedReloading()) {
            this.combatIntent = CombatIntent.RANGED;
            return;
        }
        this.combatIntent = CombatIntent.CHASE;
    }

    public boolean shouldUseMeleeGoal() {
        LivingEntity target = this.getTarget();
        return isValidTarget(target) && this.combatIntent == CombatIntent.MELEE && canStartMeleeAttack();
    }

    public boolean shouldUseRangedGoal() {
        LivingEntity target = this.getTarget();
        return isValidTarget(target) && this.combatIntent == CombatIntent.RANGED;
    }

    public boolean shouldUseChaseGoal() {
        LivingEntity target = this.getTarget();
        return isValidTarget(target) && this.combatIntent == CombatIntent.CHASE;
    }

    public boolean shouldUseIdleGoals() {
        return !isInCombatOrRecentlyInCombat();
    }

    private boolean isInCombatOrRecentlyInCombat() {
        return this.combatIntent != CombatIntent.NONE
                || (this.tickCount - this.lastCombatTick) < IDLE_RESUME_AFTER_COMBAT_TICKS;
    }

    public void smoothFaceTarget(LivingEntity target, float headTurnRateDeg) {
        if (!isValidTarget(target)) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float desiredYaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float newHeadYaw = Mth.approachDegrees(this.getYHeadRot(), desiredYaw, headTurnRateDeg);
        this.setYHeadRot(newHeadYaw);
        this.yHeadRotO = newHeadYaw;
    }

    public boolean canStartMeleeAttack() {
        return this.entityData.get(MELEE_ANIM_TICKS) <= 0 && this.meleeCooldownTicks <= 0 && !isRangedAnimationActive();
    }

    public void startMeleeAttack(LivingEntity target) {
        this.entityData.set(MELEE_ANIM_TICKS, MELEE_ANIMATION_TICKS);
        this.meleeCooldownTicks = MELEE_ATTACK_COOLDOWN_TICKS;
        this.pendingMeleeHitTicks = MELEE_HIT_DELAY_TICKS;
    }

    public boolean isMeleeAnimationLocked() {
        return this.entityData.get(MELEE_ANIM_TICKS) > 0;
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

    public void startRangedReload(int ticks) {
        this.rangedReloadTicks = Math.max(this.rangedReloadTicks, Math.max(0, ticks));
    }

    public boolean isRangedReloading() {
        return this.rangedReloadTicks > 0;
    }

    public void markHeadTurnReason(String reason) {
        this.headTurnReason = reason;
    }

    public void markBodyTurnReason(String reason) {
        this.bodyTurnReason = reason;
    }

    public void markMovementReason(String reason) {
        this.movementReason = reason;
    }

    private enum CombatIntent {
        NONE,
        CHASE,
        RANGED,
        MELEE
    }

    public Vec3 getGunSpawnPos() {
        Vec3 look = this.getLookAngle();
        return new Vec3(this.getX(), this.getY() + 1.4D, this.getZ()).add(look.scale(1.2D));
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

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!(level instanceof ServerLevelAccessor serverLevel)) {
            return false;
        }
        if (!net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.hasOverworldGunProgression(serverLevel)) {
            return false;
        }
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.hasReachedNaturalSpawnCap(serverLevel, BigLumpEntity.class, 3)) {
            return false;
        }
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.isWaterAtOrBelow(level, this.blockPosition())) {
            return false;
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
        if (vanillaLevel.getBrightness(LightLayer.SKY, pos) <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (this.isRangedAnimationActive() || this.isMeleeAnimationLocked()) {
                return PlayState.STOP;
            }
            boolean moving = state.isMoving() || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D;
            if (moving) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(new AnimationController<>(this, "ranged", 0, state -> {
            if (this.isRangedAnimationActive()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("shoot attack"));
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "melee", 0, state -> {
            if (this.isMeleeAnimationLocked()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("melee attack"));
            }
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(3);
        if (roll == 0) return NeutralSounds.NEUTRAL_BIG_LUMP_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_BIG_LUMP_IDLE_02.get();
        return NeutralSounds.NEUTRAL_BIG_LUMP_IDLE_03.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_BIG_LUMP_HURT_01.get()
                : NeutralSounds.NEUTRAL_BIG_LUMP_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_BIG_LUMP_DEATH.get();
    }
}

