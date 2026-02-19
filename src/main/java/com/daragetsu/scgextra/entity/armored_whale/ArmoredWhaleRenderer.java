package com.daragetsu.scgextra.entity.armored_whale;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

public class ArmoredWhaleRenderer extends GeoEntityRenderer<ArmoredWhaleEntity>{

    public ArmoredWhaleRenderer(Context renderManager) {
        super(renderManager, new ArmoredWhaleModel());

        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("eye_flash"),
                (bone, entity, partialTick) ->  bone.setHidden(!entity.getEyeFlash())));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_splash"),
                (bone, entity, partialTick) ->  bone.setHidden(!entity.getWaterSplash())));
    }
}