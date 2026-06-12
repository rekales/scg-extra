package net.zincstudios.scgextra.entity.fac.shovel_knight;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
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
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShovelKnightEntity extends GunnerEntity implements GeoEntity {

    private static final int SWING_ANIM_DURATION_TICKS = 12;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation AGRO_RUN = RawAnimation.begin().thenLoop("agro_run");
    private static final RawAnimation SWING = RawAnimation.begin().thenLoop("swing4");
    private static final EntityDataAccessor<Integer> SWING_ANIM_TICKS =
            SynchedEntityData.defineId(ShovelKnightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> COMBAT_ANIM =
            SynchedEntityData.defineId(ShovelKnightEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public ShovelKnightEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWING_ANIM_TICKS, 0);
        this.entityData.define(COMBAT_ANIM, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            if (target != null && !target.isAlive()) {
                this.setTarget(null);
                target = null;
            }

            int swingTicks = this.entityData.get(SWING_ANIM_TICKS);
            if (swingTicks > 0) {
                this.entityData.set(SWING_ANIM_TICKS, swingTicks - 1);
            }

            boolean hasTarget = target != null && target.isAlive();
            this.entityData.set(COMBAT_ANIM, hasTarget);
        }
    }

    @Override
    public void swing(InteractionHand hand) {
        super.swing(hand);
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            this.entityData.set(SWING_ANIM_TICKS, SWING_ANIM_DURATION_TICKS);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new ShovelKnightDigGoal(this));
        this.goalSelector.addGoal(3, new ShovelKnightMeleeAttackGoal(this, 1.45D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.19F)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MAX_HEALTH, 50.0D);
    }

    private boolean hasLiveTarget() {
        LivingEntity target = this.getTarget();
        return target != null && target.isAlive();
    }

    private boolean isSwingAnimating() {
        return this.entityData.get(SWING_ANIM_TICKS) > 0;
    }

    private boolean isCombatAnimating() {
        return this.entityData.get(COMBAT_ANIM);
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (state.getAnimatable().isSwingAnimating()) {
                return state.setAndContinue(SWING);
            }

            boolean moving = state.isMoving() || this.isActuallyMoving() || this.getNavigation().isInProgress();
            if (state.getAnimatable().isCombatAnimating() && moving) {
                return state.setAndContinue(AGRO_RUN);
            }
            if (moving) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(1.0));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
