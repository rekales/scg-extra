package net.zincstudios.scgextra.entity.wreckers.wrecker_jumbo;

import net.minecraft.sounds.SoundEvent;
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
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.fac.shovel_knight.ShovelKnightDigGoal;
import net.zincstudios.scgextra.sounds.WreckersSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

public class WreckerJumboEntity extends GunnerEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");

    private static final double BELLY_FRONT_DOT = 0.45D;
    private static final float BELLY_BULLET_DAMAGE_MULT = 0.1F;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity delayedHitTarget;
    private int delayedHitTicks = -1;

    public WreckerJumboEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18F)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MAX_HEALTH, 300.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WreckerJumboMeleeGoal(this, 1.0D, true));
        if (CommonConfig.enableAbilityDig) {
            this.goalSelector.addGoal(3, new ShovelKnightDigGoal(this));
        }
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
    public boolean hurt(DamageSource source, float amount) {
        if (CommonConfig.enableAbilityBulletproof && this.isBellyBulletHit(source)) {
            amount *= BELLY_BULLET_DAMAGE_MULT;
        }
        return super.hurt(source, amount);
    }

    private boolean isBellyBulletHit(DamageSource source) {
        if (!(source.getDirectEntity() instanceof ProjectileEntity projectile)) {
            return false;
        }
        Vec3 toProjectile = projectile.position().subtract(this.position());
        toProjectile = new Vec3(toProjectile.x, 0.0D, toProjectile.z);
        if (toProjectile.lengthSqr() < 1.0E-4D) {
            return false;
        }
        Vec3 facing = this.getForward();
        facing = new Vec3(facing.x, 0.0D, facing.z).normalize();
        return facing.dot(toProjectile.normalize()) >= BELLY_FRONT_DOT;
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
    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(this.random, WreckersSounds.JUMBO_IDLE.get(), WreckersSounds.JUMBO_LINE.get());
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
