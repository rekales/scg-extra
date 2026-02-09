package com.daragetsu.scgextra.entity.fishfolk;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FishFolkModel<T extends FishFolkEntity> extends GeoModel<FishFolkEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("fish_folk"), "main");


	public FishFolkModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(FishFolkEntity arg0) {
		return SCGExtra.asResource("animations/fish_folk.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(FishFolkEntity arg0) {
		return SCGExtra.asResource("geo/fish_folk.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(FishFolkEntity arg0) {
		return arg0.getTexture();
	}
}