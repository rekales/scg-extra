package com.daragetsu.scgextra.entity.guardian_statue;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GuardianStatueModel<T extends GuardianStatueEntity> extends GeoModel<T> {

    public GuardianStatueModel() {
    }

    @Override
    public ResourceLocation getModelResource(T t) {
        return SCGExtra.asResource("geo/guardian_statue.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T t) {
        return SCGExtra.asResource("textures/entity/guardian_statue/guardian_statue.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T t) {
        return SCGExtra.asResource("animation/guardian_statue.geo.json");
    }
}