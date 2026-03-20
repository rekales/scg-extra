package net.zincstudios.scgextra.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.interfaces.IHeadshotBox;

import javax.annotation.Nullable;

public class EntityHeadBoxDebug {

    private static @Nullable AABB getHeadBB(LivingEntity entity) {
        double expandHeight = entity instanceof Player && !entity.isCrouching() ? (double)0.0625F : (double)0.0F;
        AABB boundingBox = entity.getBoundingBox();
        boundingBox = boundingBox.expandTowards(0.0F, expandHeight, 0.0F);

        IHeadshotBox<LivingEntity> headshotBox = BoundingBoxManager.getHeadshotBoxes(entity.getType());
        if (headshotBox == null) return null;
        AABB box = headshotBox.getHeadshotBox(entity);
        if (box == null) return null;
        return box.move(boundingBox.getCenter().x, boundingBox.minY, boundingBox.getCenter().z);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null && mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                HitResult hit = mc.hitResult;

                if (hit != null && hit.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hit).getEntity() instanceof LivingEntity entity) {
                    AABB headBox = getHeadBB(entity);
                    if (headBox != null) {
                        PoseStack poseStack = event.getPoseStack();
                        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                        Vec3 camera = event.getCamera().getPosition();

                        poseStack.pushPose();
                        poseStack.translate(-camera.x, -camera.y, -camera.z);

                        LevelRenderer.renderLineBox(
                                poseStack,
                                bufferSource.getBuffer(RenderType.lines()),
                                headBox,
                                0.8F, 0F, 1F, 1.0F
                        );

                        bufferSource.endBatch();
                        poseStack.popPose();
                    }
                }
            }
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(EntityHeadBoxDebug::onRenderLevelStage);
    }
}
