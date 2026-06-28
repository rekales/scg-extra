package net.zincstudios.scgextra.entity.fac.shovel_knight;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.HeldItemGeoLayer;
import net.zincstudios.scgextra.item.SpearShovelItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class FacShovelKnightRenderer extends BaseEntityRenderer<FacShovelKnightEntity> {

    public FacShovelKnightRenderer(EntityRendererProvider.Context context, GeoModel<FacShovelKnightEntity> model) {
        super(context, model);

        this.addRenderLayer(new HeldItemGeoLayer<>(this) {

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, FacShovelKnightEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                if (stack.getItem() instanceof SpearShovelItem) {
                    poseStack.translate(0, 0.25F, -0.3125F);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(animatable, stack,
                        getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, animatable.level(),
                        packedLight, packedOverlay, animatable.getId());
                poseStack.popPose();
            }
        });
    }
}
