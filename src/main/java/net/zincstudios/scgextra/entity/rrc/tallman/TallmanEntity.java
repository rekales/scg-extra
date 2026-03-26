package net.zincstudios.scgextra.entity.rrc.tallman;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.ModSounds;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.config.EntityEquipmentConfig;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TallmanEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private SoundEvent[] hurtSounds = {
        ModSounds.RRC_TALLMAN_HURT_1.get(),
        ModSounds.RRC_TALLMAN_HURT_2.get(),
        ModSounds.RRC_TALLMAN_HURT_3.get(),
        ModSounds.RRC_TALLMAN_HURT_4.get(),
        ModSounds.RRC_TALLMAN_HURT_5.get(),
        ModSounds.RRC_TALLMAN_HURT_6.get()
    };
    public TallmanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        EntityEquipmentConfig.equipEntity(this, "scgextra:tallman");  // NOTE: using raw string
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected void registerGoals() {
        // gun attack goal to be automatically added on finalizeSpawn
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // TODO: alert mechanic
        this.targetSelector.addGoal(1, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ARMOR, 3D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle", 2, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                // TODO: better looping
                return state.setAndContinue(RawAnimation.begin()
                                .thenPlayXTimes("idle", state.getAnimatable().random.nextIntBetweenInclusive(2,4))
                                .thenLoop("idle_2")
                );
            }
        }).setAnimationSpeed(1.3));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return hurtSounds[this.random.nextInt(hurtSounds.length)];
    };
    protected SoundEvent getAmbientSound() {
        return this.random.nextBoolean() ? ModSounds.RRC_TALLMAN_IDLE_1.get() : ModSounds.RRC_TALLMAN_IDLE_2.get();
    };
    protected SoundEvent getStepSound() {
        return this.random.nextBoolean() ?
            ModSounds.RRC_TALLMAN_WALK_1.get() :
            ModSounds.RRC_TALLMAN_WALK_2.get();
    };
    protected SoundEvent getDeathSound() {
        return this.random.nextBoolean() ?
            ModSounds.RRC_TALLMAN_DEATH_1.get() :
            ModSounds.RRC_TALLMAN_DEATH_2.get();
    };
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), 1.0F);
    }
    protected float getSoundVolume() {
        return 2F;
    };
}
