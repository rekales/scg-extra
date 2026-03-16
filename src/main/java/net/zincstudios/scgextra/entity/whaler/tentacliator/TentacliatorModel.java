package net.zincstudios.scgextra.entity.whaler.tentacliator;

import net.zincstudios.scgextra.SCGExtra;

import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TentacliatorModel <T extends TentacliatorEntity> extends DefaultedEntityGeoModel<T> {

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