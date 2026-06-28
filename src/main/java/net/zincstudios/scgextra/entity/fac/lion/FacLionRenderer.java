package net.zincstudios.scgextra.entity.fac.lion;

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
    private static final double GUN_X = 0.0D;
    private static final double GUN_Y = 0.15D;
    private static final double GUN_Z = -0.25D;
    private static final float GUN_ROT_X = -90.0F;
    private static final float GUN_ROT_Y = 1.0F;

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
                    poseStack.pushPose();
                    poseStack.translate(GUN_X, GUN_Y, GUN_Z);
                    poseStack.mulPose(Axis.XP.rotationDegrees(GUN_ROT_X));
                    poseStack.mulPose(Axis.YP.rotationDegrees(GUN_ROT_Y));
                    super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                    poseStack.popPose();
                    return;
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}
