package net.zincstudios.scgextra.entity.rrc.copper_knight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunGeoLayer;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CopperKnightRenderer<T extends CopperKnightEntity> extends GunnerRenderer<T> {
    public CopperKnightRenderer(EntityRendererProvider.Context context){  
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/copper_knight")), true);

        addRenderLayer(new GunGeoLayer<>(this) {
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                if (animatable.isAiming()) {
                    poseStack.translate(-0.2, -0.3, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(135));
                    poseStack.mulPose(Axis.YP.rotationDegrees(170));
                } else {
                    poseStack.translate(-0.2, -0.2, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(145));
                    poseStack.mulPose(Axis.YP.rotationDegrees(130));
                }
                poseStack.mulPose(Axis.XP.rotationDegrees(90)); // Compensate for super
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                poseStack.popPose();
            }
        });
    }
}
