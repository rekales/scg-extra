package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: render held item
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TurtleManRenderer extends MobRenderer<TurtleManEntity, TurtleManModel<TurtleManEntity>> {

    public TurtleManRenderer(EntityRendererProvider.Context context) {
        super(context, new TurtleManModel<>(context.bakeLayer(TurtleManModel.LAYER_LOCATION)), 1);
    }

    @Override
    public void render(TurtleManEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TurtleManEntity entity) {
        return SCGExtra.asResource("textures/entity/turtleman/turtleman.png");
    }
}