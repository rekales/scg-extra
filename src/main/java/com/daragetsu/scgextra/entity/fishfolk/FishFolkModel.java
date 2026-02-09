package com.daragetsu.scgextra.entity.fishfolk;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.model.GeoModel;

public class FishFolkModel<T extends FishFolkEntity> extends GeoModel<FishFolkEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("fish_folk"), "main");
	private static final ResourceLocation GEO_TRIDENT = SCGExtra.asResource("geo/fish_folk_trident.geo.json");
	private static final ResourceLocation GEO_GUN = SCGExtra.asResource("geo/fish_folk_gun.geo.json");
	private static final ResourceLocation GEO_SITTING = SCGExtra.asResource("geo/fish_folk_sitting.geo.json");
	private static final ResourceLocation ANIM_SITTING = SCGExtra.asResource("animations/fish_folk_sitting.animation.json");
	private static final ResourceLocation ANIM_TRIDENT = SCGExtra.asResource("animations/fish_folk_trident.animation.json");
	private static final ResourceLocation ANIM_GUN = SCGExtra.asResource("animations/fish_folk_gun.animation.json");

	public FishFolkModel() {
	}

	@Override
	public ResourceLocation getAnimationResource(FishFolkEntity entity) {
		if(entity.isPassenger()){
			return ANIM_SITTING;
		}
		if(entity.getMainHandItem().is(Items.TRIDENT)){
			return ANIM_TRIDENT;
		}else{
			return ANIM_GUN;
		}
	}

	@Override
	public ResourceLocation getModelResource(FishFolkEntity entity) {
		if(entity.isPassenger()){
			return GEO_SITTING;
		}
		if(entity.getMainHandItem().is(Items.TRIDENT)){
			return GEO_TRIDENT;
		}else{
			return GEO_GUN;
		}
	}

	@Override
	public ResourceLocation getTextureResource(FishFolkEntity entity) {
		return entity.getTexture();
	}
}