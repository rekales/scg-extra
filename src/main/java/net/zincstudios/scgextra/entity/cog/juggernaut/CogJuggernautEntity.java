package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.sounds.CogSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogJuggernautEntity extends GunnerEntity implements GeoEntity {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public CogJuggernautEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    // TODO: use brain ai
    @Override
    protected void registerGoals() {

//        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
//        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
//        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        // Bosses should prioritize players
//        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false,
//                player -> !((Player) player).isCreative() && !player.isSpectator()));
//        this.targetSelector.addGoal(3, new HurtByNonFactionGoal(this));
//        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
//                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20F)
                .add(Attributes.ARMOR, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 1200.0D);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CogJuggernautAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<CogJuggernautEntity> getBrain() {
        return (Brain<CogJuggernautEntity>) super.getBrain();
    }

    protected Brain.Provider<CogJuggernautEntity> brainProvider() {
        return Brain.provider(CogJuggernautAi.MEMORY_TYPES, CogJuggernautAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cogJuggernautBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().popPush("cogJuggernautActivityUpdate");
        CogJuggernautAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state -> {
                if (state.getAnimatable().isAggressive()) {
                    if (state.isMoving()) {
                        state.setAnimation(RawAnimation.begin().thenLoop("walk_hold"));
                    } else {
                        state.setAnimation(RawAnimation.begin().thenLoop("idle_hold"));
                    }
                } else {
                    if (state.isMoving()) {
                        state.setAnimation(RawAnimation.begin().thenLoop("walk"));
                    } else {
                        state.setAnimation(RawAnimation.begin().thenLoop("idle"));
                    }
                }
                return PlayState.CONTINUE;
                }).setAnimationSpeed(1.2f)
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
    protected @Nullable SoundEvent getAmbientSound() {
        return CogSounds.GENERAL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CogSounds.COH_JUGGERNAUT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CogSounds.COH_JUGGERNAUT_DEAD.get();
    }
}
