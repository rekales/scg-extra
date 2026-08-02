package net.zincstudios.scgextra.entity.fac.tank;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import top.ribs.scguns.client.render.entity.ShotballRenderer;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

public class TankCannonProjectileRenderer extends ShotballRenderer {

    public TankCannonProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int light) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        super.render(entity, entityYaw, partialTicks, poseStack, renderTypeBuffer, light);
        poseStack.popPose();
    }
}
