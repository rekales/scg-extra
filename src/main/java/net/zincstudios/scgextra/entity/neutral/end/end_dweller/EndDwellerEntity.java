package net.zincstudios.scgextra.entity.neutral.end.end_dweller;

import net.zincstudios.scgextra.entity.neutral.NeutralEntities;
import net.zincstudios.scgextra.sounds.NeutralSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EndDwellerEntity extends Animal implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public EndDwellerEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 20.0)
        .add(Attributes.MOVEMENT_SPEED, 0.4)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
        .add(Attributes.ARMOR, 0)
        .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    protected void registerGoals() {
        // super.registerGoals();
        // this.goalSelector.addGoal(0, new FloatGoal(this));
        // this.goalSelector.addGoal(1, new PanicGoal(this, 0.5));
        // this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, (double)0.4F));
        // this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        // this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                if(state.getAnimatable().onGround()){
                    state.setAndContinue(RawAnimation.begin().thenLoop("walk_insect"));
                }else{
                    state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                }
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel arg0, AgeableMob arg1) {
        return NeutralEntities.END_DWELLER.get().create(arg0);
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return NeutralSounds.END_DWELLER_HURT.get();
    };
    protected SoundEvent getAmbientSound() {
        return NeutralSounds.END_DWELLER_IDLE.get();
    };
    protected float getSoundVolume() {
        return 0.8F;
    };
}
