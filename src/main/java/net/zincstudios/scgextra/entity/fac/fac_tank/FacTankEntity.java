package net.zincstudios.scgextra.entity.fac.fac_tank;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;
import net.zincstudios.scgextra.entity.fac.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FacTankEntity extends Monster implements GeoEntity, Stunnable, HeadShotHandler {
    private static final int DEATH_ANIMATION_TICKS = 40;
    private static final int SIDE_GUN_FIRE_WINDOW_TICKS = 8 * 20;
    private static final int SIDE_GUN_RELOAD_TICKS = 3 * 20;
    private static final int SIDE_GUN_CYCLE_TICKS = SIDE_GUN_FIRE_WINDOW_TICKS + SIDE_GUN_RELOAD_TICKS;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation STUN = RawAnimation.begin().thenPlay("stun");
    private static final RawAnimation STOMP = RawAnimation.begin().thenPlayXTimes("stomp", 1);
    private static final RawAnimation SIDE_GUN_FIRE = RawAnimation.begin().thenLoop("side gun fire");
    private static final RawAnimation CANNON_FIRE = RawAnimation.begin().thenPlayXTimes("cannon fire", 1);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
    private static final EntityDataAccessor<Integer> SIDE_GUN_ANIM_TICKS =
            SynchedEntityData.defineId(FacTankEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CANNON_ANIM_TICKS =
            SynchedEntityData.defineId(FacTankEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CANNON_WARNING_ACTIVE =
            SynchedEntityData.defineId(FacTankEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CANNON_WARNING_TICKS =
            SynchedEntityData.defineId(FacTankEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CANNON_WARNING_MAX_TICKS =
            SynchedEntityData.defineId(FacTankEntity.class, EntityDataSerializers.INT);

    // Calibrated from fac_tank.geo muzzle regions:
    // left_turret3/group2, right_turret2/group10 and cannon front barrel.
    public static final Vec3 LEFT_GUN_OFFSET = new Vec3(-1.53125D, 2.3125D, 1.0D);
    public static final Vec3 RIGHT_GUN_OFFSET = new Vec3(1.53125D, 2.3125D, 1.0D);
    public static final Vec3 CANNON_OFFSET = new Vec3(-0.71875D, 2.34375D, 1.875D);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int headshotCounter = 0;
    private boolean stunned = false;
    private boolean stunCooldown = false;
    private int stompLockTicks = 0;
    private int summonLockTicks = 0;
    private int sideGunCycleTick = 0;

    public FacTankEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIDE_GUN_ANIM_TICKS, 0);
        this.entityData.define(CANNON_ANIM_TICKS, 0);
        this.entityData.define(CANNON_WARNING_ACTIVE, false);
        this.entityData.define(CANNON_WARNING_TICKS, 0);
        this.entityData.define(CANNON_WARNING_MAX_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (this.stompLockTicks > 0) {
                this.stompLockTicks--;
            }
            if (this.summonLockTicks > 0) {
                this.summonLockTicks--;
            }
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive() && !this.isStunned() && !this.isActionLocked()) {
                this.sideGunCycleTick = (this.sideGunCycleTick + 1) % SIDE_GUN_CYCLE_TICKS;
            } else {
                this.sideGunCycleTick = 0;
            }
            if (this.isActionLocked()) {
                this.getNavigation().stop();
            }
            int sideGunTicks = this.entityData.get(SIDE_GUN_ANIM_TICKS);
            if (sideGunTicks > 0) {
                this.entityData.set(SIDE_GUN_ANIM_TICKS, sideGunTicks - 1);
            }
            int ticks = this.entityData.get(CANNON_ANIM_TICKS);
            if (ticks > 0) {
                this.entityData.set(CANNON_ANIM_TICKS, ticks - 1);
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this).smoking(true));
        this.goalSelector.addGoal(2, new FacTankStompGoal(this));
        this.goalSelector.addGoal(3, new FacTankCannonGoal(this, 200, 20, 16.0F, 20.0F, 3.0F));
        this.goalSelector.addGoal(4, new FacTankMountedGunGoal(this, 2, 16.0F, true));
        this.goalSelector.addGoal(4, new FacTankMountedGunGoal(this, 2, 16.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Bosses prioritize players and keep pressure by maintaining target lock.
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D);
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
        this.stunned = stunned;
        if (stunned) {
            this.triggerAnim("behaviour", "stun");
            this.playSound(FACSounds.FAC_TANK_STUN.get(), 1.0F, 1.0F);
        } else {
            this.headshotCounter = 0;
        }
    }

    @Override
    public boolean tickStunned(int ticksLeft) {
        return false;
    }

    @Override
    public boolean isStunned() {
        return this.stunned;
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
        return LEFT_GUN_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
    }

    public Vec3 getRightGunPos() {
        return RIGHT_GUN_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
    }

    public Vec3 getCannonPos() {
        return CANNON_OFFSET.yRot(-this.getYRot() * Mth.DEG_TO_RAD).add(this.position());
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

    public void markSideGunFiring() {
        if (this.entityData.get(SIDE_GUN_ANIM_TICKS) < 4) {
            this.entityData.set(SIDE_GUN_ANIM_TICKS, 4);
        }
    }

    public void triggerSideGunAnimation() {
        this.markSideGunFiring();
    }

    public boolean canUseMountedGuns() {
        return this.sideGunCycleTick < SIDE_GUN_FIRE_WINDOW_TICKS;
    }

    public boolean isMountedGunReloading() {
        return this.sideGunCycleTick >= SIDE_GUN_FIRE_WINDOW_TICKS
                && this.sideGunCycleTick < SIDE_GUN_CYCLE_TICKS;
    }

    public void startCannonFireAnimation() {
        this.entityData.set(CANNON_ANIM_TICKS, 15);
    }

    public void setCannonWarningActive(boolean active) {
        this.entityData.set(CANNON_WARNING_ACTIVE, active);
    }

    public boolean isCannonWarningActive() {
        return this.entityData.get(CANNON_WARNING_ACTIVE);
    }

    public void startCannonWarning(int ticks) {
        int clamped = Math.max(ticks, 0);
        this.entityData.set(CANNON_WARNING_ACTIVE, clamped > 0);
        this.entityData.set(CANNON_WARNING_TICKS, clamped);
        this.entityData.set(CANNON_WARNING_MAX_TICKS, clamped);
    }

    public void updateCannonWarning(int ticks) {
        int clamped = Math.max(ticks, 0);
        this.entityData.set(CANNON_WARNING_ACTIVE, clamped > 0);
        this.entityData.set(CANNON_WARNING_TICKS, clamped);
    }

    public void clearCannonWarning() {
        this.entityData.set(CANNON_WARNING_ACTIVE, false);
        this.entityData.set(CANNON_WARNING_TICKS, 0);
        this.entityData.set(CANNON_WARNING_MAX_TICKS, 0);
    }

    public int getCannonWarningTicks() {
        return this.entityData.get(CANNON_WARNING_TICKS);
    }

    public int getCannonWarningMaxTicks() {
        return this.entityData.get(CANNON_WARNING_MAX_TICKS);
    }

    public void startStompLock(int durationTicks) {
        this.stompLockTicks = Math.max(this.stompLockTicks, durationTicks);
    }

    public void clearStompLock() {
        this.stompLockTicks = 0;
    }

    public boolean isStompLocked() {
        return this.stompLockTicks > 0;
    }

    public void startSummonLock(int durationTicks) {
        this.summonLockTicks = Math.max(this.summonLockTicks, durationTicks);
    }

    public void clearSummonLock() {
        this.summonLockTicks = 0;
    }

    public boolean isSummonLocked() {
        return this.summonLockTicks > 0;
    }

    public boolean isActionLocked() {
        return this.stompLockTicks > 0 || this.summonLockTicks > 0;
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TANK_HURT_1.get(),
                FACSounds.FAC_TANK_HURT_2.get()
        );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TANK_IDLE_1.get(),
                FACSounds.FAC_TANK_IDLE_2.get()
        );
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(FACSounds.FAC_TANK_WALK.get(), 0.95F, 0.95F + this.random.nextFloat() * 0.1F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (state.getAnimatable().isStunned()) {
                return state.setAndContinue(IDLE);
            }
            if (state.isMoving() || this.isActuallyMoving() || this.getNavigation().isInProgress()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(0.95));

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("stun", STUN)
        );

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("stomp", STOMP)
        );

        controllers.add(new AnimationController<>(this, "side_gun", 0, state -> {
            if (state.getAnimatable().entityData.get(SIDE_GUN_ANIM_TICKS) > 0) {
                return state.setAndContinue(SIDE_GUN_FIRE);
            }
            return PlayState.STOP;
        }));

        controllers.add(new AnimationController<>(this, "cannon", 0, state -> {
            if (state.getAnimatable().entityData.get(CANNON_ANIM_TICKS) > 0) {
                return state.setAndContinue(CANNON_FIRE);
            }
            return PlayState.STOP;
        }));

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
