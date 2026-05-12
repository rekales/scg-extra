package net.zincstudios.scgextra.entity.neutral.mutant_bat;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.sounds.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MutantBatEntity extends Monster implements GeoEntity {
    private static final int SCREAM_COOLDOWN_TICKS = 220;
    private static final int SCREAM_EFFECT_TICKS = 180;
    private static final int SCREAM_NAUSEA_AMPLIFIER = 2;
    private static final int SCREAM_ANIM_TICKS = 50; 
    private static final int MELEE_ATTACK_INTERVAL_TICKS = 42;
    private static final int BITE_LOCK_TICKS = 20;
    private static final int MELEE_HIT_DELAY_TICKS = 15; 
    private static final int DEATH_ANIM_TICKS = 40; 
    private static final float SCREAM_RANGE = 6.0F;
    private static final float SCREAM_DAMAGE = 3.0F;
    private static final double RUN_SPEED_MULTIPLIER = 0.4D;
    private static final double MELEE_CHASE_SPEED = 1.44D;
    private static final EntityDataAccessor<Integer> SCREAM_LOCK_TICKS =
            SynchedEntityData.defineId(MutantBatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RUN_ANIM =
            SynchedEntityData.defineId(MutantBatEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int screamCooldown;
    private LivingEntity pendingScreamTarget;
    private int pendingScreamDamageTicks = -1;
    private LivingEntity pendingMeleeTarget;
    private int pendingMeleeDamageTicks = -1;
    private int biteLockTicks;

    public MutantBatEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.FOLLOW_RANGE, 36.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCREAM_LOCK_TICKS, 0);
        this.entityData.define(RUN_ANIM, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MutantBatMeleeGoal(this, MELEE_CHASE_SPEED, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            return;
        }

        if (this.screamCooldown > 0) {
            this.screamCooldown--;
        }
        this.tickAnim(SCREAM_LOCK_TICKS);
        this.tickPendingScreamDamage();
        this.tickPendingMeleeDamage();
        if (this.biteLockTicks > 0) {
            this.biteLockTicks--;
        }
        if (this.isScreaming()) {
            this.getNavigation().stop();
            this.setSprinting(false);
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.faceCurrentTarget();
        }

        LivingEntity target = this.getTarget();
        boolean runAnim = !this.isScreaming()
                && target != null
                && target.isAlive()
                && this.distanceTo(target) > 4.0F;
        this.entityData.set(RUN_ANIM, runAnim);
        if (runAnim) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * RUN_SPEED_MULTIPLIER, motion.y, motion.z * RUN_SPEED_MULTIPLIER);
        }

        if (this.isScreaming() || target == null || !target.isAlive() || this.distanceTo(target) > SCREAM_RANGE) {
            return;
        }

        if (this.screamCooldown > 0 || this.entityData.get(SCREAM_LOCK_TICKS) > 0) {
            return;
        }

        this.screamCooldown = SCREAM_COOLDOWN_TICKS;
        this.startAnim(SCREAM_LOCK_TICKS, SCREAM_ANIM_TICKS);
        this.triggerAnim("scream", "scream_attack");
        this.playSound(ModSounds.NEUTRAL_MUTANT_BAT_SCREAM.get(), 1.2F, this.getVoicePitch());
        this.pendingScreamTarget = target;
        this.pendingScreamDamageTicks = 5 + this.random.nextInt(2);
    }

    private void tickAnim(EntityDataAccessor<Integer> key) {
        int ticks = this.entityData.get(key);
        if (ticks > 0) {
            this.entityData.set(key, ticks - 1);
        }
    }

    private void startAnim(EntityDataAccessor<Integer> key, int ticks) {
        int current = this.entityData.get(key);
        if (current < ticks) {
            this.entityData.set(key, ticks);
        }
    }

    private void tickPendingScreamDamage() {
        if (this.pendingScreamDamageTicks < 0) {
            return;
        }
        if (this.pendingScreamDamageTicks > 0) {
            this.pendingScreamDamageTicks--;
            return;
        }

        LivingEntity target = this.pendingScreamTarget;
        this.pendingScreamTarget = null;
        this.pendingScreamDamageTicks = -1;
        if (target == null || !target.isAlive() || this.distanceTo(target) > SCREAM_RANGE + 1.0F) {
            return;
        }

        target.hurt(this.damageSources().mobAttack(this), SCREAM_DAMAGE);
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, SCREAM_EFFECT_TICKS, SCREAM_NAUSEA_AMPLIFIER));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SCREAM_EFFECT_TICKS, 0));
    }

    private void scheduleMeleeDamage(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            this.pendingMeleeTarget = null;
            this.pendingMeleeDamageTicks = -1;
            return;
        }
        this.pendingMeleeTarget = target;
        this.pendingMeleeDamageTicks = MELEE_HIT_DELAY_TICKS;
    }

    private void tickPendingMeleeDamage() {
        if (this.pendingMeleeDamageTicks < 0) {
            return;
        }
        if (this.pendingMeleeDamageTicks > 0) {
            this.pendingMeleeDamageTicks--;
            return;
        }

        LivingEntity target = this.pendingMeleeTarget;
        this.pendingMeleeTarget = null;
        this.pendingMeleeDamageTicks = -1;
        if (target == null || !target.isAlive()) {
            return;
        }

        double reachSqr = this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + target.getBbWidth();
        if (this.distanceToSqr(target) <= reachSqr) {
            super.doHurtTarget(target);
        }
    }

    private boolean isScreaming() {
        return this.entityData.get(SCREAM_LOCK_TICKS) > 0;
    }

    private void faceCurrentTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }

        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.getYRot(), desiredYaw, 7.5F);
        this.setYRot(nextYaw);
        this.setYBodyRot(nextYaw);
        this.setYHeadRot(nextYaw);
        this.yBodyRotO = nextYaw;
        this.yHeadRotO = nextYaw;
    }

    private boolean canStartBiteAttack() {
        return !this.isScreaming() && this.biteLockTicks <= 0 && this.pendingMeleeDamageTicks < 0;
    }

    private void onBiteAttackStarted() {
        this.biteLockTicks = BITE_LOCK_TICKS;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return super.doHurtTarget(target);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_ANIM_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (this.isScreaming()) {
                return PlayState.STOP;
            }
            if (state.isMoving()) {
                if (this.entityData.get(RUN_ANIM)) {
                    state.getController().setAnimationSpeed(1.2);
                    return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                }
                state.getController().setAnimationSpeed(0.75);
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            state.getController().setAnimationSpeed(1.0);
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(new AnimationController<>(this, "scream", 0, state -> PlayState.STOP)
                .triggerableAnim("scream_attack", RawAnimation.begin().thenPlay("scream")));
        controllers.add(new AnimationController<>(this, "bite", 0, state -> PlayState.STOP)
                .triggerableAnim("bite_attack", RawAnimation.begin().thenPlay("bite")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        return true;
    }

    public static boolean checkMutantBatSpawnRules(EntityType<MutantBatEntity> entityType,
                                                   ServerLevelAccessor level,
                                                   MobSpawnType spawnType,
                                                   BlockPos pos,
                                                   RandomSource random) {
        if (pos.getY() >= level.getSeaLevel()) {
            return false;
        }
        int maxLight = 4;
        java.time.LocalDate date = java.time.LocalDate.now();
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        boolean isHalloweenSeason = (month == 10 && day >= 20) || (month == 11 && day <= 3);
        if (isHalloweenSeason) {
            maxLight = 7;
        }
        return level.getMaxLocalRawBrightness(pos) <= random.nextInt(maxLight)
                && net.minecraft.world.entity.Mob.checkMobSpawnRules(entityType, level, spawnType, pos, random)
                && random.nextFloat() * 100.0F < CommonConfig.spawnChanceMutantBat;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(3);
        if (roll == 0) return ModSounds.NEUTRAL_MUTANT_BAT_IDLE_01.get();
        if (roll == 1) return ModSounds.NEUTRAL_MUTANT_BAT_IDLE_02.get();
        return ModSounds.NEUTRAL_MUTANT_BAT_IDLE_03.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.NEUTRAL_MUTANT_BAT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.NEUTRAL_MUTANT_BAT_DEATH.get();
    }

    private static final class MutantBatMeleeGoal extends MeleeAttackGoal {
        private final MutantBatEntity bat;

        private MutantBatMeleeGoal(MutantBatEntity bat, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(bat, speedModifier, followingTargetEvenIfNotSeen);
            this.bat = bat;
        }

        @Override
        protected int getAttackInterval() {
            return MELEE_ATTACK_INTERVAL_TICKS;
        }

        @Override
        public boolean canUse() {
            return !this.bat.isScreaming() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bat.isScreaming() && super.canContinueToUse();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double reachSqr = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= reachSqr && this.getTicksUntilNextAttack() <= 0 && this.bat.canStartBiteAttack()) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.bat.onBiteAttackStarted();
                this.bat.triggerAnim("bite", "bite_attack");
                this.bat.scheduleMeleeDamage(enemy);
            }
        }
    }
}
