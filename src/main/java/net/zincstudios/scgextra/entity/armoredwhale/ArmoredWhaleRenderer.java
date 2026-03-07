package net.zincstudios.scgextra.entity.armoredwhale;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

public class ArmoredWhaleRenderer extends GeoEntityRenderer<ArmoredWhaleEntity>{

    public ArmoredWhaleRenderer(Context renderManager) {
        super(renderManager, new ArmoredWhaleModel<>());

        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_splash"), (bone, entity, partialTick) ->  bone.setHidden(!entity.getWaterSplash())));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_layer1"), (bone, entity, partialTick) ->  {bone.setHidden(!(entity.getLayerN()>=1));bone.setChildrenHidden(!(entity.getLayerN()>=1));}));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_layer2"), (bone, entity, partialTick) ->  {bone.setHidden(!(entity.getLayerN()>=2));bone.setChildrenHidden(!(entity.getLayerN()>=2));}));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_layer3"), (bone, entity, partialTick) ->  {bone.setHidden(!(entity.getLayerN()>=3));bone.setChildrenHidden(!(entity.getLayerN()>=3));}));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_layer4"), (bone, entity, partialTick) ->  {bone.setHidden(!(entity.getLayerN()>=4));bone.setChildrenHidden(!(entity.getLayerN()>=4));}));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("water_layer5"), (bone, entity, partialTick) ->  {bone.setHidden(!(entity.getLayerN()>=5));bone.setChildrenHidden(!(entity.getLayerN()>=5));}));
    }
}