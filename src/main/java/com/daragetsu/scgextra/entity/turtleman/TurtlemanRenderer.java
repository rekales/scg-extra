package com.daragetsu.scgextra.entity.turtleman;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// TODO: render held item
public class TurtlemanRenderer<T extends TurtlemanEntity> extends GeoEntityRenderer<T> {

    public TurtlemanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TurtlemanModel<>());
    }
}