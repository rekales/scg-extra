package net.zincstudios.scgextra.entity.asgharian.worker;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.Faction;
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
import top.ribs.scguns.init.ModEffects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AsgharWorkerEntity extends Monster implements GeoEntity, Stunnable, HeadShotHandler {

    private static final int STUN_DURATION = 60;

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    // Server-side only for stunnable handling
    private int headshotCounter = 0;
    private boolean stunCooldown = false;
    private boolean stunned = false;

    private int currentAttack = 0;  // 0: none, 1: saw, 2: claw
    private int hurtDelay = -1;

    public AsgharWorkerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
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
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.MAX_HEALTH, 300.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (this.hurtDelay > 0) {
            this.hurtDelay--;
        } else {
            if (this.currentAttack != 0) {
                LivingEntity target = this.getTarget();
                if (target != null) {
                    double distToEnemySqr = this.getPerceivedTargetDistanceSquareForMeleeAttack(target);
                    double reach = this.getAttackReachSqr(target) * 1.2;
                    if (distToEnemySqr <= reach) {
                        super.doHurtTarget(target);
                        SCGExtra.LOGGER.debug("hurt: " + this.tickCount);
                        if (this.currentAttack == 1) {
                            target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 80));
                        }
                    }
                }
                this.currentAttack = 0;
            }
        }
    }

    private double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!this.level().isClientSide) {
            if (this.currentAttack != 0) return false;

            this.currentAttack = this.random.nextIntBetweenInclusive(1,2);
            if (this.currentAttack == 1) {
                this.triggerAnim("melee", "saw");
            } else if (this.currentAttack == 2){
                this.triggerAnim("melee", "claw");
            }
            this.hurtDelay = 14;
        }
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
            if (state.isMoving()) {
                state.setAnimation(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "melee", 2, state -> PlayState.STOP)
                .triggerableAnim("claw", RawAnimation.begin().thenPlay("claw"))
                .triggerableAnim("saw", RawAnimation.begin().thenPlay("saw"))
        );

        controllers.add(new AnimationController<>(this, "behaviour", 2, state -> PlayState.STOP)
                .triggerableAnim("stun", RawAnimation.begin().thenPlayAndHold("stun_start"))
                .triggerableAnim("end_stun", RawAnimation.begin().thenPlay("stun_end"))
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }).setAnimationSpeed(1.1f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected void tickDeath() {
        // Override to only extend death time
        ++this.deathTime;
        if (this.deathTime >= 37 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        if (this.headshotCounter < CommonConfig.abilityWeaknessHeadshots-1 || !this.stunCooldown) {
            this.headshotCounter++;
        }

        SCGExtra.LOGGER.debug("h: " + this.headshotCounter);
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
        if (ticksLeft == 15) {
            this.triggerAnim("behaviour", "end_stun");
        }
        return false;
    }
}
