package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.zincstudios.scgextra.Faction;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlamingHeadEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int deathTick = 0;
    private boolean deathAnimDone = false;
    public FlamingHeadEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 600D)
                .add(Attributes.ARMOR, 12D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("move"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "fAttack", 0, state -> PlayState.CONTINUE).triggerableAnim("fire_attack", RawAnimation.begin().thenPlay("fire_attack")));
        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("death", RawAnimation.begin().thenPlay("death")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new ThrowFlamesGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 20));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, entity -> Faction.isEnemies(this, entity)));
    }
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.is(DamageTypes.IN_FIRE) || pSource.is(DamageTypes.ON_FIRE)){
            return false;
        }
        return super.hurt(pSource, pAmount);
    }
    @Override
    public boolean isOnFire() {
        return false;
    }
    @Override
    public void tick() {
        super.tick();
        if(this.getHealth()<=1 && deathTick <= 20){
            if(this.deathTick==0){
                this.triggerAnim("behaviour", "death");
                this.setNoAi(true);
            }
            deathTick++;
        }else if(deathTick > 20){
            this.deathAnimDone = true;
            this.setHealth(0);
            this.die(this.getLastDamageSource());
        }
    }
    @Override
    public void die(DamageSource pDamageSource) {
        if(deathAnimDone){
            super.die(pDamageSource);
        }else{this.setHealth(1);}
    }
}
