package net.zincstudios.scgextra.entity.neutral.nether.nitro_beetle;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.zincstudios.scgextra.entity.common.MobUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NitroBeetleEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_FLYING =
            SynchedEntityData.defineId(NitroBeetleEntity.class, EntityDataSerializers.BOOLEAN);
    public NitroBeetleEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 20.0)
        .add(Attributes.MOVEMENT_SPEED, 0.4)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
        .add(Attributes.ARMOR, 12)
        .add(Attributes.ATTACK_DAMAGE, 3)
        .add(Attributes.ATTACK_KNOCKBACK, 0.1)
        ;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.4, true));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 5));
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if(state.getAnimatable().isFlying()){
                state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
            }else if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < (double)0.0F) {
            this.setFlying(true);
            this.setDeltaMovement(vec3.multiply((double)1.0F, 0.6, (double)1.0F));
        }else{
            this.setFlying(false);
        }
        if(!this.onGround()){
            this.setFlying(true);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if(this.getTarget()!=null){
            return SoundEvents.BEE_LOOP_AGGRESSIVE;
        }
        return SoundEvents.BEE_LOOP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BEE_HURT;
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }
    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 15);
    }
    @Override
    public boolean doHurtTarget(Entity entity) {
        this.playSound(SoundEvents.BEE_DEATH, this.getSoundVolume(), this.getVoicePitch());
        entity.setSecondsOnFire(this.random.nextInt(5));
        return super.doHurtTarget(entity);
    }
    @Override
    public boolean fireImmune() {
        return true;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_FLYING, false);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IS_FLYING", this.isFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("IS_FLYING")) {
            this.setFlying(compound.getBoolean("IS_FLYING"));
        }
    }
    
    public boolean isFlying(){
        return this.entityData.get(IS_FLYING);
    }

    public void setFlying(boolean flying){
        this.entityData.set(IS_FLYING, flying);
    }
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }
    @Override
    public void die(DamageSource damageSource) {
        if(!this.level().isClientSide()){
            ServerLevel sl = (ServerLevel) this.level();
            sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 1);
            this.playSound(SoundEvents.GENERIC_EXPLODE, this.getSoundVolume(), this.getVoicePitch());
            AABB aabb = this.getBoundingBox().inflate(5);
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
            for(LivingEntity entity : entities){
                if(this.distanceToSqr(entity)<=25){
                    this.doHurtTarget(entity);
                }
            }
        }
        super.die(damageSource);
    }
    @Override
    public boolean canSwimInFluidType(FluidType type) {
        if(type == ForgeMod.LAVA_TYPE.get()){
            return true;
        }
        return super.canSwimInFluidType(type);
    }
}