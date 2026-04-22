package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CandleFiendEntity extends Monster implements GeoEntity {

    public enum BehaviorState {
        NONE, ENRAGED, SLASH, SLAM, DYING
    }

    private static final EntityDataAccessor<Boolean> MASKED =
            SynchedEntityData.defineId(CandleFiendEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE_UNMASKED = RawAnimation.begin().thenLoop("idle_mask_off");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_UNMASKED = RawAnimation.begin().thenLoop("walk_mask_off");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation RUN_UNMASKED = RawAnimation.begin().thenLoop("run_mask_off");
    private static final RawAnimation ENRAGED = RawAnimation.begin().thenPlay("enraged");
    private static final RawAnimation ENRAGED_UNMASKED = RawAnimation.begin().thenPlay("enraged_mask_off");
    private static final RawAnimation SLASH = RawAnimation.begin().thenPlay("double_slash");
    private static final RawAnimation SLASH_UNMASKED = RawAnimation.begin().thenPlay("double_slash_mask_off");
    private static final RawAnimation SLAM = RawAnimation.begin().thenPlay("slam");
    private static final RawAnimation SLAM_UNMASKED = RawAnimation.begin().thenPlay("slam_mask_off");


    private static final int ENRAGE_DURATION_TICKS = 70;  // Match with animation

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private BehaviorState behaviorState = BehaviorState.NONE;
    private LivingEntity lastTarget = null;
    private int enragedTicks = 0;

    public CandleFiendEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (this.enragedTicks > 0) {
            this.enragedTicks--;
            this.getNavigation().stop();
            this.setBehaviorState(BehaviorState.ENRAGED);
        } else if (this.getBehaviourState() == BehaviorState.ENRAGED) {
            this.setBehaviorState(BehaviorState.NONE);
        }

        if (this.getTarget() != null) {
            if (this.lastTarget == null) {
                this.enragedTicks = ENRAGE_DURATION_TICKS;
            }
            this.setSprinting(true);
        } else {
            this.setSprinting(false);
        }
        this.lastTarget = this.getTarget();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CandleFiendAttackGoal(this, 20, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.MAX_HEALTH, 700.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 3, state -> {
            if (state.getAnimatable().isMasked()) {
                if (state.isMoving()) {
                    return state.setAndContinue(state.getAnimatable().isSprinting() ? RUN : WALK);
                }
                else {
                    return state.setAndContinue(IDLE);
                }
            } else {
                if (state.isMoving()) {
                    return state.setAndContinue(state.getAnimatable().isSprinting() ? RUN_UNMASKED : WALK_UNMASKED);
                }
                else {
                    return state.setAndContinue(IDLE_UNMASKED);
                }
            }
        }));

        controllers.add(new ExpandedAnimationController<>(this, "behaviour", 2, state -> PlayState.STOP)
                .triggerableAnim("enraged", (ctr) -> this.isMasked() ? ENRAGED : ENRAGED_UNMASKED)
                .triggerableAnim("slash", (ctr) -> this.isMasked() ? SLASH : SLASH_UNMASKED)
                .triggerableAnim("slam", (ctr) -> this.isMasked() ? SLAM : SLAM_UNMASKED)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MASKED, true);
    }

    public boolean isMasked() {
        return this.entityData.get(MASKED);
    }

    private void setMasked(boolean masked) {
        this.entityData.set(MASKED, masked);
    }

    public CandleFiendEntity.BehaviorState getBehaviourState() {
        return this.behaviorState;
    }

    public void setBehaviorState(CandleFiendEntity.BehaviorState behaviorState) {
        if (this.behaviorState != BehaviorState.ENRAGED && behaviorState == BehaviorState.ENRAGED) {
            this.triggerAnim("behaviour", "enraged");
        } else if (this.behaviorState != BehaviorState.SLASH && behaviorState == BehaviorState.SLASH) {
            this.triggerAnim("behaviour", "slash");
        } else if (this.behaviorState != BehaviorState.SLAM && behaviorState == BehaviorState.SLAM) {
            this.triggerAnim("behaviour", "slam");
        }

        this.behaviorState = behaviorState;
    }
}
