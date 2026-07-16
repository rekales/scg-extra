package net.zincstudios.scgextra.entity.neutral.end.end_scorpion;

import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.goal.BreakBlocksGoal;
import net.zincstudios.scgextra.sounds.NeutralSounds;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EndScorpionEntity extends Monster implements GeoEntity, FlyingAnimal{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final int MELEE_DAMAGE_DELAY = 10;
    private int hurtDelay = -1;
    private static final EntityDataAccessor<Boolean> IS_STINGING = SynchedEntityData.defineId(EndScorpionEntity.class, EntityDataSerializers.BOOLEAN);

    public EndScorpionEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.4, true));
        this.goalSelector.addGoal(3, new EndScorpionWanderGoal());
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.4, 10));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new BreakBlocksGoal(this, 60));
    }
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("flying"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        })
        .triggerableAnim("attack_claw", RawAnimation.begin().thenPlay("attack_claw"))
        .triggerableAnim("attack_sting", RawAnimation.begin().thenPlay("attack_sting")));
        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 30);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 200.0)
        .add(Attributes.MOVEMENT_SPEED, 0.4)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
        .add(Attributes.ATTACK_KNOCKBACK, 0.5)
        .add(Attributes.ATTACK_DAMAGE, 15)
        .add(Attributes.ARMOR, 8)
        .add(Attributes.FOLLOW_RANGE, 20)
        .add(Attributes.FLYING_SPEED, 0.4);
    }
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        this.hurtDelay--;
        if (this.hurtDelay == 0) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                double distToEnemySqr = this.getPerceivedTargetDistanceSquareForMeleeAttack(target);
                double reach = this.getAttackReachSqr(target) * 1.3;
                if (distToEnemySqr <= reach) {
                    super.doHurtTarget(target);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
                }
            }
        }
    }
    private double getAttackReachSqr(LivingEntity attackTarget) {
        return 12;
    }
    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!this.level().isClientSide) {
            if (this.hurtDelay > 0) return false;

            this.triggerAnim("controller", "attack_claw");
            this.hurtDelay = MELEE_DAMAGE_DELAY;
            this.playSound(NeutralSounds.END_STONE_CRAB_ATTACK.get());
        }
        return true;
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return NeutralSounds.END_STONE_CRAB_HURT.get();
    };
    protected SoundEvent getAmbientSound() {
        return NeutralSounds.END_STONE_CRAB_IDLE.get();
    };
    protected SoundEvent getDeathSound() {
        return NeutralSounds.END_STONE_CRAB_DEATH.get();
    };
    protected float getSoundVolume() {
        return 0.8F;
    };
    protected void playStepSound(net.minecraft.core.BlockPos pPos, net.minecraft.world.level.block.state.BlockState pState) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, this.getSoundVolume(), 1);
    };
    @Override
    public boolean isFlying() {
        return !this.onGround();
    }
    protected PathNavigation createNavigation(Level p_level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, p_level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_STINGING, false);
    }
    public void setStinging(boolean stinging){
        this.entityData.set(IS_STINGING, stinging);
    }
    public boolean isStinging(){
        return this.entityData.get(IS_STINGING);
    }
    class EndScorpionWanderGoal extends Goal {
        EndScorpionWanderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            return EndScorpionEntity.this.navigation.isDone() && EndScorpionEntity.this.random.nextInt(10) == 0;
        }

        public boolean canContinueToUse() {
            return EndScorpionEntity.this.navigation.isInProgress();
        }

        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                EndScorpionEntity.this.navigation.moveTo(EndScorpionEntity.this.navigation.createPath(BlockPos.containing(vec3), 1), (double)1.0F);
            }

        }

        @Nullable
        private Vec3 findPos() {
            Vec3 vec3 = EndScorpionEntity.this.getViewVector(0.0F);
            Vec3 vec32 = HoverRandomPos.getPos(EndScorpionEntity.this, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(EndScorpionEntity.this, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
        }
    }
}
