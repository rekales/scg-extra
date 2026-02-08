package com.daragetsu.scgextra.entity.salmonsaurs;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SalmonsaursRenderer extends MobRenderer<SalmonsaursEntity, SalmonsaursModel<SalmonsaursEntity>> {

    public SalmonsaursRenderer(Context pContext) {
        super(pContext, new SalmonsaursModel<>(pContext.bakeLayer(SalmonsaursModel.LAYER_LOCATION)), 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SalmonsaursEntity pEntity) {
        return SCGExtra.asResource("textures/entity/salmonsaurs/salmonsaurs.png");
    }
    
}
