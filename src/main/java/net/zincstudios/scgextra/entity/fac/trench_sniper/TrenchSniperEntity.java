package net.zincstudios.scgextra.entity.fac.trench_sniper;

import net.minecraft.sounds.SoundEvent;
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
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.ai.AlertFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.fac.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TrenchSniperEntity extends GunnerEntity implements GeoEntity {

    private static final int ATTACK_POSE_TRANSITION_TICKS = 7;
    private static final int AIMING_GRACE_TICKS = 16;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ATTACK = RawAnimation.begin().thenPlayAndHold("idle_attack");
    private static final RawAnimation ATTACK_IDLE = RawAnimation.begin().thenPlayAndHold("attack_idle");
    private static final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenLoop("hold_attack");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean attackPoseActive = false;
    private boolean enteringAttackPose = false;
    private int attackPoseTransitionTicks = 0;
    private int aimingGraceTicks = 0;

    public TrenchSniperEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AlertFactionGoal(this, 200, true));
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
                .add(Attributes.MOVEMENT_SPEED, 0.18F)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MAX_HEALTH, 40.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle/aim", 2, state -> {
            boolean moving = state.isMoving() || this.isActuallyMoving() || this.getNavigation().isInProgress();
            boolean aimingNow = state.getAnimatable().isAiming();
            if (aimingNow) {
                this.aimingGraceTicks = AIMING_GRACE_TICKS;
            } else if (this.aimingGraceTicks > 0) {
                this.aimingGraceTicks--;
            }

            boolean wantsAttackPose = aimingNow || this.aimingGraceTicks > 0;

            if (wantsAttackPose != this.attackPoseActive) {
                this.attackPoseActive = wantsAttackPose;
                this.enteringAttackPose = wantsAttackPose;
                this.attackPoseTransitionTicks = ATTACK_POSE_TRANSITION_TICKS;
            }

            if (this.attackPoseTransitionTicks > 0) {
                this.attackPoseTransitionTicks--;
                return state.setAndContinue(this.enteringAttackPose ? IDLE_ATTACK : ATTACK_IDLE);
            }

            if (this.attackPoseActive) {
                if (moving) {
                    return state.setAndContinue(WALK_ATTACK);
                }
                return state.setAndContinue(HOLD_ATTACK);
            }
            if (moving) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(1.0));
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_SNIPER_ALERT_1.get(),
                FACSounds.TRENCH_SNIPER_ALERT_2.get(),
                FACSounds.TRENCH_SNIPER_ALERT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_SNIPER_IDLE_1.get(),
                FACSounds.TRENCH_SNIPER_IDLE_2.get(),
                FACSounds.TRENCH_SNIPER_IDLE_3.get(),
                FACSounds.TRENCH_SNIPER_IDLE_4.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_SNIPER_DEATH_1.get(),
                FACSounds.TRENCH_SNIPER_DEATH_2.get()
        );
    }
}
