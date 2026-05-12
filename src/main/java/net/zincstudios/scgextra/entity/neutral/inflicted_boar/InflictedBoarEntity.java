package net.zincstudios.scgextra.entity.neutral.inflicted_boar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class InflictedBoarEntity extends Hoglin implements GeoEntity {
    private static final int HIT_DELAY_TICKS = 5;
    private static final double ATTACK_FRONT_DOT = 0.2D;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity delayedHitTarget;
    private int delayedHitTicks = -1;

    public InflictedBoarEntity(EntityType<? extends Hoglin> type, Level level) {
        super(type, level);
        this.setImmuneToZombification(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Hoglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
        this.setBaby(false);
        this.setImmuneToZombification(true);
        this.refreshDimensions();
        return data;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (!(target instanceof LivingEntity living) || !living.isAlive()) {
            return false;
        }
        if (!this.isTargetInFront(living, ATTACK_FRONT_DOT) || !this.hasLineOfSight(living)) {
            return false;
        }
        this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        this.triggerAnim("attack", "attack");
        this.scheduleDelayedHit(living, HIT_DELAY_TICKS);
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.processDelayedHit();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (this.random.nextFloat() * 100.0F >= CommonConfig.spawnChanceInflictedBoar) {
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
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
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

    public void scheduleDelayedHit(LivingEntity target, int delayTicks) {
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

        double reachSqr = this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + target.getBbWidth();
        if (this.distanceToSqr(target) <= reachSqr
                && this.hasLineOfSight(target)
                && this.isTargetInFront(target, ATTACK_FRONT_DOT)) {
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            boolean hit = target.hurt(this.damageSources().mobAttack(this), damage);
            if (hit) {
                net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.applyLacerate(target, 40);
                target.knockback(0.6D, this.getX() - target.getX(), this.getZ() - target.getZ());
            }
        }
    }

    private boolean isTargetInFront(LivingEntity target, double minDot) {
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.yBodyRot).normalize();
        Vec3 toTarget = target.position().subtract(this.position());
        Vec3 toTargetFlat = new Vec3(toTarget.x, 0.0D, toTarget.z);
        if (toTargetFlat.lengthSqr() < 1.0E-6D) {
            return true;
        }
        return forward.dot(toTargetFlat.normalize()) >= minDot;
    }
}

