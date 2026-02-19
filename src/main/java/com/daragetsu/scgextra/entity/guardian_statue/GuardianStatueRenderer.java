package com.daragetsu.scgextra.entity.guardian_statue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
//@MethodsReturnNonnullByDefault
public class GuardianStatueRenderer<T extends GuardianStatueEntity> extends GeoEntityRenderer<T> {

    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType GUARDIAN_BEAM_RENDER_TYPE;

    public static final ResourceLocation BEACON_BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    private static final RenderType BEACON_BEAM_RENDER_TYPE;

    public GuardianStatueRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GuardianStatueModel<>());
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        renderGuardianBeam(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        renderLaserBeam(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

    }

    private void renderLaserBeam(GuardianStatueEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity target = entity.getActiveAttackTarget();
        if (target == null) return;

        poseStack.pushPose();





//        poseStack.translate(-0.5,entity.getEyeHeight()+0.15,-0.5);
//        Vec3 eyePosOffset = new Vec3(0, 0, 0.5).yRot((-entityYaw + 720)%360 * Mth.DEG_TO_RAD);
//        poseStack.translate(eyePosOffset.x, eyePosOffset.y, eyePosOffset.z);

        poseStack.translate(-0.5,entity.getEyeHeight(),-0.5);

        Vec3 sourceVec = new Vec3(0,entity.getEyeHeight()+0.15,0.5)
                .yRot((-entityYaw + 720)%360 * Mth.DEG_TO_RAD);
        sourceVec = sourceVec.add(entity.position());

        Vec3 targetVec = target.position().add(0, target.getBbHeight()/2, 0);

        Vec3 direction = targetVec.subtract(sourceVec);
        double distance = direction.length();
        direction = direction.normalize();

        float yaw = (float)Math.atan2(direction.x, direction.z);
        float pitch = (float)Math.asin(-direction.y);

//        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.XP.rotation(pitch + (90 * Mth.DEG_TO_RAD)));

//        poseStack.popPose();





//        entity.level().addParticle(
//                ParticleTypes.FLAME,
//                targetVec.x, targetVec.y, targetVec.z,
//                0, 0, 0
//        );

        BeaconRenderer.renderBeaconBeam(
                poseStack,
                buffer,
                BEACON_BEAM_LOCATION,
                partialTicks,
                1.0F, // beam height scale
                entity.level().getGameTime(),
                0,
                (int)Math.ceil(20),
                new float[]{0.85F, 0.2F, 0.2F}, // RGB color
                0.08F, // inner radius
                0.1F // outer radius
        );

        poseStack.popPose();
    }



    private void renderGuardianBeam(GuardianStatueEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // TODO: cleanup
        LivingEntity livingentity = entity.getActiveAttackTarget();
        if (livingentity != null) {
            float f = entity.getAttackAnimationScale(partialTicks);
            float f1 = entity.getClientSideAttackTime() + partialTicks;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = entity.getEyeHeight();
            poseStack.pushPose();
            poseStack.translate(0.0F, f3, 0.0F);
            Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * (double)0.5F, partialTicks);
            Vec3 vec31 = this.getPosition(entity, (double)f3, partialTicks);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float)(vec32.length() + (double)1.0F);
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            poseStack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = buffer.getBuffer(GUARDIAN_BEAM_RENDER_TYPE);
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (entity.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            poseStack.popPose();
        }
    }

    private Vec3 getPosition(LivingEntity livingEntity, double yOffset, float partialTick) {
        double d0 = Mth.lerp(partialTick, livingEntity.xOld, livingEntity.getX());
        double d1 = Mth.lerp(partialTick, livingEntity.yOld, livingEntity.getY()) + yOffset;
        double d2 = Mth.lerp(partialTick, livingEntity.zOld, livingEntity.getZ());
        return new Vec3(d0, d1, d2);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.vertex(pose, x, y, z).color(red, green, blue, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    static {
        GUARDIAN_BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);
        BEACON_BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(BEACON_BEAM_LOCATION);
    }
}