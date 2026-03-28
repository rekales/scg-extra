package net.zincstudios.scgextra.entity.rrc.copper_knight;

import javax.annotation.Nullable;

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

public class CopperKnightRenderer<T extends CopperKnightEntity> extends GunnerRenderer<T> {
    public CopperKnightRenderer(EntityRendererProvider.Context context){  
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/copper_knight")), true);

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, T animatable) {
                if (bone.getName().equals("left_hand")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
                if (bone.getName().equals("left_hand")) {
                    return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                }
                return ItemDisplayContext.NONE;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (bone.getName().equals("left_hand")) {
                    if(animatable.isAiming()){
                        poseStack.translate(-0.2, -0.3, 0);
                        poseStack.mulPose(Axis.XP.rotationDegrees(135));
                        poseStack.mulPose(Axis.YP.rotationDegrees(170));
                    }else{
                        poseStack.translate(-0.2, -0.2, 0);
                        poseStack.mulPose(Axis.XP.rotationDegrees(145));
                        poseStack.mulPose(Axis.YP.rotationDegrees(130));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}
