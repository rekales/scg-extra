package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TentacliatorRenderer extends HumanoidMobRenderer<TentacliatorEntity, TentacliatorModel<TentacliatorEntity>>{

    public TentacliatorRenderer(Context pContext) {
        super(pContext, new TentacliatorModel<>(pContext.bakeLayer(TentacliatorModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(TentacliatorEntity pEntity) {
        return SCGExtra.asResource("textures/entity/tentacliator/tentacliator.png");
    }
}
