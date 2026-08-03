package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class WreckerHelicubeRenderer extends GeoEntityRenderer<WreckerHelicubeEntity> {

    private static final ResourceLocation ARM_GEO =
            SCGExtra.asResource("geo/entity/wreckers/wrecker_helicube_arm.geo.json");
    private static final ResourceLocation ARM_TEXTURE =
            SCGExtra.asResource("textures/entity/wreckers/wrecker_helicube_arm.png");
    private static final ResourceLocation ARM_ANIMATION =
            SCGExtra.asResource("animations/entity/wreckers/wrecker_helicube_arm.animation.json");

    public WreckerHelicubeRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_helicube_cannon")) {
            @Override
            public ResourceLocation getModelResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_GEO : super.getModelResource(animatable);
            }

            @Override
            public ResourceLocation getTextureResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_TEXTURE : super.getTextureResource(animatable);
            }

            @Override
            public ResourceLocation getAnimationResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_ANIMATION : super.getAnimationResource(animatable);
            }
        });
        this.shadowRadius = 0.4F;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            public void render(PoseStack poseStack, WreckerHelicubeEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
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
    protected float getDeathMaxRotation(WreckerHelicubeEntity animatable) {
        return 0.0F;
    }
}
