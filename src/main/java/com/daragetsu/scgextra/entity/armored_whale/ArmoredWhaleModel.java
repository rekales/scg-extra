package com.daragetsu.scgextra.entity.armored_whale;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ArmoredWhaleModel<T extends ArmoredWhaleEntity> extends GeoModel<ArmoredWhaleEntity>{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("armored_whale"), "main");
    public ArmoredWhaleModel(){
    }

    @Override
    public ResourceLocation getModelResource(ArmoredWhaleEntity entity) {
        return SCGExtra.asResource("geo/armored_whale.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArmoredWhaleEntity entity) {
        return SCGExtra.asResource("textures/entity/armored_whale/armored_whale.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArmoredWhaleEntity entity) {
        return SCGExtra.asResource("animation/armored_whale.geo.json");
    }
    
}
