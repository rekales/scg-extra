package net.zincstudios.scgextra.entity.fac.lion;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.mixin.GunAttackGoalAccessor;
import net.zincstudios.scgextra.sounds.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.entity.ai.GunAttackGoal;

public class FacLionEntity extends GunnerEntity implements GeoEntity {

    private static final int DEATH_ANIMATION_TICKS = 40;
    private static final EntityDataAccessor<Boolean> DEFENDING =
            SynchedEntityData.defineId(FacLionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COMBAT_POSE =
            SynchedEntityData.defineId(FacLionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHIELD_BASHING =
            SynchedEntityData.defineId(FacLionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final double SHIELD_BLOCK_DOT_THRESHOLD = 0.22D;
    private static final double SHIELD_BLOCK_RIGHT_OFFSET = 0.35D;
    private static final double SHIELD_BLOCK_HEIGHT_OFFSET = 1.45D;
    private static final double SHIELD_BLOCK_FORWARD_OFFSET = 0.45D;
    private static final int SHIELD_START_BLOCK_TICKS = 30;
    private static final boolean SHIELD_DEBUG_VISUAL = false;
    private static final int SHIELD_DEBUG_PARTICLE_INTERVAL_TICKS = 4;
    private static final double HITBOX_BACK_OFFSET = 0.0D;
    private static final double HITBOX_SIDE_OFFSET = 0.0D;

    private static final RawAnimation SHIELD_UP = RawAnimation.begin().thenPlayAndHold("shield_up");
    private static final RawAnimation SHIELD_WALK = RawAnimation.begin().thenLoop("shield_walk");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SHIELD_BASH = RawAnimation.begin().thenPlay("shield_bash");
    private static final RawAnimation SHIELD_START = RawAnimation.begin().thenPlay("shield_start");
    private static final RawAnimation SHIELD_END = RawAnimation.begin().thenPlay("shield_end");
    private static final RawAnimation ATACC_HOLD = RawAnimation.begin().thenPlayAndHold("atacc");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean wasDefendingLastTick = false;
    private int combatPoseGraceTicks = 0;
    private int shieldStartBlockTicks = 0;

    public FacLionEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new FacLionShieldBashGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.14F)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.MAX_HEALTH, 200.0D);
    }

