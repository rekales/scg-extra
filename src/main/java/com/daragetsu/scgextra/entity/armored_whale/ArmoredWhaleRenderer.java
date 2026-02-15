package com.daragetsu.scgextra.entity.armored_whale;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArmoredWhaleRenderer extends GeoEntityRenderer<ArmoredWhaleEntity>{
    //gonna leave the renderer as is, change to whatever you need
    public ArmoredWhaleRenderer(Context renderManager) {
        super(renderManager, new ArmoredWhaleModel<ArmoredWhaleEntity>());
    }
}