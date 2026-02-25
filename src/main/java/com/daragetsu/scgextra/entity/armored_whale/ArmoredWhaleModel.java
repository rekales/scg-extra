package com.daragetsu.scgextra.entity.armored_whale;

import com.daragetsu.scgextra.SCGExtra;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ArmoredWhaleModel <T extends ArmoredWhaleEntity> extends DefaultedEntityGeoModel<T> {

    public ArmoredWhaleModel(){
        super(SCGExtra.asResource("whaler/armored_whale"), false);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // NOTE: potentially expensive calls, cache if necessary
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
