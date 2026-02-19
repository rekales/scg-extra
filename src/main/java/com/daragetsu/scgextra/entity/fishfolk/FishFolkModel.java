package com.daragetsu.scgextra.entity.fishfolk;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class FishFolkModel<T extends FishFolkEntity> extends GeoModel<FishFolkEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("fish_folk"), "main");
	private static final ResourceLocation GEO = SCGExtra.asResource("geo/fish_folk.geo.json");
	private static final ResourceLocation ANIM = SCGExtra.asResource("animations/fish_folk.animation.json");

	public FishFolkModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(FishFolkEntity entity) {
		return ANIM;
	}

	@Override
	public ResourceLocation getModelResource(FishFolkEntity entity) {
		return GEO;
	}

	@Override
	public ResourceLocation getTextureResource(FishFolkEntity entity) {
		return entity.getTexture();
	}
	@Override
	public void setCustomAnimations(FishFolkEntity animatable, long instanceId,
			AnimationState<FishFolkEntity> animationState) {
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
		if(animatable.isPassenger()){
			if(rightArm!=null)rightArm.setRotX((float)Math.toRadians(90));
			if(leftArm!=null)leftArm.setRotX((float)Math.toRadians(90));
			if(rightLeg!=null)rightLeg.setRotX(-80F);
			if(leftLeg!=null)leftLeg.setRotX(-80F);
		}
	}
}