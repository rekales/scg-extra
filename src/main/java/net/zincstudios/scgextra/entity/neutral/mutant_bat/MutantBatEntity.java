package net.zincstudios.scgextra.entity.neutral.mutant_bat;

import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.neutral.NeutralEntities;

import javax.annotation.Nullable;

import net.zincstudios.scgextra.sounds.NeutralSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MutantBatEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> IS_SCREAMING = SynchedEntityData.defineId(MutantBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(MutantBatEntity.class, EntityDataSerializers.BOOLEAN);

    public MutantBatEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new MutantBatMeleeAttackGoal(this, 0.5, true));
        this.goalSelector.addGoal(2, new MutantBatScreamAttackGoal(this));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.5, 10));
        this.goalSelector.addGoal(3, new MutantBatRunGoal(this, 0.7f));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 100.0)
        .add(Attributes.MOVEMENT_SPEED, 0.5)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
        .add(Attributes.ATTACK_KNOCKBACK, 0.5)
        .add(Attributes.ATTACK_DAMAGE, 8)
        .add(Attributes.ARMOR, 2)
        .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if(state.getAnimatable().isRunning()){
                state.setAndContinue(RawAnimation.begin().thenLoop("run"));
            }else if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        })
        .triggerableAnim("melee_attack", RawAnimation.begin().thenPlay("melee_attack"))
        .triggerableAnim("scream_attack", RawAnimation.begin().thenPlay("scream_attack")));
        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SCREAMING, false);
        this.entityData.define(IS_RUNNING, false);
    }

    public boolean isScreaming(){
        return this.entityData.get(IS_SCREAMING);
    }

    public void setScreaming(boolean bool){
        this.entityData.set(IS_SCREAMING, bool);
    }

    public boolean isRunning(){
        return this.entityData.get(IS_RUNNING);
    }

    public void setRunning(boolean bool){
        this.entityData.set(IS_RUNNING, bool);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 30);
    }
    @Override
    public void tick() {
        super.tick();
    }
    @Override
    protected float getSoundVolume() {
        return 2;
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return NeutralSounds.MUTANT_BAT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return NeutralSounds.MUTANT_BAT_HURT.get();
    }
    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.MUTANT_BAT_DEAD.get();
    }
    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (this.blockPosition().getY() >= 63) {
            return false;
        } else {
            if(level.isClientSide()){
                return false;
            }
            return checkMonsterSpawnRules(NeutralEntities.MUTANT_BAT.get(), (ServerLevelAccessor)level, spawnReason, this.blockPosition(), random);
        }
    }
}