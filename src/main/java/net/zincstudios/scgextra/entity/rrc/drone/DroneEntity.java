package net.zincstudios.scgextra.entity.rrc.drone;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;
import net.zincstudios.scgextra.sounds.ModSounds;
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.zincstudios.scgextra.Faction;

//The health checks in the play sound is just to have it not play any extra sounds while it's about to die
public class DroneEntity extends Monster implements GeoEntity, Stunnable{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Float> INACCURACY = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.FLOAT);
    private final DronePart[] subEntities;
    public DroneEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        DronePart pipe = new DronePart(this, "pipe", 0.5F, 2F);
        DronePart back = new DronePart(this, "back", 2F, 2F);
        DronePart body = new DronePart(this, "body", 2.5F, 2F);
        DronePart leg1 = new DronePart(this, "leg1", 0.5F, 1.3F);
        DronePart leg2 = new DronePart(this, "leg2", 0.5F, 1.3F);
        this.subEntities = new DronePart[]{pipe, back, body, leg1, leg2};
    }
    @Override
    public boolean isMultipartEntity() {
        return true;
    }
    public DronePart[] getSubEntities() {
        return this.subEntities;
    }
    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.subEntities;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this));
        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new ClawAttackGoal(this, 1, true));
        this.goalSelector.addGoal(3, new MountedGunAttackGoal(this, 20));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 20));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, entity -> Faction.isEnemies(this, entity) || entity.getMobType().equals(MobType.UNDEAD)));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (((this.getX() - this.xo)*(this.getX() - this.xo))+((this.getZ() - this.zo)*(this.getZ() - this.zo))>0.0002) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "mAttack", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("melee_attack", RawAnimation.begin().thenPlay("melee_attack")));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("gun_firing", RawAnimation.begin().thenPlay("gun_firing")));
        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("stun", RawAnimation.begin().thenPlay("stun")));
        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            } else {
                return PlayState.STOP;
            }
        }));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 120.0D);
    }
    @Override
    public void tick() {
        super.tick();
        updateSubentities();
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isStunned()){
            return super.hurt(pSource, pAmount*2);
        }
        return super.hurt(pSource, pAmount);
    }
    public void updateSubentities(){
        for (DronePart part : this.subEntities) {
            part.xo = part.getX();
            part.yo = part.getY();
            part.zo = part.getZ();
            part.xOld = part.getX();
            part.yOld = part.getY();
            part.zOld = part.getZ();
        }
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        float[] offsets = new float[] { -0.5f, -0.5F, 0.0F, 0.0F, 0.0F };
        float[] lateralOffsets = new float[] { -1F, 0F, 0.0F, -0.5F, 0.5F };
        double yawRad = Math.toRadians(this.getYRot());
        for (int i = 0; i < this.subEntities.length; i++) {
            DronePart part = this.subEntities[i];
            float fDistance = offsets[i];
            float lateral = lateralOffsets[i];

            double offsetX = -Math.sin(yawRad) * fDistance + Math.cos(yawRad) * lateral;
            double offsetZ =  Math.cos(yawRad) * fDistance + Math.sin(yawRad) * lateral;
            //pipe
            if(i==0){
                part.setPosRaw(x + offsetX, y+2.8, z + offsetZ);
            }
            //back
            else if(i == 1){
                part.setPosRaw(x + offsetX, y+1.7, z + offsetZ);
            }
            //body
            else if(i == 2){
                part.setPosRaw(x + offsetX, y+1.7, z + offsetZ);
            }
            else{
                part.setPosRaw(x + offsetX, y, z + offsetZ);
            }
            part.setOldPosAndRot();
            part.refreshDimensions();
        }
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(INACCURACY, 8F);
    }
    public void lowerInaccuracy(){
        float inaccuracy = getInaccuracy();
        if(inaccuracy>0){
            this.entityData.set(INACCURACY, getInaccuracy()-0.2F);
        }
    }
    public float getInaccuracy(){
        return this.entityData.get(INACCURACY);
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
    @Override
    public int getDefaultStunDuration() {
        return 100;
    }
    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        return super.addEffect(effectInstance, entity)
                && this.handleAddEffectStun(effectInstance, entity);
    }
    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
            this.random, 
            ModSounds.RRC_DRONE_DEATH_1.get(), 
            ModSounds.RRC_DRONE_DEATH_2.get()
        );
    };
    protected SoundEvent getAmbientSound() {
        return ModSounds.RRC_DRONE_IDLE.get();
    };
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return MobUtil.getSound(
            this.random, 
            ModSounds.RRC_DRONE_HURT_1.get(),
            ModSounds.RRC_DRONE_HURT_2.get(),
            ModSounds.RRC_DRONE_HURT_3.get(),
            ModSounds.RRC_DRONE_HURT_4.get(),
            ModSounds.RRC_DRONE_HURT_5.get(),
            ModSounds.RRC_DRONE_HURT_6.get(),
            ModSounds.RRC_DRONE_HURT_7.get(),
            ModSounds.RRC_DRONE_HURT_8.get()
        );
    };
    protected SoundEvent getStepSound() {
        if(this.random.nextFloat() < 0.4F){
            return ModSounds.RRC_DRONE_WALK.get();
        }else{
            return SoundEvents.IRON_GOLEM_STEP;
        }
    }
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), 1.0F);
    }
    protected float getSoundVolume() {
        return 2F;
    };
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 18 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    };
}