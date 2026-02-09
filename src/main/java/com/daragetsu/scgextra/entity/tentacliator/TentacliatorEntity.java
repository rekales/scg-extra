package com.daragetsu.scgextra.entity.tentacliator;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.animated.AnimatedUnderWaterGunItem;

public class TentacliatorEntity extends Drowned implements GeoEntity{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public Random random = new Random();
    public TentacliatorEntity(EntityType<? extends Drowned> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        int i = pRandom.nextInt(20);
        if (i < 10) {
           this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        } else {
            ArrayList<Item> guns = new ArrayList<>();
            for(RegistryObject<Item> item : ModItems.REGISTER.getEntries()){
                if(item.get() instanceof AnimatedUnderWaterGunItem){
                    guns.add(item.get());
                }
            }
            ItemStack gun = new ItemStack(guns.get(random.nextInt(guns.size())));
            gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
            this.setItemSlot(EquipmentSlot.MAINHAND, gun);
        }
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 35.0D)
        .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
        .add(Attributes.ATTACK_DAMAGE, 3.0D)
        .add(Attributes.ARMOR, 6.0D)
        .add(Attributes.MAX_HEALTH, 40.0D)
        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }
   @Override
   protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Skeleton.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(
            this, 
            Player.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Creeper.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Spider.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Witch.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Pillager.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Animal.class, 
            10, 
            true, 
            false, 
            this::okTarget
        ));
        this.targetSelector.addGoal(3, new InkAttackGoal(
            this
        ));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (state.isMoving()) {
                state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "special", 0, state -> PlayState.CONTINUE)
        .triggerableAnim("special_attack", RawAnimation.begin().thenPlay("special_attack")));
    }
}