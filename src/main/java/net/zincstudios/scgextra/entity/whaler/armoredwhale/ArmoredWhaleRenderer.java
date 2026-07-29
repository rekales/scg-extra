package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            public void render(PoseStack poseStack, ArmoredWhaleEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                RenderType emissiveRenderType = getRenderType(animatable);
                getRenderer().reRender(
                    bakedModel,
                    poseStack,
                    bufferSource,
                    animatable,
                    emissiveRenderType,
                    bufferSource.getBuffer(emissiveRenderType),
                    partialTick,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    1,
                    1,
                    1,
                    1
                );
            }
        });
    }
}