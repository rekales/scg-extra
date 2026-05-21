package net.zincstudios.scgextra.entity.neutral.netherite_eater;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class NetheriteEaterEntity extends Monster implements GeoEntity {
    private static final int ATTACK_FIRST_HIT_DELAY_TICKS = 10; 
    private static final int ATTACK_SECOND_HIT_DELAY_TICKS = 18; 
    private static final int ATTACK_LOCK_TICKS = 30;
    private static final int ATTACK_COOLDOWN_TICKS = 34;
    private static final int ATTACK_ANIM_TICKS = 30;
    private static final int BREATH_ANIM_TICKS = 77; 
    private static final int BREATH_COOLDOWN_TICKS = 300; 
    private static final float BREATH_RANGE = 4.0F;
    private static final int BREATH_ACTIVE_TICKS = 77; 
    private static final double ATTACK_EXTRA_REACH_BLOCKS = 1.0D;

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_LOCK =
            SynchedEntityData.defineId(NetheriteEaterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BREATH_ANIM_LOCK =
            SynchedEntityData.defineId(NetheriteEaterEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final double MAX_CLIMB_HEIGHT = 3.0D;
    private static final int CLIMB_NO_DIG_TICKS = 20;
    private static final int CLIMB_COOLDOWN_TICKS = 10;
    private int breathCooldown;
    private int breathActiveTicks;
    private int digCooldown;
    private int climbNoDigTicks;
    private int climbCooldownTicks;
    private boolean runningForAnim;
    private int attackLockTicks;
    private int attackCooldownTicks;
    private LivingEntity pendingAttackTarget;
    private int pendingFirstHitTicks = -1;
    private int pendingSecondHitTicks = -1;
    private boolean actionYawLocked;
    private float actionLockedYaw;

    public NetheriteEaterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(3.0F);
        this.xpReward = 60;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
        NeutralCombatUtil.markNaturalSpawn(this, spawnType);
        return data;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_ANIM_LOCK, 0);
        this.entityData.define(BREATH_ANIM_LOCK, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new NetheriteEaterMeleeGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Hoglin.class, true));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (isBreathAnimActive()) {
            return false;
        }
        if (this.attackLockTicks > 0 || this.attackCooldownTicks > 0) {
            return false;
        }
        if (!(target instanceof LivingEntity living) || !living.isAlive()) {
            return false;
        }

        this.attackLockTicks = ATTACK_LOCK_TICKS;
        this.attackCooldownTicks = ATTACK_COOLDOWN_TICKS;
        startAnimLock(ATTACK_ANIM_LOCK, ATTACK_ANIM_TICKS);
        this.triggerAnim("attack", "attack");
        this.scheduleTwoHitAttack(living);
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (breathActiveTicks > 0) {
            breathActiveTicks--;
            applyBreathPulse();
        } else if (breathCooldown > 0) {
            breathCooldown--;
        } else if (target != null && target.isAlive()
                && this.distanceTo(target) <= BREATH_RANGE
                && !isAttackAnimActive()) {
            breathCooldown = BREATH_COOLDOWN_TICKS;
            breathActiveTicks = BREATH_ACTIVE_TICKS;
            startAnimLock(BREATH_ANIM_LOCK, BREATH_ANIM_TICKS);
            this.triggerAnim("breath", "fire_breath");
            this.playSound(NeutralSounds.NEUTRAL_NETHERITE_EATER_BREATH.get(), 1.4F, this.getVoicePitch());
        }

        if (isBreathAnimActive()) {
            this.runningForAnim = false;
            this.setSprinting(false);
            this.getNavigation().stop();
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(0.0D, motion.y * 0.2D, 0.0D);
            tickActionRotationLock();
            this.tickAttackState();
            this.tickClimbCooldowns();
            tickAnimLock(ATTACK_ANIM_LOCK);
            tickAnimLock(BREATH_ANIM_LOCK);
            return;
        }

        this.runningForAnim = target != null && target.isAlive() && this.distanceTo(target) > 4.5F;
        this.setSprinting(this.runningForAnim);
        tickActionRotationLock();
        tickAnimLock(ATTACK_ANIM_LOCK);
        tickAnimLock(BREATH_ANIM_LOCK);
        this.tickAttackState();
        this.tickClimbCooldowns();
        boolean climbingThisTick = this.tryClimbTowardTarget(target);

        if (climbingThisTick || this.climbNoDigTicks > 0) {
            if (digCooldown > 0) {
                digCooldown--;
            }
            return;
        }

        if (digCooldown > 0) {
            digCooldown--;
        } else if (target != null && target.isAlive() && this.distanceTo(target) < 20.0F) {
            digCooldown = 10;
            breakBlocksToward(target);
        }
    }

    private void scheduleTwoHitAttack(LivingEntity target) {
        this.pendingAttackTarget = target;
        this.pendingFirstHitTicks = ATTACK_FIRST_HIT_DELAY_TICKS;
        this.pendingSecondHitTicks = ATTACK_SECOND_HIT_DELAY_TICKS;
    }

    private void startAnimLock(EntityDataAccessor<Integer> key, int ticks) {
        int current = this.entityData.get(key);
        if (current < ticks) {
            this.entityData.set(key, ticks);
        }
    }

    private void tickAnimLock(EntityDataAccessor<Integer> key) {
        int ticks = this.entityData.get(key);
        if (ticks > 0) {
            this.entityData.set(key, ticks - 1);
        }
    }

    private boolean isAttackAnimActive() {
        return this.entityData.get(ATTACK_ANIM_LOCK) > 0;
    }

    private boolean isBreathAnimActive() {
        return this.entityData.get(BREATH_ANIM_LOCK) > 0;
    }

    private void tickActionRotationLock() {
        boolean actionActive = isAttackAnimActive() || isBreathAnimActive();
        if (!actionActive) {
            this.actionYawLocked = false;
            return;
        }

        if (!this.actionYawLocked) {
            this.actionLockedYaw = this.getYRot();
            this.actionYawLocked = true;
        }

        this.setYRot(this.actionLockedYaw);
        this.setYBodyRot(this.actionLockedYaw);
        this.setYHeadRot(this.actionLockedYaw);
        this.yRotO = this.actionLockedYaw;
        this.yBodyRotO = this.actionLockedYaw;
        this.yHeadRotO = this.actionLockedYaw;
    }

    private void tickAttackState() {
        if (this.attackLockTicks > 0) {
            this.attackLockTicks--;
        }
        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }

        LivingEntity target = this.pendingAttackTarget;
        if (target == null || !target.isAlive()) {
            this.pendingAttackTarget = null;
            this.pendingFirstHitTicks = -1;
            this.pendingSecondHitTicks = -1;
            return;
        }

        if (this.pendingFirstHitTicks > 0) {
            this.pendingFirstHitTicks--;
        } else if (this.pendingFirstHitTicks == 0) {
            performTimedHit(target);
            this.pendingFirstHitTicks = -1;
        }

        if (this.pendingSecondHitTicks > 0) {
            this.pendingSecondHitTicks--;
        } else if (this.pendingSecondHitTicks == 0) {
            performTimedHit(target);
            this.pendingSecondHitTicks = -1;
        }

        if (this.pendingFirstHitTicks < 0 && this.pendingSecondHitTicks < 0) {
            this.pendingAttackTarget = null;
        }
    }

    private void performTimedHit(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return;
        }
        double baseReach = this.getBbWidth() * 2.0D + target.getBbWidth();
        double reach = baseReach + ATTACK_EXTRA_REACH_BLOCKS;
        if (this.distanceToSqr(target) > reach * reach) {
            return;
        }

        target.invulnerableTime = 0;
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            NeutralCombatUtil.applyLacerate(target, 60);
        }
    }

    public boolean isRunningForAnim() {
        return this.runningForAnim;
    }

    private void applyBreathPulse() {
        ServerLevel server = (ServerLevel) this.level();
        server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(), this.getY() + 1.2D, this.getZ(), 10, 1.5D, 0.5D, 1.5D, 0.02D);

        if (breathActiveTicks % 9 != 0) {
            return;
        }

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(5.0D),
                e -> e != this && e.isAlive()
        );
        for (LivingEntity living : targets) {
            living.hurt(this.damageSources().mobAttack(this), 4.0F);
            living.setSecondsOnFire(4);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (isAttackAnimActive() || isBreathAnimActive()) {
                return PlayState.STOP;
            }
            boolean moving = state.isMoving()
                    || this.getNavigation().isInProgress()
                    || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D;
            if (moving) {
                if (this.isSprinting()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                }
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
        controllers.add(new AnimationController<>(this, "breath", 0, state -> PlayState.STOP)
                .triggerableAnim("fire_breath", RawAnimation.begin().thenPlay("fire_breath")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private void tryBreakSoftBlock(BlockPos pos) {
        if (this.level().isClientSide()) return;
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(this.level(), pos) < 0.0F) return;
        if (state.is(BlockTags.WITHER_IMMUNE)) return;
        if (!canBreakWithIronPickaxeOnly(state)) return;
        this.level().destroyBlock(pos, true, this);
    }

    private boolean canBreakWithIronPickaxeOnly(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    private void breakBlocksToward(LivingEntity target) {
        double vx = target.getX() - this.getX();
        double vz = target.getZ() - this.getZ();
        double len = Math.sqrt(vx * vx + vz * vz);
        if (len < 0.001D) {
            return;
        }

        double dx = vx / len;
        double dz = vz / len;
        double px = -dz;
        double pz = dx;

        double baseX = this.getX() + dx * 1.35D;
        double baseZ = this.getZ() + dz * 1.35D;
        int baseY = this.blockPosition().getY();

        for (int side = -1; side <= 1; side++) {
            double sx = baseX + px * side * 0.9D;
            double sz = baseZ + pz * side * 0.9D;
            BlockPos front = BlockPos.containing(sx, baseY, sz);
            tryBreakSoftBlock(front);
            tryBreakSoftBlock(front.above());
            tryBreakSoftBlock(front.above(2));
        }
    }

    private void tickClimbCooldowns() {
        if (this.climbNoDigTicks > 0) {
            this.climbNoDigTicks--;
        }
        if (this.climbCooldownTicks > 0) {
            this.climbCooldownTicks--;
        }
    }

    private boolean tryClimbTowardTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        double dy = target.getY() - this.getY();
        if (dy <= 0.6D || dy > MAX_CLIMB_HEIGHT + 0.6D) {
            return false;
        }

        double horizontalDistSqr = this.distanceToSqr(target.getX(), this.getY(), target.getZ());
        if (horizontalDistSqr > 16.0D || this.climbCooldownTicks > 0) {
            return false;
        }

        if (!this.onGround() || !this.horizontalCollision) {
            return false;
        }

        Vec3 toward = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (toward.lengthSqr() < 1.0E-4D) {
            toward = this.getLookAngle();
        } else {
            toward = toward.normalize();
        }

        Vec3 current = this.getDeltaMovement();
        this.setDeltaMovement(
                current.x * 0.45D + toward.x * 0.10D,
                Math.max(current.y, 0.24D),
                current.z * 0.45D + toward.z * 0.10D
        );
        this.hasImpulse = true;
        this.climbNoDigTicks = CLIMB_NO_DIG_TICKS;
        this.climbCooldownTicks = CLIMB_COOLDOWN_TICKS;
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (NeutralCombatUtil.isManualSpawn(spawnReason)) {
            return true;
        }
        BlockPos pos = this.blockPosition();
        if (NeutralCombatUtil.isWaterAtOrBelow(level, pos)) {
            return false;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (!(level instanceof ServerLevelAccessor serverLevel)) {
            return false;
        }
        if (!NeutralCombatUtil.passesSpawnChance(this.random, CommonConfig.spawnChanceNetheriteEater)) {
            return false;
        }
        if (!level.getBiome(pos).is(Biomes.NETHER_WASTES)) {
            return false;
        }
        if (!NeutralCombatUtil.hasSolidGroundBelow(level, pos)) {
            return false;
        }
        if (NeutralCombatUtil.hasReachedNaturalSpawnCap(
                serverLevel,
                NetheriteEaterEntity.class,
                2,
                pos,
                192.0D,
                96.0D
        )) {
            return false;
        }
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(4);
        if (roll == 0) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_02.get();
        if (roll == 2) return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_03.get();
        return NeutralSounds.NEUTRAL_NETHERITE_EATER_IDLE_04.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_NETHERITE_EATER_HURT_01.get()
                : NeutralSounds.NEUTRAL_NETHERITE_EATER_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_NETHERITE_EATER_DEATH.get();
    }

    private static final class NetheriteEaterMeleeGoal extends Goal {
        private static final double CHASE_SPEED = 1.15D;
        private static final int REPATH_INTERVAL_TICKS = 4;
        private static final double TARGET_MOVE_REPATH_DISTANCE_SQR = 1.0D;

        private final NetheriteEaterEntity eater;
        private int repathCooldown;
        private double lastTargetX;
        private double lastTargetY;
        private double lastTargetZ;

        private NetheriteEaterMeleeGoal(NetheriteEaterEntity eater) {
            this.eater = eater;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.eater.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.eater.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.eater.getTarget();
            if (target == null || !target.isAlive()) {
                return;
            }

            this.eater.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.eater.isBreathAnimActive() || this.eater.isAttackAnimActive()) {
                this.eater.getNavigation().stop();
                return;
            }

            boolean canDirectChase = this.eater.hasLineOfSight(target)
                    && Math.abs(target.getY() - this.eater.getY()) < 1.5D;
            if (canDirectChase) {
                this.eater.getNavigation().stop();
                this.eater.getMoveControl().setWantedPosition(target.getX(), this.eater.getY(), target.getZ(), CHASE_SPEED);
                this.repathCooldown = 0;
            } else {
                if (this.repathCooldown > 0) {
                    this.repathCooldown--;
                }
                if (shouldRepath(target)) {
                    this.eater.getNavigation().moveTo(target, CHASE_SPEED);
                    this.repathCooldown = REPATH_INTERVAL_TICKS;
                    this.lastTargetX = target.getX();
                    this.lastTargetY = target.getY();
                    this.lastTargetZ = target.getZ();
                }
            }

            if (this.eater.attackLockTicks > 0 || this.eater.attackCooldownTicks > 0) {
                return;
            }

            double reach = this.eater.getBbWidth() * 2.0D + target.getBbWidth() + ATTACK_EXTRA_REACH_BLOCKS;
            if (this.eater.distanceToSqr(target) <= reach * reach) {
                this.eater.swing(InteractionHand.MAIN_HAND);
                this.eater.doHurtTarget(target);
            }
        }

        private boolean shouldRepath(LivingEntity target) {
            if (!this.eater.getNavigation().isInProgress()) {
                return true;
            }
            if (this.repathCooldown <= 0) {
                return true;
            }
            double dx = target.getX() - this.lastTargetX;
            double dy = target.getY() - this.lastTargetY;
            double dz = target.getZ() - this.lastTargetZ;
            return (dx * dx + dy * dy + dz * dz) >= TARGET_MOVE_REPATH_DISTANCE_SQR;
        }
    }
}

