package net.zincstudios.scgextra.entity.fac.commissar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.HeldGunFlashGeoLayer;
import net.zincstudios.scgextra.entity.common.client.HeldItemGeoLayer;
import net.zincstudios.scgextra.item.CavalrySaberItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class FacCommissarRenderer extends BaseEntityRenderer<FacCommissarEntity> {

    public FacCommissarRenderer(EntityRendererProvider.Context context, GeoModel<FacCommissarEntity> model) {
        super(context, model);
        this.addRenderLayer(new HeldItemGeoLayer<>(this) {
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, FacCommissarEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                if (stack.getItem() instanceof CavalrySaberItem) {
                    poseStack.translate(0, -0.25F, -0.2);
                    poseStack.mulPose(Axis.XP.rotationDegrees(180));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90F));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(animatable, stack,
                        getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, animatable.level(),
                        packedLight, packedOverlay, animatable.getId());
                poseStack.popPose();
            }
        });
        this.addRenderLayer(new HeldGunFlashGeoLayer<>(this, 0));
    }
}
