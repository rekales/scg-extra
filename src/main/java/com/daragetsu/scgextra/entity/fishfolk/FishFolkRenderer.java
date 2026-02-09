package com.daragetsu.scgextra.entity.fishfolk;

import org.joml.Quaternionf;

import com.daragetsu.scgextra.entity.tentacliator.TentacliatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FishFolkRenderer extends GeoEntityRenderer<FishFolkEntity> {

    public FishFolkRenderer(Context context) {
        super(context, new FishFolkModel<FishFolkEntity>());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(FishFolkEntity entity) {
        return entity.getTexture();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, FishFolkEntity animatable, GeoBone bone, RenderType renderType,
            MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
            int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
        if (bone.getName().equals("left_arm") && animatable != null) {
            ItemStack itemStack = animatable.getMainHandItem();
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();
                if(itemStack.is(Items.TRIDENT)){
                    // Adjust item position: tweak these values based on your arm pivot
                    poseStack.translate(-0.9F, 0F, 0.0F); // Y = up/down, Z = forward/back
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(90)));
                    poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180)));
    
                    // Render the item in left hand
                    Minecraft.getInstance().getItemRenderer().renderStatic(
                            itemStack,
                            ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            bufferSource,
                            animatable.level(),
                            0
                    );
                }else{
                    poseStack.translate(0.3F, 1.5F, -0.7F);
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(360)));
                    poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(360)));
    
                    // Render the item in left hand
                    Minecraft.getInstance().getItemRenderer().renderStatic(
                            itemStack,
                            ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            bufferSource,
                            animatable.level(),
                            0
                    );
                }
                poseStack.popPose();
            }
        }
    }
}