package com.daragetsu.scgextra.entity.fishfolk;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.animated.AnimatedUnderWaterGunItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import com.daragetsu.scgextra.SCGExtra;

import java.util.ArrayList;
import java.util.Random;

public class FishFolkEntity extends Drowned{
    private final ResourceLocation texture;
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
}