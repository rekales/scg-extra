package com.daragetsu.scgextra.entity.armored_whale;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArmoredWhaleEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean didSlam = false;
    //gonna leave the entity as is, change to whatever you need
    public ArmoredWhaleEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void registerControllers(ControllerRegistrar controller) {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.ARMOR, 6);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(
            2, 
            new SlamAttackGoal(this)
        );
        this.goalSelector.addGoal(
            2, 
            new NearestAttackableTargetGoal<>(this, Player.class, true, false)
        );
    }

    public void setDidSlam(boolean slam){
        this.didSlam = slam;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(this.didSlam){
            setDidSlam(false);
            return false;
        }
        return super.hurt(pSource, pAmount);
    }
    
}
