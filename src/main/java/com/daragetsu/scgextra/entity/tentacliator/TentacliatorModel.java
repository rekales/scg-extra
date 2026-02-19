package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TentacliatorModel <T extends TentacliatorEntity> extends GeoModel<TentacliatorEntity>{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("tentacliator"), "main");
	private static final ResourceLocation GEO = SCGExtra.asResource("geo/tentacliator.geo.json");
	private static final ResourceLocation ANIM = SCGExtra.asResource("animations/tentacliator.animation.json");

	public TentacliatorModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(TentacliatorEntity entity) {
		return ANIM;
	}

	@Override
	public ResourceLocation getModelResource(TentacliatorEntity entity) {
		return GEO;
	}

	@Override
	public ResourceLocation getTextureResource(TentacliatorEntity entity) {
		return entity.getLocation();
	}
	@Override
	public void setCustomAnimations(TentacliatorEntity animatable, long instanceId,
			AnimationState<TentacliatorEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
		if(leftArm != null) {
			if(!animatable.getMainHandItem().is(Items.TRIDENT)) {
				leftArm.setRotX((float)Math.toRadians(90));
			}
		}
	}
}