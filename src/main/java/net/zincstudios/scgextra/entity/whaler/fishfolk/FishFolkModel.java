package net.zincstudios.scgextra.entity.whaler.fishfolk;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.TextureVarEntityGeoModel;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class FishFolkModel<T extends FishFolkEntity> extends TextureVarEntityGeoModel<T> {

	public FishFolkModel() {
        super(SCGExtra.asResource("whaler/fish_folk"));
	}

	@Override
	public void setCustomAnimations(T animatable, long instanceId,
			AnimationState<T> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
		CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
		CoreGeoBone rightLeg = this.getAnimationProcessor().getBone("right_leg");
		CoreGeoBone leftLeg = this.getAnimationProcessor().getBone("left_leg");
		if(rightArm != null) {
			if(!animatable.getMainHandItem().is(Items.TRIDENT)) {
				rightArm.setRotX((float)Math.toRadians(90));
			}
		}
		if(animatable.isSitting()){
			if(rightArm!=null)rightArm.setRotX((float)Math.toRadians(90));
			if(leftArm!=null)leftArm.setRotX((float)Math.toRadians(90));
			if(rightLeg!=null)rightLeg.setRotX(-80F);
			if(leftLeg!=null)leftLeg.setRotX(-80F);
		}
	}
}