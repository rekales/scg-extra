package net.zincstudios.scgextra.entity.fac.fac_walker;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FacWalkerEntity extends GunnerEntity implements GeoEntity, Stunnable, HeadShotHandler {

    private static final int DEATH_ANIMATION_TICKS = 40;
    private static final int MOUNTED_GUN_FIRE_WINDOW_TICKS = 5 * 20;
    private static final int MOUNTED_GUN_RELOAD_TICKS = 3 * 20;
    private static final int MOUNTED_GUN_CYCLE_TICKS = MOUNTED_GUN_FIRE_WINDOW_TICKS + MOUNTED_GUN_RELOAD_TICKS;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation IDLE_2 = RawAnimation.begin().thenLoop("scan");
    private static final RawAnimation STUN = RawAnimation.begin().thenLoop("stun");
    private static final RawAnimation ATACC = RawAnimation.begin().thenLoop("atacc");
    private static final RawAnimation STOMP = RawAnimation.begin().thenPlayXTimes("stomp", 1);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
    private static final EntityDataAccessor<Boolean> STUNNED =
            SynchedEntityData.defineId(FacWalkerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RANGED_ANIM_TICKS =
            SynchedEntityData.defineId(FacWalkerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STOMP_ANIM_TICKS =
            SynchedEntityData.defineId(FacWalkerEntity.class, EntityDataSerializers.INT);

    // Calibrated from the fac_walker.geo left_turret/right_turret pivots.
    private static final Vec3 LEFT_TURRET_OFFSET = new Vec3(0.96875D, 3.28125D, 0.0625D);
    private static final Vec3 RIGHT_TURRET_OFFSET = new Vec3(-0.96875D, 3.28125D, 0.0625D);
    private static final double TURRET_MUZZLE_FORWARD_OFFSET = 0.75D;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int headshotCounter = 0;
    private boolean stunCooldown = false;
    private int stompLockTicks = 0;
    private int mountedGunCycleTick = 0;

    public FacWalkerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STUNNED, false);
        this.entityData.define(RANGED_ANIM_TICKS, 0);
        this.entityData.define(STOMP_ANIM_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            if (target != null && !target.isAlive()) {
                this.setTarget(null);
            }

            if (this.stompLockTicks > 0) {
                this.stompLockTicks--;
            }
            if (target != null && target.isAlive() && !this.isActionLocked()) {
                this.mountedGunCycleTick = (this.mountedGunCycleTick + 1) % MOUNTED_GUN_CYCLE_TICKS;
            } else {
                this.mountedGunCycleTick = 0;
            }
            if (this.isActionLocked()) {
                this.getNavigation().stop();
            }

            int rangedTicks = this.entityData.get(RANGED_ANIM_TICKS);
            if (rangedTicks > 0) {
                this.entityData.set(RANGED_ANIM_TICKS, rangedTicks - 1);
            }

            int stompAnimTicks = this.entityData.get(STOMP_ANIM_TICKS);
            if (stompAnimTicks > 0) {
                this.entityData.set(STOMP_ANIM_TICKS, stompAnimTicks - 1);
            }

        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this).smoking(true));
        this.goalSelector.addGoal(2, new FacWalkerStompGoal(this));
        this.goalSelector.addGoal(3, new FacWalkerMountedGunGoal(this, 2, 16.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
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
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26F)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, 250.0D);
    }

    @Override
    public int shouldStun() {
        if (!CommonConfig.enableAbilityWeakness) {
            return 0;
        }
        if (this.headshotCounter >= CommonConfig.abilityWeaknessHeadshots) {
            return CommonConfig.abilityWeaknessDuration;
        }
        return 0;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.entityData.set(STUNNED, stunned);
        if (!stunned) {
            this.triggerAnim("behaviour", "end_stun");
            this.headshotCounter = 0;
            this.stopRangedAnimation();
        }
    }

    @Override
    public boolean isStunned() {
        return this.entityData.get(STUNNED);
    }

    @Override
    public void setStunCooldown(boolean cooldown) {
        this.stunCooldown = cooldown;
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        if (this.headshotCounter < CommonConfig.abilityWeaknessHeadshots - 1 || !this.stunCooldown) {
            this.headshotCounter++;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isStunned()) {
            amount *= (float) CommonConfig.abilityWeaknessDamageMult;
        }
        return super.hurt(source, amount);
    }

    public Vec3 getLeftGunPos() {
        Vec3 turretPos = LEFT_TURRET_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).scale(TURRET_MUZZLE_FORWARD_OFFSET);
        return turretPos.add(forward);
    }

    public Vec3 getRightGunPos() {
        Vec3 turretPos = RIGHT_TURRET_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
        Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).scale(TURRET_MUZZLE_FORWARD_OFFSET);
        return turretPos.add(forward);
    }

    public boolean hasClearShot(Vec3 from, LivingEntity target) {
        Vec3 to = target.getEyePosition();
        HitResult hit = this.level().clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));
        return hit.getType() == HitResult.Type.MISS;
    }

    public void startRangedAnimation(int durationTicks) {
        if (durationTicks <= 0) {
            return;
        }
        int current = this.entityData.get(RANGED_ANIM_TICKS);
        if (current < durationTicks) {
            this.entityData.set(RANGED_ANIM_TICKS, durationTicks);
        }
    }

    public void stopRangedAnimation() {
        this.entityData.set(RANGED_ANIM_TICKS, 0);
    }

    public boolean isRangedPoseActive() {
        return this.entityData.get(RANGED_ANIM_TICKS) > 0;
    }

    public boolean canUseMountedGun() {
        return this.mountedGunCycleTick < MOUNTED_GUN_FIRE_WINDOW_TICKS;
    }

    public void startStompLock(int durationTicks) {
        this.stompLockTicks = Math.max(this.stompLockTicks, durationTicks);
        int current = this.entityData.get(STOMP_ANIM_TICKS);
        if (current < durationTicks) {
            this.entityData.set(STOMP_ANIM_TICKS, durationTicks);
        }
    }

    public void clearStompLock() {
        this.stompLockTicks = 0;
    }

    public boolean isStompLocked() {
        return this.stompLockTicks > 0;
    }

    public boolean isStompAnimationActive() {
        return this.entityData.get(STOMP_ANIM_TICKS) > 0;
    }

    public boolean isActionLocked() {
        return this.isStompLocked() || this.isStunned();
    }

    private boolean hasLiveTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    private boolean shouldPlayRunAnimation() {
        return this.hasLiveTarget() && !this.isStunned() && !this.isRangedPoseActive() && !this.isStompAnimationActive();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 1, state -> {
            if (state.getAnimatable().isStunned()) {
                return state.setAndContinue(STUN);
            }
            if (state.getAnimatable().isStompAnimationActive()) {
                return state.setAndContinue(IDLE);
            }
            if (state.getAnimatable().isRangedPoseActive()) {
                return state.setAndContinue(ATACC);
            }
            boolean moving = state.isMoving() || this.isActuallyMoving() || this.getNavigation().isInProgress();
            if (state.getAnimatable().shouldPlayRunAnimation() && (moving || state.getAnimatable().hasLiveTarget())) {
                return state.setAndContinue(RUN);
            }
            if (moving) {
                return state.setAndContinue(WALK);
            }
            int idle2Phase = (state.getAnimatable().tickCount + state.getAnimatable().getId() * 23) % 320;
            if (!state.getAnimatable().hasLiveTarget() && idle2Phase >= 180 && idle2Phase < 260) {
                return state.setAndContinue(IDLE_2);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("end_stun", IDLE)
        );

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("stomp", STOMP)
        );

        controllers.add(new AnimationController<>(this, "death", 0, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= DEATH_ANIMATION_TICKS && !this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
