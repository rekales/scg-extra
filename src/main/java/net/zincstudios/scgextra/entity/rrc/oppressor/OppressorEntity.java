package net.zincstudios.scgextra.entity.rrc.oppressor;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.AlertFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.FlareSummonGoal;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.ModSounds;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.config.EntityEquipmentConfig;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OppressorEntity extends GunnerEntity implements GeoEntity {

    private static final RawAnimation AIMING = RawAnimation.begin().thenPlayAndHold("idle_aim");
    private static final RawAnimation HOLD = RawAnimation.begin().thenPlay("aim_idle");
    private static final RawAnimation FLARE = RawAnimation.begin().thenPlay("flare");
    private static final RawAnimation ALERT = RawAnimation.begin().thenPlay("alert");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private SoundEvent[] idleSounds = {
        ModSounds.RRC_OPPRESSOR_IDLE_1.get(),
        ModSounds.RRC_OPPRESSOR_IDLE_2.get(),
        ModSounds.RRC_OPPRESSOR_IDLE_3.get()
    };
    
    public OppressorEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, "scgextra:oppressor");  // NOTE: using raw string
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected void registerGoals() {
        // TODO: custom gun attack goal
        this.goalSelector.addGoal(3, new AlertFactionGoal(this, 200));
        this.goalSelector.addGoal(4, new FlareSummonGoal(this, 100, 30,
                ModEntities.SCOUT.get(), ModEntities.TALLMAN.get()));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120D)
                .add(Attributes.ARMOR, 6D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle", 2, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
        }));

        controllers.add(new AnimationController<>(this, "idle_head", 2, state -> {
            return state.setAndContinue(RawAnimation.begin()
                    .thenWait(50)  // TODO: random time
                    .thenPlay("idle_head")
            );
        }).setAnimationSpeed(1.3));

        controllers.add(new ExpandedAnimationController<>(this, "behaviour", 0,
                state -> {

                    if (!state.getController().isPlayingTriggeredAnimation()) {
                        if (state.isCurrentAnimation(AIMING) && !state.getAnimatable().isAiming()) {
                            state.setAnimation(HOLD);
                        }
                        if (state.getAnimatable().isAiming()) {
                            state.setAnimation(AIMING);
//                            return state.setAndContinue(AIMING);
                        }
                    } else {
//                        SCGExtra.LOGGER.debug("trig");
//                        if (state.getAnimatable().isAiming() && state.getController() instanceof ExpandedAnimationController<?> controller) {
//                            if (state.isCurrentAnimation(FLARE)) {
//                                SCGExtra.LOGGER.debug("altered");
//                                controller.setTriggeredAnimation(RawAnimation.begin().thenPlay("aim_idle").thenPlay("flare"));
//                            }
//                            if (state.isCurrentAnimation(ALERT)) {
//                                controller.setTriggeredAnimation(RawAnimation.begin().thenPlay("aim_idle").thenPlay("alert"));
//                            }
//                        }

//                        if (state.isCurrentAnimation(AIMING)) {
//                            if (state.getController() instanceof ExpandedAnimationController<?> controller) {
//                                if (state.isCurrentAnimation(FLARE)) {
//                                    controller.setTriggeredAnimation(RawAnimation.begin().thenPlay("aim_idle").thenPlay("flare"));
//                                }
//                                if (state.isCurrentAnimation(ALERT)) {
//                                    controller.setTriggeredAnimation(RawAnimation.begin().thenPlay("aim_idle").thenPlay("alert"));
//                                }
//                            }
//
////                            state.getController().tri
//
////                            state.setAnimation(RawAnimation.begin().thenPlayAndHold("aim_idle"));
//
////                            SCGExtra.LOGGER.debug("stop");
////                            return PlayState.STOP;
////
////                            state.resetCurrentAnimation();
////                            state.setAnimation(AIMING);
////                            return state.setAndContinue(AIMING);
//                        }
                    }

                    return PlayState.CONTINUE;

//                    SCGExtra.LOGGER.debug(state.getController().isPlayingTriggeredAnimation() +"");

//                    if (!state.getController().isPlayingTriggeredAnimation()) {
//                        if (state.getAnimatable().isAiming()) {
//                            return state.setAndContinue(AIMING);
//                        } else {
//                            return state.setAndContinue(HOLD);
//                        }
//                    } else {
//                        if (state.getAnimatable().isAiming()) {
//                            state.resetCurrentAnimation();
//                            return state.setAndContinue(AIMING);
//                        }
//                    }
//
//
//                    return PlayState.CONTINUE;

                })
//                .receiveTriggeredAnimations()
                .triggerableAnim("flare", FLARE)
                .triggerableAnim("alert", ALERT)
        );

