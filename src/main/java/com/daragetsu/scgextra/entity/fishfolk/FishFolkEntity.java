package com.daragetsu.scgextra.entity.fishfolk;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Witch;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import com.daragetsu.scgextra.SCGExtra;

import java.util.ArrayList;
import java.util.Random;

public class FishFolkEntity extends Drowned implements GeoEntity{
    private final ResourceLocation texture;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public FishFolkEntity(EntityType<? extends Drowned> entity, Level level) {
        super(entity, level);
        if (new Random().nextInt(0, 2)==1) {
            texture = SCGExtra.asResource("textures/entity/fishfolk/fishfolk_1.png");
        } else {
            texture = SCGExtra.asResource("textures/entity/fishfolk/fishfolk_2.png");
        }
    }
    public ResourceLocation getTexture() {
        return texture;
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
            ItemStack gun = new ItemStack(guns.get(new Random().nextInt(guns.size())));
            gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
            this.setItemSlot(EquipmentSlot.MAINHAND, gun);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 35.0D)
        .add(Attributes.MOVEMENT_SPEED, (double)0.23F)
        .add(Attributes.ATTACK_DAMAGE, 3.0D)
        .add(Attributes.ARMOR, 4.0D)
        .add(Attributes.MAX_HEALTH, 20.0D)
        .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }
   @Override
   protected void addBehaviourGoals() {
        super.addBehaviourGoals();
        this.goalSelector.addGoal(2, new FishFolkAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
            this, 
            Skeleton.class, 
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
    }
    static class FishFolkAttackGoal extends ZombieAttackGoal {
        private final FishFolkEntity fish_folk;
        public FishFolkAttackGoal(FishFolkEntity pFishFolk, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
           super(pFishFolk, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
           this.fish_folk = pFishFolk;
        }
        public boolean canUse() {
           return super.canUse() && this.fish_folk.okTarget(this.fish_folk.getTarget());
        }
        public boolean canContinueToUse() {
           return super.canContinueToUse() && this.fish_folk.okTarget(this.fish_folk.getTarget());
        }
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
    }
}