package net.zincstudios.scgextra.entity.fac.trench_goblin;

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
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.brain.BrainCommons;
import net.zincstudios.scgextra.entity.common.client.ExpandedAnimationController;
import net.zincstudios.scgextra.sounds.FACSounds;
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
public class TrenchGoblinEntity extends GunnerEntity implements GeoEntity {

    static final int MELEE_DAMAGE_DELAY = 10;
    static final int MELEE_DURATION = 18;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation ATTACK_NO_LEGS = RawAnimation.begin().thenPlay("attack_no_legs");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public TrenchGoblinEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32F)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return TrenchGoblinAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<TrenchGoblinEntity> getBrain() {
        return (Brain<TrenchGoblinEntity>) super.getBrain();
    }

    protected Brain.Provider<TrenchGoblinEntity> brainProvider() {
        return Brain.provider(TrenchGoblinAi.MEMORY_TYPES, TrenchGoblinAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cogTrenchGoblinBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        BrainCommons.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && brain.getTimeUntilExpiry(ModBrainMemories.DELAYED_MELEE.get()) == MELEE_DURATION) {
            this.triggerAnim("attack", "melee");
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new ExpandedAnimationController<>(this, "main", 2, state ->
                state.setAndContinue(state.isMoving() ? WALK : IDLE))
        );

        controllers.add(new ExpandedAnimationController<>(this, "attack", 0, state -> PlayState.STOP)
                .triggerableAnim("melee", (ctr) -> {
                    TrenchGoblinEntity entity = (TrenchGoblinEntity) ctr.getAnimatable();
                    AnimationController<?> mainCtr = entity.getAnimatableInstanceCache().getManagerForId(entity.getId()).getAnimationControllers().get("main");
                    return mainCtr.getCurrentRawAnimation() == WALK ? ATTACK_NO_LEGS : ATTACK;
                })
                .setAnimationSpeed(1.4f)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_HURT_1.get(),
                FACSounds.TRENCH_GOBLIN_HURT_2.get(),
                FACSounds.TRENCH_GOBLIN_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_IDLE_1.get(),
                FACSounds.TRENCH_GOBLIN_IDLE_2.get(),
                FACSounds.TRENCH_GOBLIN_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.TRENCH_GOBLIN_DEATH_1.get(),
                FACSounds.TRENCH_GOBLIN_DEATH_2.get()
        );
    }
}