    @Override
    protected AABB makeBoundingBox() {
        var dimensions = this.getDimensions(this.getPose());
        float halfWidth = dimensions.width / 2.0F;

        Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        Vec3 right = new Vec3(forward.z, 0.0D, -forward.x);
        double centerX = this.getX() - forward.x * HITBOX_BACK_OFFSET + right.x * HITBOX_SIDE_OFFSET;
        double centerZ = this.getZ() - forward.z * HITBOX_BACK_OFFSET + right.z * HITBOX_SIDE_OFFSET;

        return new AABB(
                centerX - halfWidth, this.getY(), centerZ - halfWidth,
                centerX + halfWidth, this.getY() + dimensions.height, centerZ + halfWidth
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEFENDING, false);
        this.entityData.define(COMBAT_POSE, false);
        this.entityData.define(SHIELD_BASHING, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean shieldWalkFromCombatPose = this.isCombatPose() && this.isCombatStrideMoving();
        boolean shieldActive = this.isDefending() || this.shieldStartBlockTicks > 0 || shieldWalkFromCombatPose;
        if (CommonConfig.enableAbilityBulletproof && shieldActive) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity target = this.getTarget();
        if (target != null && !target.isAlive()) {
            this.setTarget(null);
        }

        boolean isReloadingNow = false;
        boolean inGunCombatWindow = false;
        GunAttackGoal<?> gunAttackGoal = this.getGunAttackGoal();
        if (gunAttackGoal instanceof GunAttackGoalAccessor accessor) {
            isReloadingNow = accessor.isReloading();
            inGunCombatWindow = accessor.getAttackTime() > 0 || accessor.getRemainingBursts() > 0;
        }
        if (this.shieldStartBlockTicks > 0) {
            this.shieldStartBlockTicks--;
        }

        boolean hasTargetNow = this.hasLiveTarget();
        boolean defendingNow = hasTargetNow && isReloadingNow;
        boolean combatPoseNow;
        if (hasTargetNow && !isReloadingNow) {
            if (this.isAiming() || inGunCombatWindow) {
                this.combatPoseGraceTicks = 16;
                combatPoseNow = true;
            } else if (this.combatPoseGraceTicks > 0) {
                this.combatPoseGraceTicks--;
                combatPoseNow = true;
            } else {
                combatPoseNow = false;
            }
        } else {
            this.combatPoseGraceTicks = 0;
            combatPoseNow = false;
        }

        this.entityData.set(DEFENDING, defendingNow);
        this.entityData.set(COMBAT_POSE, combatPoseNow);

        if (SHIELD_DEBUG_VISUAL && (defendingNow || this.shieldStartBlockTicks > 0 || (combatPoseNow && this.isCombatStrideMoving()))
                && this.tickCount % SHIELD_DEBUG_PARTICLE_INTERVAL_TICKS == 0
                && this.level() instanceof ServerLevel serverLevel) {
            this.spawnShieldDebugParticles(serverLevel);
        }

        if (!this.isShieldBashing()) {
            if (defendingNow && !this.wasDefendingLastTick) {
                this.shieldStartBlockTicks = SHIELD_START_BLOCK_TICKS;
                this.triggerAnim("attack", "shield_start");
            } else if (!defendingNow && this.wasDefendingLastTick) {
                this.triggerAnim("attack", "shield_end");
            }
        }

        this.wasDefendingLastTick = defendingNow;
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    private boolean isCombatStrideMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.0025D;
    }

    private boolean hasLiveTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    private void spawnShieldDebugParticles(ServerLevel level) {
        Vec3 anchor = this.getShieldBlockAnchor();
        Vec3 forward = this.getShieldFacingVector();
        double halfAngle = Math.acos(SHIELD_BLOCK_DOT_THRESHOLD);
        double radius = 0.95D;
        double[] yOffsets = {-0.25D, 0.15D, 0.55D};

        for (double yOffset : yOffsets) {
            for (int i = -4; i <= 4; i++) {
                double angle = halfAngle * i / 4.0D;
                Vec3 dir = rotateAroundY(forward, angle);
                double px = anchor.x + dir.x * radius;
                double py = anchor.y + yOffset;
                double pz = anchor.z + dir.z * radius;
                level.sendParticles(ParticleTypes.END_ROD, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private Vec3 getShieldBlockAnchor() {
        Vec3 forward = this.getShieldFacingVector();
        Vec3 right = new Vec3(forward.z, 0.0D, -forward.x);

        return this.position()
                .add(right.scale(SHIELD_BLOCK_RIGHT_OFFSET))
                .add(0.0D, SHIELD_BLOCK_HEIGHT_OFFSET, 0.0D)
                .add(forward.scale(SHIELD_BLOCK_FORWARD_OFFSET));
    }

    private Vec3 getShieldFacingVector() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            Vec3 toTarget = target.position().subtract(this.position());
            Vec3 toTargetXZ = new Vec3(toTarget.x, 0.0D, toTarget.z);
            if (toTargetXZ.lengthSqr() > 0.000001D) {
                return toTargetXZ.normalize();
            }
        }
        return this.getBodyForwardVector();
    }

    private Vec3 getBodyForwardVector() {
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.yBodyRot);
        Vec3 forwardXZ = new Vec3(forward.x, 0.0D, forward.z);
        if (forwardXZ.lengthSqr() < 0.000001D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }
        return forwardXZ.normalize();
    }

    private static Vec3 rotateAroundY(Vec3 vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(
                vector.x * cos - vector.z * sin,
                vector.y,
                vector.x * sin + vector.z * cos
        );
    }

    private boolean isDefending() {
        return this.entityData.get(DEFENDING);
    }

    private boolean isCombatPose() {
        return this.entityData.get(COMBAT_POSE);
    }

    public boolean isShieldBashing() {
        return this.entityData.get(SHIELD_BASHING);
    }

    public void setShieldBashing(boolean shieldBashing) {
        this.entityData.set(SHIELD_BASHING, shieldBashing);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle/aim", 4, state -> {
            boolean moving = state.isMoving() || this.isActuallyMoving();
            if (state.getAnimatable().isDefending()) {
                if (moving) {
                    return state.setAndContinue(SHIELD_WALK);
                }
                return state.setAndContinue(SHIELD_UP);
            }
            if (state.getAnimatable().isCombatPose()) {
                if (state.isMoving() && this.isCombatStrideMoving()) {
                    return state.setAndContinue(SHIELD_WALK);
                }
                return state.setAndContinue(ATACC_HOLD);
            }
            if (moving) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(1.0));

        controllers.add(new AnimationController<>(this, "shield_bash", 0, state -> PlayState.STOP)
                .triggerableAnim("shield_bash", SHIELD_BASH)
        );

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("shield_start", SHIELD_START)
                .triggerableAnim("shield_end", SHIELD_END)
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= DEATH_ANIMATION_TICKS && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_LION_HURT_1.get(),
                FACSounds.FAC_LION_HURT_2.get(),
                FACSounds.FAC_LION_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_LION_IDLE_1.get(),
                FACSounds.FAC_LION_IDLE_2.get(),
                FACSounds.FAC_LION_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_LION_DEATH_1.get(),
                FACSounds.FAC_LION_DEATH_2.get()
        );
    }
}
