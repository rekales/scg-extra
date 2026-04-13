package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

public class ArmoredWhaleRenderer extends GeoEntityRenderer<ArmoredWhaleEntity>{

    public ArmoredWhaleRenderer(Context renderManager) {
        super(renderManager, new ArmoredWhaleModel<>());

        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("bottom"), (bone, entity, partialTick) -> {
            bone.setHidden(!entity.getWaterSplash());
            bone.setChildrenHidden(!entity.getWaterSplash());
        }));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("side"), (bone, entity, partialTick) ->{
            bone.setHidden(!entity.getWaterSplash());
            bone.setChildrenHidden(!entity.getWaterSplash());
        }));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("top"), (bone, entity, partialTick) ->{
            bone.setHidden(!entity.getWaterSplash());
            bone.setChildrenHidden(!entity.getWaterSplash());
        }));
    }
}