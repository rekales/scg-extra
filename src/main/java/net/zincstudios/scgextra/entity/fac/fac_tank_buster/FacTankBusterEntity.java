package net.zincstudios.scgextra.entity.fac.fac_tank_buster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FacTankBusterEntity extends GunnerEntity implements GeoEntity, HeadShotHandler {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenLoop("hold_attack");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int headshotDamageReductionTicks = 0;

    public FacTankBusterEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.17F)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.MAX_HEALTH, 40.0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.headshotDamageReductionTicks > 0) {
            this.headshotDamageReductionTicks--;
        }
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        this.headshotDamageReductionTicks = 2;
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.headshotDamageReductionTicks > 0) {
            this.headshotDamageReductionTicks = 0;
            amount *= 0.5F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle/aim", 2, state -> {
            boolean moving = state.isMoving() || this.isActuallyMoving() || this.getNavigation().isInProgress();
            if (state.getAnimatable().isAiming()) {
                if (moving) {
                    return state.setAndContinue(WALK_ATTACK);
                }
                return state.setAndContinue(HOLD_ATTACK);
            }
            if (moving) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        }).setAnimationSpeed(1.05));
    }

    private boolean isActuallyMoving() {
        double dx = this.getX() - this.xo;
        double dz = this.getZ() - this.zo;
        return dx * dx + dz * dz > 0.000001D;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TANK_BUSTER_HURT_1.get(),
                FACSounds.FAC_TANK_BUSTER_HURT_2.get(),
                FACSounds.FAC_TANK_BUSTER_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TANK_BUSTER_IDLE_1.get(),
                FACSounds.FAC_TANK_BUSTER_IDLE_2.get(),
                FACSounds.FAC_TANK_BUSTER_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_TANK_BUSTER_DEATH_1.get(),
                FACSounds.FAC_TANK_BUSTER_DEATH_2.get(),
                FACSounds.FAC_TANK_BUSTER_DEATH_3.get()
        );
    }
}
