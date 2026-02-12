package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.model.GeoModel;

public class TentacliatorModel <T extends TentacliatorEntity> extends GeoModel<TentacliatorEntity>{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("tentacliator"), "main");
	private static final ResourceLocation GEO_TRIDENT = SCGExtra.asResource("geo/tentacliator_trident.geo.json");
	private static final ResourceLocation GEO_GUN = SCGExtra.asResource("geo/tentacliator_gun.geo.json");
	private static final ResourceLocation ANIM_TRIDENT = SCGExtra.asResource("animations/tentacliator_trident.animation.json");
	private static final ResourceLocation ANIM_GUN = SCGExtra.asResource("animations/tentacliator_gun.animation.json");

	public TentacliatorModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(TentacliatorEntity entity) {
		if(entity.getMainHandItem().is(Items.TRIDENT)){
			return ANIM_TRIDENT;
		}else{
			return ANIM_GUN;
		}
	}

	@Override
	public ResourceLocation getModelResource(TentacliatorEntity entity) {
		if(entity.getMainHandItem().is(Items.TRIDENT)){
			return GEO_TRIDENT;
		}else{
			return GEO_GUN;
		}
	}

	@Override
	public ResourceLocation getTextureResource(TentacliatorEntity entity) {
		return entity.getLocation();
	}
}