package net.zincstudios.scgextra.entity.fac.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class FacTankRenderer extends GeoEntityRenderer<FacTankEntity> {
    public FacTankRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_tank")));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FacTankEntity animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay, float red, float green,
                                  float blue, float alpha) {
        if ("cannon".equals(bone.getName()) && animatable.getCannonWarningTicks() > 0) {
            float maxTicks = Math.max(1, animatable.getCannonWarningMaxTicks());
            float ticksLeft = Math.max(animatable.getCannonWarningTicks() - partialTick, 0.0F);
            float progress = Mth.clamp(1.0F - (ticksLeft / maxTicks), 0.0F, 1.0F);
            float intensity = progress * progress;

            // Keep original cannon texture detail and only shift tint from soft orange to bright orange.
            float orangeR = 1.0F;
            float orangeG = Mth.lerp(intensity, 0.90F, 0.45F);
            float orangeB = Mth.lerp(intensity, 0.78F, 0.08F);
            red *= orangeR;
            green *= orangeG;
            blue *= orangeB;
        }

        super.renderRecursively(
                poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha
        );
    }

    @Override
    protected float getDeathMaxRotation(FacTankEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
