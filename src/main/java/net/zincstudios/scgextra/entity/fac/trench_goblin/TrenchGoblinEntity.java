package net.zincstudios.scgextra.entity.fac.trench_goblin;

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
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TrenchGoblinEntity extends GunnerEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity delayedHitTarget;
    private int delayedHitTicks = -1;

    public TrenchGoblinEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new TrenchGoblinMeleeGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
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
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32F)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle", 2, state -> {
            boolean moving = state.isMoving() || this.getNavigation().isInProgress();
            if (state.getAnimatable().hasLiveTarget()) {
                state.getController().setAnimationSpeed(1.2);
                return state.setAndContinue(moving ? WALK_ATTACK : IDLE);
            }
            if (moving) {
                state.getController().setAnimationSpeed(1.5);
                return state.setAndContinue(WALK);
            }
            state.getController().setAnimationSpeed(1.0);
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK)
                .setAnimationSpeed(1.8)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            this.playSound(MobUtil.getSound(
                    this.random,
                    FACSounds.TRENCH_GOBLIN_ATTACK_1.get(),
                    FACSounds.TRENCH_GOBLIN_ATTACK_2.get(),
                    FACSounds.TRENCH_GOBLIN_ATTACK_3.get()
            ), 1.0F, 1.0F);
        }
        return hit;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_HURT_1.get(),
                FACSounds.TRENCH_GOBLIN_HURT_2.get(),
                FACSounds.TRENCH_GOBLIN_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_IDLE_1.get(),
                FACSounds.TRENCH_GOBLIN_IDLE_2.get(),
                FACSounds.TRENCH_GOBLIN_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_DEATH_1.get(),
                FACSounds.TRENCH_GOBLIN_DEATH_2.get()
        );
    }
}

