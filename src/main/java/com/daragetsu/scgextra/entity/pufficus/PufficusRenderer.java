package com.daragetsu.scgextra.entity.pufficus;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// TODO: render held item
public class PufficusRenderer<T extends PufficusEntity> extends GeoEntityRenderer<T> {

    public PufficusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PufficusModel<>());
    }
}