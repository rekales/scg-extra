package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.world.damagesource.DamageSource;
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
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModEffects;
import net.zincstudios.scgextra.Faction;

public class DroneEntity extends Monster implements GeoEntity{
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean deathAnimDone = false;
    private int deathTick = 0;
    public DroneEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StunnedGoal(this));
        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.goalSelector.addGoal(2, new ClawAttackGoal(this, 1, true));
        this.goalSelector.addGoal(3, new MountedGunAttackGoal(this, 20));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 20));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, entity -> Faction.isEnemies(this, entity)));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (((this.getX() - this.xo)*(this.getX() - this.xo))+((this.getZ() - this.zo)*(this.getZ() - this.zo))>0.0002) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "mAttack", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("melee_attack", RawAnimation.begin().thenPlay("melee_attack")));
        controllers.add(new AnimationController<>(this, "attack", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("gun_firing", RawAnimation.begin().thenPlay("gun_firing")));
        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("stun", RawAnimation.begin().thenPlay("stun")));
        controllers.add(new AnimationController<>(this, "dBehaviour", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("death", RawAnimation.begin().thenPlay("death")));
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 35.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.2F)
        .add(Attributes.ATTACK_DAMAGE, 5.0D)
        .add(Attributes.ARMOR, 12.0D)
        .add(Attributes.MAX_HEALTH, 120.0D);
    }
    @Override
    public void tick() {
        super.tick();
        if(this.getHealth()<=1 && deathTick <= 29){
            if(this.deathTick==0){
                this.triggerAnim("dBehaviour", "death");
                this.setNoAi(true);
            }
            deathTick++;
        }else if(deathTick > 29){
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
    public boolean isStunned(){return this.hasEffect(ModEffects.DEAFENED.get()) && this.hasEffect(ModEffects.BLINDED.get());};
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.isStunned()){
            return super.hurt(pSource, pAmount*2);
        }
        return super.hurt(pSource, pAmount);
    }
}