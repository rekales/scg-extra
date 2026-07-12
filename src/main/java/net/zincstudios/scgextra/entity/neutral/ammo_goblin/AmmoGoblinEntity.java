package net.zincstudios.scgextra.entity.neutral.ammo_goblin;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AmmoGoblinEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_RUNNING =
            SynchedEntityData.defineId(AmmoGoblinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DISAPPEAR =
            SynchedEntityData.defineId(AmmoGoblinEntity.class, EntityDataSerializers.INT);

    public AmmoGoblinEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 50.0)
        .add(Attributes.MOVEMENT_SPEED, 0.5)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.1)
        .add(Attributes.ARMOR, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(
            this, 
            Player.class, 
            5,
            0.5, 
            0.7
        ){
            @Override
            public void start() {
                super.start();
                if(mob instanceof AmmoGoblinEntity age){
                    age.setRunning(true);
                }
            }
            @Override
            public void tick() {
                if (this.mob.distanceToSqr(this.toAvoid) < (double)100.0F) {
                    this.mob.getNavigation().setSpeedModifier(0.7);
                } else {
                    this.mob.getNavigation().setSpeedModifier(0.5);
                }
            }
            @Override
            public void stop() {
                super.stop();
                if(this.mob instanceof AmmoGoblinEntity age){
                    age.triggerAnim("controller", "smoke_bomb");
                    age.setDisappearingTick(25);
                    age.setRunning(false);
                }
            }
        });
        this.goalSelector.addGoal(2, new PanicGoal(this, (double)0.7F){
            @Override
            public void start() {
                super.start();
                if(mob instanceof AmmoGoblinEntity age)age.setRunning(true);
            }
            @Override
            public void stop() {
                super.stop();
                if(mob instanceof AmmoGoblinEntity age)age.setRunning(false);
            }
        });
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 5));
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
        }).triggerableAnim("smoke_bomb", RawAnimation.begin().thenPlay("smoke_bomb")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_RUNNING, false);
        this.entityData.define(DISAPPEAR, -1);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IS_RUNNING", this.isRunning());
        compound.putBoolean("DISAPPEAR", this.isRunning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IS_RUNNING")) {
            this.setRunning(compound.getBoolean("IS_RUNNING"));
        }
        if (compound.contains("DISAPPEAR")) {
            this.setRunning(compound.getBoolean("DISAPPEAR"));
        }
    }
    
    public boolean isRunning(){
        return this.entityData.get(IS_RUNNING);
    }

    public void setRunning(boolean running){
        this.entityData.set(IS_RUNNING, running);
    }

    public int getDisappearingTick(){
        return this.entityData.get(DISAPPEAR);
    }

    public void setDisappearingTick(int dis){
        this.entityData.set(DISAPPEAR, dis);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getDisappearingTick() == 0){
            this.discard();
        }else if(this.getDisappearingTick() == 15){
            if(!this.level().isClientSide()){
                ServerLevel l = (ServerLevel)this.level();
                l.sendParticles(
                    ParticleTypes.LARGE_SMOKE, 
                    this.getX(), 
                    this.getY(), 
                    this.getZ(),
                    50, 
                    0.5, 
                    0.5, 
                    0.5,
                    0.1
                );
                l.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER, 
                    this.getX(), 
                    this.getY(), 
                    this.getZ(),
                    1,
                    0.5, 
                    0.5, 
                    0.5,
                    0.1
                );
            }
            this.setDisappearingTick(this.getDisappearingTick()-1);
        }
        else{
            this.setDisappearingTick(this.getDisappearingTick()-1);
        }
    }
    @Override
    protected float getSoundVolume() {
        return 2;
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return NeutralSounds.AMMO_GOBLIN_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return NeutralSounds.AMMO_GOBLIN_HURT.get();
    }
    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.AMMO_GOBLIN_DEAD.get();
    }
}