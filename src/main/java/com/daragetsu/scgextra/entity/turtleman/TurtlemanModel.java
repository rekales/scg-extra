package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TurtlemanModel<T extends TurtlemanEntity> extends GeoModel<T> {

    public TurtlemanModel() {
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return SCGExtra.asResource("geo/turtleman.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return SCGExtra.asResource("textures/entity/turtleman/turtleman.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return SCGExtra.asResource("animation/turtleman.geo.json");
    }
}