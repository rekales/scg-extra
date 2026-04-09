package net.zincstudios.scgextra.entity.fac.fac_lion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class FacLionRenderer extends GunnerRenderer<FacLionEntity> {
    public FacLionRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_lion")), true);
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected ItemStack getStackForBone(GeoBone bone, FacLionEntity animatable) {
                if ("right_hand".equals(bone.getName())) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, FacLionEntity animatable) {
                if ("right_hand".equals(bone.getName())) {
                    return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                }
                return ItemDisplayContext.NONE;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, FacLionEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if ("right_hand".equals(bone.getName())) {
                    if (animatable.isAiming()) {
                        poseStack.translate(-0.3, -0.02, -0.25);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-102));
                    } else {
                        poseStack.translate(-0.3, -0.02, -0.25);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-82));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}
