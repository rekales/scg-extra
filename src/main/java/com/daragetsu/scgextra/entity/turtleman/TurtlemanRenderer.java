package com.daragetsu.scgextra.entity.turtleman;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

// TODO: render held item
public class TurtlemanRenderer<T extends TurtlemanEntity> extends GeoEntityRenderer<T> {

    public TurtlemanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TurtlemanModel<>());

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected @Nullable ItemStack getStackForBone(GeoBone bone, T animatable) {
                if (bone.getName().equals("right_hand")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
                if (bone.getName().equals("right_hand")) {
                    return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                }
                return ItemDisplayContext.NONE;            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (bone.getName().equals("right_hand")) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-100));
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);            }
        });
    }
}