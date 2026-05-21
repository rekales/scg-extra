package net.zincstudios.scgextra.entity.neutral.end_scorpion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

import java.util.EnumSet;

public class EndScorpionEntity extends Monster implements GeoEntity {
    private static final int ATTACK_NONE = 0;
    private static final int ATTACK_CLAW = 1;
    private static final int ATTACK_STING = 2;
    private static final int CLAW_ANIM_TICKS = 60;
    private static final int CLAW_HIT_DELAY_TICKS = 12;
    private static final int STING_ANIM_TICKS = 60;
    private static final int STING_HIT_DELAY_TICKS = 30;
    private static final int STING_COOLDOWN_TICKS = 120;
    private static final int ATTACK_COOLDOWN_MIN_TICKS = 24;
    private static final int ATTACK_COOLDOWN_MAX_TICKS = 34;
    private static final int DEATH_ANIM_TICKS = 30;
    private static final int DIG_COOLDOWN_TICKS = 20;
    private static final double BASE_ATTACK_REACH = 3.8D;
    private static final double HOVER_HEIGHT = 1.5D;
    private static final double BASE_FLYING_SPEED = 1.2D;
    private static final double BASE_CHASE_SPEED = 1.45D;
    private static final double AGGRO_SPEED_MULTIPLIER = 2.5D;

    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(EndScorpionEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int attackAnimTicks;
    private int attackCooldownTicks;
    private int stingCooldownTicks;
    private int digCooldown;
    private LivingEntity pendingAttackTarget;
    private int pendingAttackKind = ATTACK_NONE;
    private int pendingAttackTicks = -1;
    private boolean aggroSpeedActive;

    public EndScorpionEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 120;
        this.setNoGravity(true);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.FLYING_SPEED, BASE_FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, ATTACK_NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new EndScorpionMeleeGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
        this.tickHoverHeight();

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.isAttackAnimationActive()) {
                this.faceTarget(target, 10.0F);
            }
        }
        this.setAggroSpeedState(target != null && target.isAlive());

        if (this.level().isClientSide()) {
            return;
        }

        this.tickAttackAnimation();
        this.tickAttackCooldowns();
        this.tickPendingAttack();
        this.tickDigging();

        if (this.isAttackAnimationActive()) {
            this.getNavigation().stop();
        }
    }

    private void tickHoverHeight() {
        if (this.isDeadOrDying()) {
            return;
        }

        double targetY = this.findHoverTargetY();
        double deltaY = targetY - this.getY();
        double verticalSpeed = Mth.clamp(deltaY * 0.25D, -0.25D, 0.25D);
        this.setDeltaMovement(this.getDeltaMovement().x, verticalSpeed, this.getDeltaMovement().z);
    }

    private double findHoverTargetY() {
        BlockPos base = this.blockPosition();
        for (int offset = 0; offset <= 16; offset++) {
            BlockPos check = base.below(offset);
            BlockState state = this.level().getBlockState(check);
            if (!state.isAir() && state.blocksMotion()) {
                return check.getY() + 1.0D + HOVER_HEIGHT;
            }
        }
        return this.getY();
    }

    private void tickAttackAnimation() {
        if (this.attackAnimTicks <= 0) {
            return;
        }
        this.attackAnimTicks--;
        if (this.attackAnimTicks == 0) {
            this.entityData.set(ATTACK_STATE, ATTACK_NONE);
        }
    }

    private void tickAttackCooldowns() {
        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }
        if (this.stingCooldownTicks > 0) {
            this.stingCooldownTicks--;
        }
    }

    private void tickPendingAttack() {
        if (this.pendingAttackTicks < 0) {
            return;
        }
        if (this.pendingAttackTicks > 0) {
            this.pendingAttackTicks--;
            return;
        }

        LivingEntity target = this.pendingAttackTarget;
        int attackKind = this.pendingAttackKind;
        this.pendingAttackTarget = null;
        this.pendingAttackKind = ATTACK_NONE;
        this.pendingAttackTicks = -1;

        if (attackKind == ATTACK_STING) {
            this.performStingHit(target);
            return;
        }
        this.performClawHit(target);
    }

    private void tickDigging() {
        if (this.digCooldown > 0) {
            this.digCooldown--;
            return;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || this.distanceTo(target) > 5.0F) {
            return;
        }

        this.digCooldown = DIG_COOLDOWN_TICKS;
        Vec3 toTarget = target.position().subtract(this.position());
        double horizontalLen = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        if (horizontalLen < 0.001D) {
            return;
        }

        double nx = toTarget.x / horizontalLen;
        double nz = toTarget.z / horizontalLen;
        double px = -nz;
        double pz = nx;
        int baseY = this.blockPosition().getY();
        double forwardX = this.getX() + nx * 1.5D;
        double forwardZ = this.getZ() + nz * 1.5D;

        for (int side = -1; side <= 1; side++) {
            double sx = forwardX + px * side * 0.9D;
            double sz = forwardZ + pz * side * 0.9D;
            BlockPos front = BlockPos.containing(sx, baseY, sz);
            this.tryBreakSoftBlock(front);
            this.tryBreakSoftBlock(front.above());
        }
    }

    private void startAttackAnimation(int attackKind) {
        this.getNavigation().stop();
        if (attackKind == ATTACK_STING) {
            this.attackAnimTicks = STING_ANIM_TICKS;
        } else {
            this.attackAnimTicks = CLAW_ANIM_TICKS;
        }
        this.entityData.set(ATTACK_STATE, attackKind);
    }

    private void scheduleAttackHit(LivingEntity target, int attackKind) {
        this.pendingAttackTarget = target;
        this.pendingAttackKind = attackKind;
        this.pendingAttackTicks = attackKind == ATTACK_STING ? STING_HIT_DELAY_TICKS : CLAW_HIT_DELAY_TICKS;

        RandomSource random = this.getRandom();
        this.attackCooldownTicks = random.nextInt(ATTACK_COOLDOWN_MAX_TICKS - ATTACK_COOLDOWN_MIN_TICKS + 1)
                + ATTACK_COOLDOWN_MIN_TICKS;
        if (attackKind == ATTACK_STING) {
            this.stingCooldownTicks = STING_COOLDOWN_TICKS;
        }
    }

    private void performClawHit(LivingEntity target) {
        if (!this.canHitTarget(target)) {
            return;
        }
        super.doHurtTarget(target);
    }

    private void performStingHit(LivingEntity target) {
        if (!this.canHitTarget(target)) {
            return;
        }
        this.playSound(NeutralSounds.NEUTRAL_END_SCORPION_STING.get(), 1.2F, this.getVoicePitch());
        target.hurt(this.damageSources().mobAttack(this), 10.0F);
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
        NeutralCombatUtil.applyLacerate(target, 100);
    }

    private void setAggroSpeedState(boolean aggro) {
        if (this.aggroSpeedActive == aggro) {
            return;
        }
        this.aggroSpeedActive = aggro;
        this.setSprinting(aggro);

        double multiplier = aggro ? AGGRO_SPEED_MULTIPLIER : 1.0D;
        if (this.getAttribute(Attributes.FLYING_SPEED) != null) {
            this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(BASE_FLYING_SPEED * multiplier);
        }
    }

    private void applyFlightChaseBoost(LivingEntity target, double speedMultiplier) {
        if (target == null || !target.isAlive()) {
            return;
        }

        double dx = target.getX() - this.getX();
        double dy = target.getEyeY() - this.getY();
        double dz = target.getZ() - this.getZ();
        double lenXZ = Math.sqrt(dx * dx + dz * dz);
        if (lenXZ < 0.0001D) {
            return;
        }

        double basePush = 0.06D;
        double horizontalPush = basePush * speedMultiplier;
        double verticalPush = Mth.clamp(dy * 0.02D, -0.08D, 0.08D);
        Vec3 cur = this.getDeltaMovement();

        this.setDeltaMovement(
                Mth.clamp(cur.x * 0.82D + (dx / lenXZ) * horizontalPush, -1.8D, 1.8D),
                Mth.clamp(cur.y * 0.88D + verticalPush, -0.8D, 0.8D),
                Mth.clamp(cur.z * 0.82D + (dz / lenXZ) * horizontalPush, -1.8D, 1.8D)
        );
    }

    private boolean canHitTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (!this.hasLineOfSight(target)) {
            return false;
        }
        double reach = BASE_ATTACK_REACH + this.getBbWidth() * 0.5D + target.getBbWidth() * 0.5D;
        return this.distanceToSqr(target) <= reach * reach;
    }

    private boolean isAttackAnimationActive() {
        return this.entityData.get(ATTACK_STATE) != ATTACK_NONE;
    }

    private void faceTarget(LivingEntity target, float turnStep) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float desiredYaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
        float nextYaw = Mth.approachDegrees(this.getYRot(), desiredYaw, turnStep);
        this.setYRot(nextYaw);
        this.setYBodyRot(nextYaw);
        this.setYHeadRot(nextYaw);
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= DEATH_ANIM_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            if (this.isAttackAnimationActive()) {
                return PlayState.STOP;
            }
            if (state.isMoving() || this.getNavigation().isInProgress()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("flying"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> {
            int attackState = this.entityData.get(ATTACK_STATE);
            if (attackState == ATTACK_STING) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("attack_sting"));
            }
            if (attackState == ATTACK_CLAW) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("attack_claw"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private void tryBreakSoftBlock(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir()) return;
        float hardness = state.getDestroySpeed(this.level(), pos);
        if (hardness < 0.0F || hardness > 12.0F) return;
        if (state.is(Blocks.BEDROCK)
                || state.is(Blocks.END_PORTAL_FRAME)
                || state.is(Blocks.OBSIDIAN)
                || state.is(Blocks.CRYING_OBSIDIAN)) return;
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return;
        this.level().destroyBlock(pos, true, this);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        int roll = this.random.nextInt(3);
        if (roll == 0) return NeutralSounds.NEUTRAL_END_SCORPION_IDLE_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_END_SCORPION_IDLE_02.get();
        return NeutralSounds.NEUTRAL_END_SCORPION_IDLE_03.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_END_SCORPION_HURT_01.get()
                : NeutralSounds.NEUTRAL_END_SCORPION_HURT_02.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_END_SCORPION_DEATH.get();
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
        return NeutralCombatUtil.passesSpawnChance(this.random, CommonConfig.spawnChanceEndScorpion)
                && NeutralCombatUtil.canSpawnEndSurface(level, pos);
    }

    private static final class EndScorpionMeleeGoal extends Goal {
        private static final int REPATH_INTERVAL_TICKS = 4;
        private static final double TARGET_MOVE_REPATH_DISTANCE_SQR = 1.0D;

        private final EndScorpionEntity scorpion;
        private int repathCooldown;
        private double lastTargetX;
        private double lastTargetY;
        private double lastTargetZ;

        private EndScorpionMeleeGoal(EndScorpionEntity scorpion) {
            this.scorpion = scorpion;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.scorpion.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.scorpion.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.scorpion.getTarget();
            if (target == null || !target.isAlive()) {
                return;
            }

            this.scorpion.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.scorpion.isAttackAnimationActive()) {
                this.scorpion.getNavigation().stop();
                this.scorpion.faceTarget(target, 10.0F);
            } else {
                double chaseMultiplier = this.scorpion.aggroSpeedActive ? AGGRO_SPEED_MULTIPLIER : 1.0D;
                boolean canDirectChase = this.scorpion.hasLineOfSight(target)
                        && Math.abs(target.getY() - this.scorpion.getY()) < 2.0D;
                if (canDirectChase) {
                    this.scorpion.getNavigation().stop();
                    this.scorpion.getMoveControl().setWantedPosition(
                            target.getX(),
                            target.getY() + HOVER_HEIGHT,
                            target.getZ(),
                            BASE_CHASE_SPEED * chaseMultiplier
                    );
                    this.repathCooldown = 0;
                } else {
                    if (this.repathCooldown > 0) {
                        this.repathCooldown--;
                    }
                    if (shouldRepath(target)) {
                        this.scorpion.getNavigation().moveTo(target, BASE_CHASE_SPEED * chaseMultiplier);
                        this.repathCooldown = REPATH_INTERVAL_TICKS;
                        this.lastTargetX = target.getX();
                        this.lastTargetY = target.getY();
                        this.lastTargetZ = target.getZ();
                    }
                }
                this.scorpion.applyFlightChaseBoost(target, chaseMultiplier);
            }

            if (this.scorpion.attackCooldownTicks > 0
                    || this.scorpion.pendingAttackTicks >= 0
                    || this.scorpion.isAttackAnimationActive()) {
                return;
            }
            if (!this.scorpion.canHitTarget(target)) {
                return;
            }

            boolean useSting = this.scorpion.stingCooldownTicks <= 0 && this.scorpion.getRandom().nextInt(3) == 0;
            int attackKind = useSting ? ATTACK_STING : ATTACK_CLAW;
            this.scorpion.swing(InteractionHand.MAIN_HAND);
            this.scorpion.startAttackAnimation(attackKind);
            this.scorpion.scheduleAttackHit(target, attackKind);
        }

        private boolean shouldRepath(LivingEntity target) {
            if (!this.scorpion.getNavigation().isInProgress()) {
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

