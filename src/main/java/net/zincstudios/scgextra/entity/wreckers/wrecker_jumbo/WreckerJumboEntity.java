package net.zincstudios.scgextra.entity.wreckers.wrecker_jumbo;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.BulletProofParts;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.WreckersSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WreckerJumboEntity extends GunnerEntity implements GeoEntity, BulletProofParts {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");

    private static final double BELLY_HALF_WIDTH = 0.95D;
    private static final double BELLY_MIN_Y = 1.2D;
    private static final double BELLY_MAX_Y = 2.2D;
    private static final double BELLY_MIN_FORWARD = 0.5D;
    private static final double BELLY_MAX_FORWARD = 1.6D;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity delayedHitTarget;
    private int delayedHitTicks = -1;

    public WreckerJumboEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18F)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MAX_HEALTH, 300.0D);
    }

    @Override
    protected void registerGoals() {
        if (CommonConfig.enableAbilityDig) {
            this.goalSelector.addGoal(2, new WreckerJumboDigGoal(this));
        }
        this.goalSelector.addGoal(3, new WreckerJumboMeleeGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
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
        double reachSqr = this.getBbWidth() * 2.4F * this.getBbWidth() * 2.4F + target.getBbWidth();
        if (this.distanceToSqr(target) <= reachSqr && this.hasLineOfSight(target)) {
            this.doHurtTarget(target);
        }
    }

    private boolean hasLiveTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        super.tick();
        this.processDelayedHit();
    }

    @Override
    public boolean isBulletProofHit(Vec3 hitVec) {
        Vec3 local = hitVec.subtract(this.position());
        if (local.y < BELLY_MIN_Y || local.y > BELLY_MAX_Y) {
            return false;
        }
        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Mth.sin(yaw), 0.0D, Mth.cos(yaw));
        double forwardDist = local.x * forward.x + local.z * forward.z;
        double lateralDist = Math.abs(local.x * forward.z - local.z * forward.x);
        return forwardDist >= BELLY_MIN_FORWARD && forwardDist <= BELLY_MAX_FORWARD
                && lateralDist <= BELLY_HALF_WIDTH;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            boolean moving = state.isMoving() || this.getNavigation().isInProgress();
            return state.setAndContinue(moving ? WALK : IDLE);
        }).setAnimationSpeed(0.9));

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK));

        controllers.add(new AnimationController<>(this, "death", 0, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(WreckersSounds.JUMBO_ATTACK.get(), 1.0F, 1.0F);
        }
        return hit;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected float getSoundVolume() {
        return 0.95F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(4) == 0) {
            return WreckersSounds.JUMBO_LINE.get();
        }
        return WreckersSounds.JUMBO_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return WreckersSounds.JUMBO_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return WreckersSounds.JUMBO_DEATH.get();
    }
}
