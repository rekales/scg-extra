package net.zincstudios.scgextra.entity.neutral.nether.head_hunter;

import net.zincstudios.scgextra.entity.common.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModEffects;

public class HeadHunterEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(HeadHunterEntity.class, EntityDataSerializers.BOOLEAN);

    public HeadHunterEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.MAX_HEALTH, 120.0)
        .add(Attributes.MOVEMENT_SPEED, 0.4)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
        .add(Attributes.ATTACK_KNOCKBACK, 0.5)
        .add(Attributes.ATTACK_DAMAGE, 8)
        .add(Attributes.ARMOR, 6)
        .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new HeadHunterAttackGoal(this, 0.6, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.5, 10));
        this.goalSelector.addGoal(3, new HeadHunterRunGoal(this, 0.7f));
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
        }).triggerableAnim("attack", RawAnimation.begin().thenPlay("attack")));
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
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    };
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    };
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    };
    protected float getSoundVolume() {
        return 0.8F;
    };
    protected void playStepSound(net.minecraft.core.BlockPos pPos, net.minecraft.world.level.block.state.BlockState pState) {
        this.playSound(SoundEvents.WITHER_SKELETON_STEP, this.getSoundVolume(), this.getVoicePitch());
    };
    @Override
    protected void tickDeath() {
        MobUtil.tickDeath(this, 22);
    }
    @Override
    public boolean fireImmune() {
        return true;
    }
    @Override
    public boolean addEffect(MobEffectInstance effectInstance, Entity entity) {
        if(effectInstance.getEffect().equals(ModEffects.LACERATED.get())){
            return false;
        }
        return super.addEffect(effectInstance, entity);
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_RUNNING, false);
    }
    public boolean isRunning(){
        return this.entityData.get(IS_RUNNING);
    }

    public void setRunning(boolean bool){
        this.entityData.set(IS_RUNNING, bool);
    }
    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        AABB aabb = new AABB(
            pos.getX()-50,
            pos.getY()-50,
            pos.getZ()-50,
            pos.getX()+50,
            pos.getY()+50,
            pos.getZ()+50
        );
        if(level.getEntitiesOfClass(HeadHunterEntity.class, aabb).size()>3){
            return false;
        }
        return level.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(type, level, spawnType, pos, random);
    }
    @Override
    public void tick() {
        super.tick();
    }
}