//        controllers.add(new AnimationController<>(this, "walk/idle/aim", 2,
//                state -> {
//                    AnimationController<?> behaviorController = this.getAnimatableInstanceCache()
//                            .getManagerForId(this.getId())
//                            .getAnimationControllers()
//                            .get("behavior");
//
//
//                    if (state.getAnimatable().isAiming()) {
//                        if (behaviorController != null && behaviorController.isPlayingTriggeredAnimation()) {
//                            return state.setAndContinue(RawAnimation.begin().thenPlay("aim_idle"));
//                        } else {
//                            return state.setAndContinue(AIMING);
//                        }
//                    } else {
//                        RawAnimation anim = RawAnimation.begin();
//                        if (state.isCurrentAnimation(AIMING)) {
//                            anim = anim.thenPlay("aim_idle");
//                        }
//                        if (state.isMoving()) {
//                            return state.setAndContinue(anim.thenLoop("walk"));
//                        } else {
//                            return state.setAndContinue(anim.thenLoop("idle_2"));
//                            // TODO: idle variation switching
//                        }
//                    }
//                }
//        ).setAnimationSpeed(1.4));
//
//        // NOTE: maybe add triggerable animations on the main controller instead.
//        controllers.add(new AnimationController<>(this, "behaviour", 0,
//                state -> {
//                    AnimationController<?> aimController = this.getAnimatableInstanceCache()
//                            .getManagerForId(this.getId())
//                            .getAnimationControllers()
//                            .get("walk/idle/aim");
//
//
//                    if (state.getController().isPlayingTriggeredAnimation()) {
//                        SCGExtra.LOGGER.debug("triggered");
//                        if (Objects.equals(aimController.getCurrentRawAnimation(), AIMING)) {
//                            return PlayState.STOP;
//
////                            state.resetCurrentAnimation();
////                            return state.setAndContinue(RawAnimation.begin()
////                                    .thenPlay("aim_idle")
////                                    .thenPlay("flare")
////                                    .thenPlayAndHold("idle_aim"));
//                        }
//
////                        state.getController().getCurrentRawAnimation()
//                    }
//
//                    return PlayState.STOP;
//                })
//                .receiveTriggeredAnimations()
//                .triggerableAnim("flare", RawAnimation.begin().thenPlay("flare"))
//                .triggerableAnim("alert", RawAnimation.begin().thenPlay("alert"))
//        );


        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    protected void tickDeath() {
        // Override to only extend death time
        ++this.deathTime;
        if (this.deathTime >= 30 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return this.random.nextBoolean() ? ModSounds.RRC_OPPRESSOR_HURT_1.get() : ModSounds.RRC_OPPRESSOR_HURT_2.get();
    };
    protected SoundEvent getAmbientSound() {
        return idleSounds[this.random.nextInt(idleSounds.length)];
    };
    protected SoundEvent getStepSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    };
    protected SoundEvent getDeathSound() {
        return this.random.nextBoolean() ?
            ModSounds.RRC_OPPRESSOR_DEATH_1.get() :
            ModSounds.RRC_OPPRESSOR_DEATH_2.get();
    };
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), 1.0F);
    }
    protected float getSoundVolume() {
        return 2F;
    };
}