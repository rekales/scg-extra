package com.daragetsu.scgextra.entity.guardian_statue;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuardianStatueRenderer extends MobRenderer<GuardianStatueEntity, GuardianStatueModel<GuardianStatueEntity>> {

    public GuardianStatueRenderer(EntityRendererProvider.Context context) {
        super(context, new GuardianStatueModel<>(context.bakeLayer(GuardianStatueModel.LAYER_LOCATION)), 1);
    }

    @Override
    public void render(GuardianStatueEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GuardianStatueEntity entity) {
        return SCGExtra.asResource("textures/entity/guardian_statue/guardian_statue.png");
    }
}