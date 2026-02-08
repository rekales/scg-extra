package com.daragetsu.scgextra.entity.tentacliator;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


public class TentacliatorRenderer extends MobRenderer<TentacliatorEntity, TentacliatorModel<TentacliatorEntity>>{

    public TentacliatorRenderer(Context pContext) {
        super(pContext, new TentacliatorModel<>(pContext.bakeLayer(TentacliatorModel.LAYER_LOCATION)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(TentacliatorEntity pEntity) {
        return SCGExtra.asResource("textures/entity/tentacliator/tentacliator.png");
    
    }
    @Override
    public void render(TentacliatorEntity entity, float yaw, float partialTicks,
                    PoseStack poseStack, MultiBufferSource buffer, int light) {

        super.render(entity, yaw, partialTicks, poseStack, buffer, light);

        ItemStack stack = entity.getMainHandItem();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            this.getModel().translateToHand(HumanoidArm.RIGHT, poseStack);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180F));
            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(
                    entity,
                    stack,
                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    false,
                    poseStack,
                    buffer,
                    light
            );

            poseStack.popPose();
        }
    }

}
