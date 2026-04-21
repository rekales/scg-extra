package net.zincstudios.scgextra.entity.asgharian.surgeon;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.asgharian.AsgharianEntities;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.entity.ai.AIType;

public class AsgharSurgeonEntity extends GunnerEntity implements GeoEntity {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public AsgharSurgeonEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    // TODO: custom gun attack
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new AsgharSurgeonAttackGoal<>(this, this.getMainHandItem(),
                1.0F, AIType.RECKLESS, 3, 10));
        this.goalSelector.addGoal(3, new ConstantSummonGoal(this, 140, AsgharianEntities.FAILED_ONE.get()));
        this.goalSelector.addGoal(4, new HealNearbyGoal<>(this, 8, LivingEntity.class,
                entity -> Faction.isFriendlies(entity, this)));
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
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 7.0D)
                .add(Attributes.MAX_HEALTH, 200.0D);
    }

    @Override
    public void onGunAttack(LivingEntity target, ItemStack itemStack) {
//        SCGExtra.LOGGER.debug("tick: " + this.tickCount);
    }

    // TODO: gun attack animation
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

        controllers.add(new AnimationController<>(this, "behaviour", 2, state -> PlayState.STOP)
                .triggerableAnim("melee", RawAnimation.begin().thenPlay("saw"))
                .setAnimationSpeed(1.2f)
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }).setAnimationSpeed(1.3f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }
}
