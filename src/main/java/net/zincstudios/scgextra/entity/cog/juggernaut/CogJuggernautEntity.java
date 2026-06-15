package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.entity.common.EquippedEntity;
import net.zincstudios.scgextra.entity.common.brain.BrainUtils;
import net.zincstudios.scgextra.sounds.CogSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogJuggernautEntity extends EquippedEntity implements GeoEntity {

    private static final EntityDataAccessor<Boolean> JET_ACTIVE =
            SynchedEntityData.defineId(CogJuggernautEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public CogJuggernautEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20F)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 1200.0D);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CogJuggernautAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<CogJuggernautEntity> getBrain() {
        return (Brain<CogJuggernautEntity>) super.getBrain();
    }

    protected Brain.Provider<CogJuggernautEntity> brainProvider() {
        return Brain.provider(CogJuggernautAi.MEMORY_TYPES, CogJuggernautAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cogJuggernautBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().popPush("cogJuggernautActivityUpdate");
        BrainUtils.Standard.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

//    @Override
//    public boolean hurt(DamageSource source, float amount) {
//        if (source.is(DamageTypes.FALL) && this.isJetActive()) return false;
//        return super.hurt(source, amount);
//    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (this.isJetActive()) return;
        super.checkFallDamage(y, onGround, state, pos);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
                if (state.getAnimatable().isAggressive()) {
                    if (state.isMoving()) {
                        state.setAnimation(RawAnimation.begin().thenLoop("walk_hold"));
                    } else {
                        state.setAnimation(RawAnimation.begin().thenLoop("idle_hold"));
                    }
                } else {
                    if (state.isMoving()) {
                        state.setAnimation(RawAnimation.begin().thenLoop("walk"));
                    } else {
                        state.setAnimation(RawAnimation.begin().thenLoop("idle"));
                    }
                }
                return PlayState.CONTINUE;
                }).setAnimationSpeed(1.2f)
        );

        controllers.add(new AnimationController<>(this, "jet", 4, state -> {
                    if (state.getAnimatable().isJetActive()) {
                        return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("fly"));
                    }
                    return PlayState.STOP;
                })
                .triggerableAnim("land", RawAnimation.begin().thenPlay("land"))
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(JET_ACTIVE, false);
    }

    public void setJetActive(boolean jetActive) {
        if (this.entityData.get(JET_ACTIVE) && !jetActive) {
            this.triggerAnim("jet", "land");
        }
        this.entityData.set(JET_ACTIVE, jetActive);
    }

    public boolean isJetActive() {
        return this.entityData.get(JET_ACTIVE);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return CogSounds.GENERAL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CogSounds.COG_JUGGERNAUT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CogSounds.COG_JUGGERNAUT_DEAD.get();
    }

//    @Override
//    public Fallsounds getFallSounds() {
//        return super.getFallSounds();
//    }
}
