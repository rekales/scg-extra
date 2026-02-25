package com.daragetsu.scgextra.entity.projectile.net;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class NetEntityRenderer extends ArrowRenderer<NetEntity>{
    ResourceLocation TEXTURE = SCGExtra.asResource("textures/entity/net_entity/net_entity.png");
    private final NetEntityModel model;
    public NetEntityRenderer(Context ctx) {
        super(ctx);
        this.model = new NetEntityModel(ctx.bakeLayer(NetEntityModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(NetEntity p_114482_) {
        return TEXTURE;
    }
    @Override
    public void render(NetEntity entity, float yaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffer, int light) {

        pose.pushPose();

        pose.mulPose(Axis.YP.rotationDegrees(
                Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        pose.mulPose(Axis.ZP.rotationDegrees(
                Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) - 10));

        pose.mulPose(Axis.XP.rotationDegrees(90));

        pose.scale(2F, 2F, 2F);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        pose.popPose();
    }
}
