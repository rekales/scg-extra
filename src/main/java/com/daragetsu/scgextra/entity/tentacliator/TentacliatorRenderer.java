package com.daragetsu.scgextra.entity.tentacliator;

import org.joml.Quaternionf;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import top.ribs.scguns.init.ModItems;

public class TentacliatorRenderer extends GeoEntityRenderer<TentacliatorEntity>{

    TentacliatorEntity entity = null;

    public TentacliatorRenderer(Context context) {
        super(context, new TentacliatorModel<TentacliatorEntity>());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(TentacliatorEntity pEntity) {
        entity = pEntity;
        return SCGExtra.asResource("textures/entity/tentacliator/tentacliator.png");
    
    }
    @Override
    public void renderRecursively(PoseStack poseStack, TentacliatorEntity animatable, GeoBone bone,
            RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
            float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
        if (bone.getName().equals("left_arm") && animatable != null) {
            ItemStack itemStack = animatable.getMainHandItem();
            if (!itemStack.isEmpty()) {
                poseStack.pushPose();

                if(itemStack.is(Items.TRIDENT)){
                    poseStack.translate(-1F, 1.7F, 0.0F); // Y = up/down, Z = forward/back
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
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
                }else if(itemStack.is(ModItems.HULLBREAKER.get())){
                    poseStack.translate(-0.45F, 1.5F, -0.7F);
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(300)));
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
                }else{
                    poseStack.translate(-0.45F, 1.5F, -0.7F);
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
