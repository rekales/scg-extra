package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.util.RandomSource;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.cog.gigantes.FlyCloseToTargetGoal;
import net.zincstudios.scgextra.entity.common.goal.MobHurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.WreckersSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.item.GunItem;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@ParametersAreNonnullByDefault
public class WreckerHelicubeEntity extends FlyingMob implements GeoEntity, Enemy {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");

    private static final EntityDataAccessor<Boolean> FIRING =
            SynchedEntityData.defineId(WreckerHelicubeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ARM_VARIANT =
            SynchedEntityData.defineId(WreckerHelicubeEntity.class, EntityDataSerializers.BOOLEAN);

    private static final float EXPLOSION_RADIUS = 1.5F;
    private static final double CLAW_REACH = 2.4D;
    private static final int CLAW_COOLDOWN_TICKS = 20;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int clawCooldown = 0;
    private boolean exploded = false;

    public WreckerHelicubeEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 1.2D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.MAX_HEALTH, 15.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WreckerHelicubeAttackGoal(this, (GunItem) top.ribs.scguns.init.ModItems.GREASER_SMG.get())
                .burstAmount(10)
                .burstIntervalTicks(2)
                .maxRange(16)
                .attackInterval(200)
                .spread(12.0F));
        this.goalSelector.addGoal(4, new FlyCloseToTargetGoal(this, 1.0F, 16.0F, 6.0F) {
            @Override
            public boolean canUse() {
                return !WreckerHelicubeEntity.this.isArmVariant() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new FlyCloseToTargetGoal(this, 1.2F, 1.8F, 0.0F) {
            @Override
            public boolean canUse() {
                return WreckerHelicubeEntity.this.isArmVariant() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new RandomFloatAroundGoal(this));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new MobHurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FIRING, false);
        this.entityData.define(ARM_VARIANT, false);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                                  @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.setArmVariant(level.getRandom().nextBoolean());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("ArmVariant", this.isArmVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setArmVariant(tag.getBoolean("ArmVariant"));
    }

    public void setArmVariant(boolean armVariant) {
        this.entityData.set(ARM_VARIANT, armVariant);
    }

    public boolean isArmVariant() {
        return this.entityData.get(ARM_VARIANT);
    }

    public void setFiring(boolean firing) {
        this.entityData.set(FIRING, firing);
    }

    public boolean isFiring() {
        return this.entityData.get(FIRING);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }
        if (this.clawCooldown > 0) {
            this.clawCooldown--;
        }
        if (!this.isArmVariant()) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.clawCooldown <= 0
                && this.distanceToSqr(target) <= CLAW_REACH * CLAW_REACH) {
            this.clawCooldown = CLAW_COOLDOWN_TICKS;
            this.doHurtTarget(target);
        }
    }

    @ParametersAreNonnullByDefault
    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide() && !this.exploded) {
            this.exploded = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_RADIUS, Level.ExplosionInteraction.NONE);
        }
        super.die(cause);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 3, state -> {
            if (this.isFiring()) {
                return state.setAndContinue(ATTACK);
            }
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "death", 0, state -> {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(DEATH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected float getSoundVolume() {
        return 0.7F;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return WreckersSounds.HELICUBE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(this.random, WreckersSounds.HELICUBE_HURT_1.get(), WreckersSounds.HELICUBE_HURT_2.get());
    }

    static class RandomFloatAroundGoal extends Goal {

        private final WreckerHelicubeEntity helicube;

        RandomFloatAroundGoal(WreckerHelicubeEntity helicube) {
            this.helicube = helicube;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.helicube.getTarget();
            if (target != null && target.isAlive()) {
                return false;
            }
            MoveControl moveControl = this.helicube.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            }
            double dx = moveControl.getWantedX() - this.helicube.getX();
            double dy = moveControl.getWantedY() - this.helicube.getY();
            double dz = moveControl.getWantedZ() - this.helicube.getZ();
            double distSqr = dx * dx + dy * dy + dz * dz;
            return distSqr < 1.0D || distSqr > 3600.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.helicube.getRandom();
            double x = this.helicube.getX() + (random.nextFloat() * 2.0F - 1.0F) * 10.0F;
            double y = this.helicube.getY() + (random.nextFloat() * 2.0F - 1.0F) * 4.0F;
            double z = this.helicube.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 10.0F;
            this.helicube.getMoveControl().setWantedPosition(x, y, z, 0.9D);
        }
    }
}
