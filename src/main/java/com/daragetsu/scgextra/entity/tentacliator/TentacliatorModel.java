package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;

import com.daragetsu.scgextra.entity.TextureVarEntityGeoModel;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class TentacliatorModel <T extends TentacliatorEntity> extends TextureVarEntityGeoModel<T> {

	public TentacliatorModel() {
        super(SCGExtra.asResource("whaler/tentacliator"));
	}

    @Override
	public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
		if(leftArm != null) {
			if(!animatable.getMainHandItem().is(Items.TRIDENT)) {
				leftArm.setRotX((float)Math.toRadians(90));
			}
		}
	}
}