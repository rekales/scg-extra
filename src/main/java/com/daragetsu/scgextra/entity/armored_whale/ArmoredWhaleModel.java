package com.daragetsu.scgextra.entity.armored_whale;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ArmoredWhaleModel extends GeoModel<ArmoredWhaleEntity>{

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

    @Override
    public void setCustomAnimations(ArmoredWhaleEntity animatable, long instanceId, AnimationState<ArmoredWhaleEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        //
        // NOTE: potentially expensive calls
        CoreGeoBone leftGunBone = getAnimationProcessor().getBone("left_gun");
        CoreGeoBone rightGunBone = getAnimationProcessor().getBone("right_gun");

        if (leftGunBone != null) {
            leftGunBone.setRotY(0.15f-animatable.getLeftGunYRot());  // Not sure why I need this weird offset
        }
        if (rightGunBone != null) {
            rightGunBone.setRotY(0.15f-animatable.getRightGunYRot());
        }
    }
}
