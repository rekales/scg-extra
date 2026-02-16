package com.daragetsu.scgextra.entity.armored_whale;

import net.minecraft.world.damagesource.DamageSource;
import com.daragetsu.scgextra.Faction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArmoredWhaleEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean didSlam = false;
    //commented out cause i was saving
    // private final ArmoredWhalePart[] subEntities;
    // public final ArmoredWhalePart head;
    // private final ArmoredWhalePart neck;
    // private final ArmoredWhalePart body;
    // private final ArmoredWhalePart tail1;
    // private final ArmoredWhalePart tail2;
    // private final ArmoredWhalePart tail3;
    // private final ArmoredWhalePart wing1;
    // private final ArmoredWhalePart wing2;
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
        this.goalSelector.addGoal(4, new SlamAttackGoal(this));
        this.goalSelector.addGoal(3, new DeployMinesGoal(this, 800, 1.1F, 4, 6));

        // Bosses will prioritize players and does not require line of sight to maintain targeting to avoid cheese
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                // Avoid retaliation from friendly fire
                if (this.mob.getLastHurtByMob() != null && Faction.isFriendlies(this.mob, this.mob.getLastHurtByMob())) {
                    return false;
                }
                return super.canUse();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
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
