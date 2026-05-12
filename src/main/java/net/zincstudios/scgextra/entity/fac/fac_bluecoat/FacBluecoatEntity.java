package net.zincstudios.scgextra.entity.fac.fac_bluecoat;

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
import net.zincstudios.scgextra.entity.EntityTypeTags;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.MobUtil;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.sounds.FACSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FacBluecoatEntity extends GunnerEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation HOLD_ATTACK = RawAnimation.begin().thenLoop("hold_attack");
    private static final RawAnimation WALK_ATTACK = RawAnimation.begin().thenLoop("walk_attack");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FacBluecoatEntity(EntityType<? extends GunnerEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 1, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 1, true, false,
                this::isHostileFactionTarget));
    }

    private boolean isHostileFactionTarget(LivingEntity entity) {
        if (entity == this) {
            return false;
        }

        if (Faction.isEnemies(this, entity)) {
            return true;
        }

        return entity.getType().is(EntityTypeTags.RRC);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk/idle/aim", 2, state -> {
            boolean moving = state.isMoving() || this.getNavigation().isInProgress();

            if (state.getAnimatable().isAiming()) {
                return state.setAndContinue(moving ? WALK_ATTACK : HOLD_ATTACK);
            }

            return state.setAndContinue(moving ? WALK : IDLE);
        }).setAnimationSpeed(1.15));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_BLUECOAT_HURT_1.get(),
                FACSounds.FAC_BLUECOAT_HURT_2.get(),
                FACSounds.FAC_BLUECOAT_HURT_3.get()
        );
    }

    protected SoundEvent getAmbientSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_BLUECOAT_IDLE_1.get(),
                FACSounds.FAC_BLUECOAT_IDLE_2.get(),
                FACSounds.FAC_BLUECOAT_IDLE_3.get()
        );
    }

    protected SoundEvent getDeathSound() {
        return MobUtil.getSound(
                this.random,
                FACSounds.FAC_BLUECOAT_DEATH_1.get(),
                FACSounds.FAC_BLUECOAT_DEATH_2.get(),
                FACSounds.FAC_BLUECOAT_DEATH_3.get()
        );
    }
}

