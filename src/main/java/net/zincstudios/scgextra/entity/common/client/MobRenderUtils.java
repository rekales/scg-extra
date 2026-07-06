package net.zincstudios.scgextra.entity.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import top.ribs.scguns.client.GunRenderType;

public final class MobRenderUtils {

    public static void renderMuzzleFlash(PoseStack poseStack, RenderType renderType, MultiBufferSource buffer, ResourceLocation flashTexture,
                                         boolean enchanted, RandomSource rand, float scale, LivingEntity entity) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(360 * rand.nextFloat()));
        poseStack.scale(scale, scale, scale);
        poseStack.translate((rand.nextFloat()-0.5)*0.07, (rand.nextFloat()-0.5)*0.07, 0);

        poseStack.pushPose();
        poseStack.translate(-0.5, -0.5, 0);
        MobRenderUtils.drawMuzzleFlash(poseStack, renderType, buffer, flashTexture, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(360 * rand.nextFloat()));
        poseStack.translate(-0.5, -0.5, 0);
        MobRenderUtils.drawMuzzleFlash(poseStack, renderType, buffer, flashTexture, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(360 * rand.nextFloat()));
        poseStack.translate(-0.5, -0.5, 0);
        MobRenderUtils.drawMuzzleFlash(poseStack, renderType, buffer, flashTexture, false);
        poseStack.popPose();
    }

    public static void drawMuzzleFlash(PoseStack poseStack, RenderType renderType, MultiBufferSource buffer, ResourceLocation flashTexture, boolean enchanted) {
        float minU = enchanted ? 0.5F : 0.0F;
        float maxU = enchanted ? 1.0F : 0.5F;

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer builder = buffer.getBuffer(GunRenderType.getMuzzleFlash(flashTexture));

        builder.vertex(matrix, 0, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, 1.0F).uv2(15728880).endVertex();
        builder.vertex(matrix, 1, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, 1.0F).uv2(15728880).endVertex();
        builder.vertex(matrix, 1, 1, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(minU, 0).uv2(15728880).endVertex();
        builder.vertex(matrix, 0, 1, 0).color(1.0F, 1.0F, 1.0F, 1.0F).uv(maxU, 0).uv2(15728880).endVertex();

        // needed to reset to previous buffer because it leaks to the rest of the model
        buffer.getBuffer(renderType);
    }
}
