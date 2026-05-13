package net.zincstudios.scgextra.entity.neutral.nitro_beetle;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NitroBeetleEntity extends Monster implements GeoEntity {
    private static final int FLY_PHASE_BASE_TICKS = 300; 
    private static final int FLY_PHASE_RANDOM_TICKS = 40; 
    private static final int WALK_PHASE_BASE_TICKS = 100; 
    private static final int WALK_PHASE_RANDOM_TICKS = 40; 

    private static final EntityDataAccessor<Boolean> FLY_ANIM =
            SynchedEntityData.defineId(NitroBeetleEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int phaseTicks;

    public NitroBeetleEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FLYING_SPEED, 0.35D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLY_ANIM, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            if (this.phaseTicks <= 0) {
                if (this.entityData.get(FLY_ANIM)) {
                    startWalkPhase();
                } else {
                    startFlyPhase();
                }
            }
            this.phaseTicks--;
            applyPhasePhysics();
        }

        if (!this.onGround()) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x, movement.y * 0.75D, movement.z);
        }
    }

    private void applyPhasePhysics() {
        if (this.entityData.get(FLY_ANIM)) {
            this.setNoGravity(true);
            enforceMinFlightHeightAboveGround(0.5D);
            return;
        }

        this.setNoGravity(false);
        if (!this.onGround()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.92D, Math.min(motion.y, -0.08D), motion.z * 0.92D);
        }
    }

    private void startFlyPhase() {
        this.entityData.set(FLY_ANIM, true);
        this.phaseTicks = FLY_PHASE_BASE_TICKS + this.random.nextInt(FLY_PHASE_RANDOM_TICKS + 1);
    }

    private void startWalkPhase() {
        this.entityData.set(FLY_ANIM, false);
        this.phaseTicks = WALK_PHASE_BASE_TICKS + this.random.nextInt(WALK_PHASE_RANDOM_TICKS + 1);
    }

    private void enforceMinFlightHeightAboveGround(double minHeight) {
        double groundY = findNearestGroundTopY(8);
        if (Double.isNaN(groundY)) {
            return;
        }

        double clearance = this.getY() - groundY;
        if (clearance >= minHeight) {
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        double pushUp = 0.08D + (minHeight - clearance) * 0.12D;
        this.setDeltaMovement(motion.x, Math.max(motion.y, pushUp), motion.z);
        this.hasImpulse = true;
    }

    private double findNearestGroundTopY(int maxDown) {
        int baseX = Mth.floor(this.getX());
        int baseY = Mth.floor(this.getY());
        int baseZ = Mth.floor(this.getZ());

        for (int offset = 1; offset <= maxDown; offset++) {
            BlockPos check = new BlockPos(baseX, baseY - offset, baseZ);
            if (!this.level().getBlockState(check).isAir()
                    && this.level().getBlockState(check).blocksMotion()) {
                return check.getY() + 1.0D;
            }
        }
        return Double.NaN;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.setSecondsOnFire(3);
        }
        return hit;
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource source) {
        if (!this.level().isClientSide()) {
            List<LivingEntity> targets = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(5.0D),
                    living -> living != this && living.isAlive()
            );
            for (LivingEntity living : targets) {
                living.hurt(this.damageSources().mobAttack(this), 3.0F);
                living.setSecondsOnFire(4);
            }

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);
        }
        super.die(source);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            boolean moving = state.isMoving() || this.getNavigation().isInProgress();
            if (this.entityData.get(FLY_ANIM)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }
            if (moving) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
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
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.isWaterAtOrBelow(level, this.blockPosition())) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceNitroBeetle) {
            return false;
        }
        return super.checkSpawnRules(level, spawnReason)
                && level.getBiome(this.blockPosition()).is(Biomes.CRIMSON_FOREST);
    }
}



