package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedGoal;
import net.zincstudios.scgextra.sounds.ModSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlamingHeadEntity extends Monster implements GeoEntity, Stunnable {

    public enum BehaviorState {
        NONE, RAMMING, SPINNING, STUNNED
    }

    private static final EntityDataAccessor<Integer> BEHAVIOR_STATE =
            SynchedEntityData.defineId(FlamingHeadEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private SoundEvent[] idleSounds = {
        ModSounds.RRC_FLAMING_HEAD_IDLE_1.get(),
        ModSounds.RRC_FLAMING_HEAD_IDLE_2.get(),
        ModSounds.RRC_FLAMING_HEAD_IDLE_3.get(),
        ModSounds.RRC_FLAMING_HEAD_IDLE_4.get(),
        ModSounds.RRC_FLAMING_HEAD_IDLE_5.get()
    };

    public FlamingHeadEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable StunnedGoal<?> getStunnedGoal() {
        for(WrappedGoal goal : this.goalSelector.getAvailableGoals()){
            if(goal.getGoal() instanceof StunnedGoal<?> stunnedGoal){
                return stunnedGoal;
            }
        }
        return null;
    }

    public BehaviorState getBehaviorState() {
        return BehaviorState.values()[this.entityData.get(BEHAVIOR_STATE)];
    }

    public void setBehaviorState(BehaviorState state) {
        this.entityData.set(BEHAVIOR_STATE, state.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEHAVIOR_STATE, BehaviorState.NONE.ordinal());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600D)
                .add(Attributes.ARMOR, 12D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0,
                state -> {
                    if (state.getAnimatable().isDeadOrDying()) {
                        return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
                    }

                    if (state.getAnimatable().getBehaviorState() == BehaviorState.STUNNED) {
                        state.setAnimation(RawAnimation.begin().thenLoop("stun"));
                    } else if (state.getAnimatable().getBehaviorState() == BehaviorState.RAMMING) {
                        state.setAnimation(RawAnimation.begin().thenPlay("ramming_attack"));
                    } else if (state.getAnimatable().getBehaviorState() == BehaviorState.SPINNING) {
                        state.setAnimation(RawAnimation.begin().thenPlay("fire_attack"));
                    } else if (state.isMoving()) {
                        state.setAnimation(RawAnimation.begin().thenLoop("move"));
                    } else {
                        state.setAnimation(RawAnimation.begin().thenLoop("idle"));
                    }

                    return PlayState.CONTINUE;
                }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new RammingAttackGoal(this, 600, 30, 3));
        this.goalSelector.addGoal(3, new FireSpinAttackGoal(this, 200, 30, 8F, 10));
//        this.goalSelector.addGoal(4, new ThrowFlamesGoal(this));  //TODO: convert to aiStep

        this.goalSelector.addGoal(5, new MoveTowardsTargetGoal(this, 1, 40));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity) || entity.getMobType().equals(MobType.UNDEAD)));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.is(DamageTypes.IN_FIRE) || pSource.is(DamageTypes.ON_FIRE)){
            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.RRC_FLAMING_HEAD_DEAD_1.get();
    };
    protected SoundEvent getAmbientSound() {
        return idleSounds[this.random.nextInt(idleSounds.length)];
    };
    protected SoundEvent getStepSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    };
    protected SoundEvent getDeathSound() {
        return this.random.nextBoolean() ? ModSounds.RRC_FLAMING_HEAD_DEAD_1.get() : ModSounds.RRC_FLAMING_HEAD_DEAD_2.get();
    };
    protected float getSoundVolume() {
        return 2F;
    };
    protected void playStepSound(net.minecraft.core.BlockPos pPos, net.minecraft.world.level.block.state.BlockState pState) {
      this.playSound(this.getStepSound(), this.getSoundVolume() * 0.15F, 1F);
    };
}
