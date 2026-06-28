package net.zincstudios.scgextra.entity.fac.commissar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import net.zincstudios.scgextra.item.ModItems;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class FacCommissarRenderer extends GunnerRenderer<FacCommissarEntity> {

    public FacCommissarRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_commissar")), true);
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected ItemStack getStackForBone(GeoBone bone, FacCommissarEntity animatable) {
                ItemStack stack = animatable.getMainHandItem();
                if (stack.isEmpty()) {
                    return null;
                }

                if (stack.is(ModItems.CAVALRY_SABER.get())) {
                    return "left_hand".equals(bone.getName()) ? stack : null;
                }

                return "right_hand".equals(bone.getName()) ? stack : null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, FacCommissarEntity animatable) {
                if (stack.is(ModItems.CAVALRY_SABER.get())) {
                    return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                }
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, FacCommissarEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();

                // Move saber closer to wrist and slightly outward from the body.
                if (stack.is(ModItems.CAVALRY_SABER.get())) {
                    poseStack.translate(0D, -0.30D, -0.5D);
                } else {
                    // Preserve old gun tilt for firearms.
                    poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                poseStack.popPose();
            }
        });
    }
}
