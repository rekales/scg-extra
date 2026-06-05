package net.zincstudios.scgextra.entity.cog.centipede;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.ai.StunnedWithVisualGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CogCentipedeEntity extends GunnerEntity implements GeoEntity, Stunnable, HeadShotHandler {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final int STUN_DURATION = 80;

    // Server-side only for stunnable handling
    private int headshotCounter = 0;
    private boolean stunCooldown = false;
    private boolean stunned = false;

    public CogCentipedeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this));
        this.goalSelector.addGoal(3, new CogCentipedeAttackGoal(this, 120)
                .maxRange(10)
                .approachDist(4)
                .attackInterval(80)
        );
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 400.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (state.isMoving()) {
                state.setAnimation(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "gun", 2, state -> PlayState.STOP)
                .triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))
        );

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("stun", RawAnimation.begin().thenPlayAndHold("stun_start"))
                .triggerableAnim("end_stun", RawAnimation.begin().thenPlay("stun_end"))
                .triggerableAnim("slam", RawAnimation.begin().thenPlay("slam"))
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected void tickDeath() {
        // Override to only extend death time
        ++this.deathTime;
        if (this.deathTime >= 42 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        if (this.headshotCounter < CommonConfig.abilityWeaknessHeadshots-1 || !this.stunCooldown) {
            this.headshotCounter++;
        }
        return false;
    }

    @Override
    public int shouldStun() {
        if (!CommonConfig.enableAbilityWeakness) return 0;

        if (this.headshotCounter >= CommonConfig.abilityWeaknessHeadshots) {
            return STUN_DURATION;
        }

        return 0;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
        if (stunned) {
            this.triggerAnim("behaviour", "stun");
        } else {
            this.headshotCounter = 0;
        }
    }

    @Override
    public void setStunCooldown(boolean stunCooldown) {
        this.stunCooldown = stunCooldown;
    }

    @Override
    public boolean isStunned() {
        return this.stunned;
    }

    @Override
    public boolean tickStunned(int ticksLeft) {
        if (ticksLeft == 12) {
            this.triggerAnim("behaviour", "end_stun");
        }
        return false;
    }
}
