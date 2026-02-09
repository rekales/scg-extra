package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TentacliatorModel <T extends TentacliatorEntity> extends GeoModel<TentacliatorEntity>{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("tentacliator"), "main");

	public TentacliatorModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(TentacliatorEntity arg0) {
		return SCGExtra.asResource("animations/tentacliator.animation.json");
	}
	
	@Override
	public ResourceLocation getModelResource(TentacliatorEntity arg0) {
		return SCGExtra.asResource("geo/tentacliator.geo.json");
	}
	
	@Override
	public ResourceLocation getTextureResource(TentacliatorEntity arg0) {
		return SCGExtra.asResource("textures/entity/tentacliator/tentacliator.png");
	}
}