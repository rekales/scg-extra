package net.zincstudios.scgextra.entity.whaler.glowing_tentacliator;

import net.zincstudios.scgextra.SCGExtra;

import net.zincstudios.scgextra.entity.TextureVarEntityGeoModel;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class GlowingTentacliatorModel <T extends GlowingTentacliatorEntity> extends TextureVarEntityGeoModel<T> {

	public GlowingTentacliatorModel() {
        super(SCGExtra.asResource("whaler/glowing_tentacliator"));
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