package net.zincstudios.scgextra.entity.neutral.overworld.inflicted_wolf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.zincstudios.scgextra.SCGExtra;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class InflictedWolfRenderer extends GeoEntityRenderer<InflictedWolfEntity> {

    public InflictedWolfRenderer(Context context) {
        super(context, new InflictedWolfModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            public void render(PoseStack poseStack, InflictedWolfEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
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
        this.shadowRadius = 0.8F;
    }

    @Override
    public ResourceLocation getTextureLocation(InflictedWolfEntity pEntity) {
        return SCGExtra.asResource("textures/entity/neutral/inflicted_wolf.png");
    }
    @Override
    protected void applyRotations(InflictedWolfEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable != null && animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
    }
    
}
