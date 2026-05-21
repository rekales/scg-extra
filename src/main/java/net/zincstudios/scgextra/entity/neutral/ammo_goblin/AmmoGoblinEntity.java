package net.zincstudios.scgextra.entity.neutral.ammo_goblin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class AmmoGoblinEntity extends Zombie implements GeoEntity {
    private static final int FLEE_RADIUS = 14;
    private static final int ESCAPE_GRACE_RADIUS = 15;
    private static final int ESCAPE_TICKS = 90;
    private static final int SMOKE_BOMB_LEAD_TICKS = 35;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int fleeTicks;
    private int smokeBombTicks;

    public AmmoGoblinEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, dataTag);
        this.setBaby(false);
        this.refreshDimensions();
        return data;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, FLEE_RADIUS, 1.1D, 1.5D));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Player nearestPlayer = NeutralCombatUtil.nearestSurvivalPlayer(
                this,
                ESCAPE_GRACE_RADIUS
        );
        boolean keepEscapeTimer = nearestPlayer != null;
        boolean fleeing = keepEscapeTimer
                && nearestPlayer.distanceToSqr(this) <= (double) (FLEE_RADIUS * FLEE_RADIUS);

        this.setSprinting(fleeing);

        if (keepEscapeTimer) {
            if (fleeTicks < ESCAPE_TICKS) {
                fleeTicks++;
            }
            smokeBombTicks = fleeTicks >= (ESCAPE_TICKS - SMOKE_BOMB_LEAD_TICKS)
                    ? (ESCAPE_TICKS - fleeTicks + 1)
                    : 0;
        } else {
            fleeTicks = 0;
            smokeBombTicks = 0;
        }

        if (!this.level().isClientSide() && fleeTicks >= ESCAPE_TICKS) {
            ((ServerLevel) this.level()).sendParticles(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    30, 0.6D, 0.4D, 0.6D, 0.02D
            );
            this.discard();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (this.smokeBombTicks > 0) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("smoke_bomb"));
            }
            if (state.isMoving()) {
                if (this.isSprinting()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("run"));
                }
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        if (NeutralCombatUtil.isManualSpawn(spawnReason)) {
            return true;
        }
        if (!(level instanceof ServerLevelAccessor serverLevel)) {
            return false;
        }
        if (!NeutralCombatUtil.hasOverworldGunProgression(serverLevel)) {
            return false;
        }
        BlockPos pos = this.blockPosition();
        if (NeutralCombatUtil.isWaterAtOrBelow(level, pos)) {
            return false;
        }
        if (!NeutralCombatUtil.hasNaturalGroundSupport(level, pos)) {
            return false;
        }
        if (!super.checkSpawnRules(level, spawnReason)) {
            return false;
        }
        if (!NeutralCombatUtil.passesSpawnChance(this.random, CommonConfig.spawnChanceAmmoGoblin)) {
            return false;
        }
        if (level instanceof Level vanillaLevel && vanillaLevel.isDay()) {
            return false;
        }
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextBoolean()
                ? NeutralSounds.NEUTRAL_AMMO_GOBLIN_IDLE_01.get()
                : NeutralSounds.NEUTRAL_AMMO_GOBLIN_IDLE_02.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        int roll = this.random.nextInt(3);
        if (roll == 0) return NeutralSounds.NEUTRAL_AMMO_GOBLIN_HURT_01.get();
        if (roll == 1) return NeutralSounds.NEUTRAL_AMMO_GOBLIN_HURT_02.get();
        return NeutralSounds.NEUTRAL_AMMO_GOBLIN_HURT_03.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NeutralSounds.NEUTRAL_AMMO_GOBLIN_DEAD.get();
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }
}

