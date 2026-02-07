package com.daragetsu.scgextra.entity.FishFolk;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import top.ribs.scguns.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import com.daragetsu.scgextra.Main;

import java.util.Random;

public class FishFolkEntity extends Drowned{
    private final ResourceLocation texture;
    public FishFolkEntity(EntityType<? extends Drowned> entity, Level level) {
        super(entity, level);
        if (new Random().nextInt(0, 2)==1) {
            texture = new ResourceLocation(Main.MOD_ID, "textures/entity/fishfolk/fishfolk_1.png");
        } else {
            texture = new ResourceLocation(Main.MOD_ID, "textures/entity/fishfolk/fishfolk_2.png");
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
            ItemStack gun = new ItemStack(ModItems.FLOUNDERGAT.get());
            gun.getOrCreateTag().putBoolean("IgnoreAmmo", true);
            this.setItemSlot(EquipmentSlot.MAINHAND, gun);
        }
    }
}
