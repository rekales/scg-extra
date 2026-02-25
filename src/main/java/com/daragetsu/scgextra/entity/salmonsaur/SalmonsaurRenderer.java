package com.daragetsu.scgextra.entity.salmonsaur;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SalmonsaurRenderer <T extends SalmonsaurEntity> extends GeoEntityRenderer<T> {

    public SalmonsaurRenderer(Context renderManager) {
        super(renderManager, new SalmonsaurModel<>());
    }
}
