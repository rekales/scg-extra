package com.daragetsu.scgextra.entity.fishfolk;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FishFolkRenderer extends HumanoidMobRenderer<FishFolkEntity, FishFolkModel<FishFolkEntity>> {
    public FishFolkRenderer(Context pContext) {
        super(pContext, new FishFolkModel<>(pContext.bakeLayer(FishFolkModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(FishFolkEntity entity) {
        return entity.getTexture();
    }
}