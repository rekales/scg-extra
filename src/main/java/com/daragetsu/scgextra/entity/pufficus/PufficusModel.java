package com.daragetsu.scgextra.entity.pufficus;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PufficusModel<T extends PufficusEntity> extends GeoModel<T> {

    public PufficusModel() {
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return SCGExtra.asResource("geo/pufficus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return SCGExtra.asResource("textures/entity/pufficus/pufficus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return SCGExtra.asResource("animation/pufficus.geo.json");
    }
}