package net.zincstudios.scgextra.entity.fac.fac_trencher;

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
import net.zincstudios.scgextra.entity.EntityTypeTags;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.fac.FACSounds;
import net.zincstudios.scgextra.entity.rrc.scout.ScoutEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class FacTrencherEntity extends ScoutEntity {
    private static final int ATTACK_POSE_TRANSITION_TICKS = 7;
    private static final int AIMING_GRACE_TICKS = 14;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ATTACK = RawAnimation.begin().thenPlayAndHold("idle_attack");
    private static final RawAnimation ATTACK_IDLE = RawAnimation.begin().thenPlayAndHold("attack_idle");
    private static final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenLoop("hold_attack");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");
    private boolean attackPoseActive = false;
    private boolean enteringAttackPose = false;
    private int attackPoseTransitionTicks = 0;
    private int aimingGraceTicks = 0;

    public FacTrencherEntity(EntityType<? extends GunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 1, true, false,
                this::isHostileFactionTarget));
    }

    private boolean isHostileFactionTarget(LivingEntity entity) {
        if (entity == this) {
            return false;
        }

        if (Faction.isEnemies(this, entity)) {
            return true;
        }

        return entity.getType().is(EntityTypeTags.RRC);
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
                return state.setAndContinue(moving ? WALK_ATTACK : HOLD_ATTACK);
            }
            return state.setAndContinue(moving ? WALK : IDLE);
        }).setAnimationSpeed(1.0));
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TRENCHER_HURT_1.get(),
                FACSounds.FAC_TRENCHER_HURT_2.get(),
                FACSounds.FAC_TRENCHER_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TRENCHER_IDLE_1.get(),
                FACSounds.FAC_TRENCHER_IDLE_2.get(),
                FACSounds.FAC_TRENCHER_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TRENCHER_DEATH_1.get(),
                FACSounds.FAC_TRENCHER_DEATH_2.get(),
                FACSounds.FAC_TRENCHER_DEATH_3.get()
        );
    }
}

