package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

@ParametersAreNonnullByDefault
public class WreckerHelicubeEntity extends FlyingMob implements GeoEntity, Enemy {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");

    private static final EntityDataAccessor<Boolean> FIRING =
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
                .accuracyModifier(2.0F));
        this.goalSelector.addGoal(4, new FlyCloseToTargetGoal(this, 1.0F, 16.0F, 6.0F));
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
    protected @Nullable SoundEvent getAmbientSound() {
        return WreckersSounds.HELICUBE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(this.random, WreckersSounds.HELICUBE_HURT_1.get(), WreckersSounds.HELICUBE_HURT_2.get());
    }
}
