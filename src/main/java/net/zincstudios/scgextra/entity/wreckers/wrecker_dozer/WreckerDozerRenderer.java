package net.zincstudios.scgextra.entity.wreckers.wrecker_dozer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class WreckerDozerRenderer extends GeoEntityRenderer<WreckerDozerEntity> {

    public WreckerDozerRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_dozer")));
        this.shadowRadius = 2.2F;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            public void render(PoseStack poseStack, WreckerDozerEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
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

    @Override
    protected float getDeathMaxRotation(WreckerDozerEntity animatable) {
        return 0.0F;
    }
}
