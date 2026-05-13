package net.zincstudios.scgextra.entity.neutral.inflicted_wolf;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class InflictedWolfEntity extends Monster implements GeoEntity {
    private static final float ATTACK_REACH_SCALE = 2.6F;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity pendingAttackTarget;
    private int pendingAttackWindupTicks = -1;
    private int pendingHitDelayTicks = 0;
    private LivingEntity delayedHitTarget;
    private int delayedHitTicks = -1;

    public InflictedWolfEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new InflictedWolfMeleeGoal(this, 1.05D, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Skeleton.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof net.minecraft.world.entity.LivingEntity living) {
            NeutralCombatUtil.applyLacerate(living, 60);
        }
        return hit;
    }

    public void scheduleAttack(LivingEntity target, int windupTicks, int hitDelayTicks) {
        if (target == null || !target.isAlive()) {
            return;
        }
        this.pendingAttackTarget = target;
        this.pendingAttackWindupTicks = Math.max(0, windupTicks);
        this.pendingHitDelayTicks = Math.max(0, hitDelayTicks);
    }

    private void processQueuedAttack() {
        if (this.level().isClientSide() || this.pendingAttackWindupTicks < 0) {
            return;
        }
        if (this.pendingAttackWindupTicks > 0) {
            this.pendingAttackWindupTicks--;
            return;
        }

        LivingEntity target = this.pendingAttackTarget;
        this.pendingAttackTarget = null;
        this.pendingAttackWindupTicks = -1;
        if (target == null || !target.isAlive()) {
            return;
        }

        this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        this.triggerAnim("attack", "attack");
        this.scheduleDelayedHit(target, this.pendingHitDelayTicks);
        this.pendingHitDelayTicks = 0;
    }

    private void scheduleDelayedHit(LivingEntity target, int delayTicks) {
        if (target == null || !target.isAlive()) {
            this.clearDelayedHit();
            return;
        }
        this.delayedHitTarget = target;
        this.delayedHitTicks = Math.max(0, delayTicks);
    }

    private void clearDelayedHit() {
        this.delayedHitTarget = null;
        this.delayedHitTicks = -1;
    }

    private void processDelayedHit() {
        if (this.level().isClientSide() || this.delayedHitTicks < 0) {
            return;
        }
        if (this.delayedHitTicks > 0) {
            this.delayedHitTicks--;
            return;
        }

        LivingEntity target = this.delayedHitTarget;
        this.clearDelayedHit();
        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.distanceToSqr(target) <= this.getAttackReachSqr(target) && this.hasLineOfSight(target)) {
            this.doHurtTarget(target);
        }
    }

    public double getAttackReachSqr(LivingEntity target) {
        return this.getBbWidth() * ATTACK_REACH_SCALE * this.getBbWidth() * ATTACK_REACH_SCALE + target.getBbWidth();
    }

    @Override
    public void tick() {
        super.tick();
        this.processQueuedAttack();
        this.processDelayedHit();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.isWaterAtOrBelow(level, this.blockPosition())) {
            return false;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceInflictedWolf) {
            return false;
        }
        net.minecraft.core.BlockPos pos = this.blockPosition();
        if (level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos.below()).is(FluidTags.WATER)) {
            return false;
        }
        net.minecraft.world.level.block.state.BlockState below = level.getBlockState(pos.below());
        if (below.isAir()) {
            return false;
        }
        if (below.is(BlockTags.LEAVES) || below.is(BlockTags.LOGS)) {
            return false;
        }
        if (!below.isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
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
            boolean moving = state.isMoving()
                    || this.getNavigation().isInProgress()
                    || this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5D;
            if (moving) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }
}

