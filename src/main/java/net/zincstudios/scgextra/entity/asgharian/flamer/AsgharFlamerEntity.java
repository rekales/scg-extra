package net.zincstudios.scgextra.entity.asgharian.flamer;

import net.minecraft.world.entity.*;
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
import net.zincstudios.scgextra.entity.asgharian.SimpleBurstGunAttackGoal;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AsgharFlamerEntity extends GunnerEntity implements GeoEntity {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public AsgharFlamerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isLeftHanded() {
        return true;
    }

    // TODO: adjust engagement distance to be closer
    // TODO: maybe custom and stupider gun attack ai that allows moving while aiming
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SimpleBurstGunAttackGoal<>(this, 3, 4)
                .runAndGun()
                .approachDist(4)
                .attackInterval(30)
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
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.MAX_HEALTH, 70.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (state.getAnimatable().isAggressive()) {
                if (state.isMoving()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("aim_walk"));
                } else {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("aim"));
                }
            } else {
                if (state.isMoving()) {
                    state.setAnimation(RawAnimation.begin().thenLoop("walk"));
                } else {
                    state.setAnimation(RawAnimation.begin().thenLoop("idle"));
                }
            }
            return PlayState.CONTINUE;
        }));

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
        if (this.deathTime >= 55 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }
}
