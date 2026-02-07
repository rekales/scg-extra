package com.daragetsu.scgextra.entity.FishFolk;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

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
}
