package net.zincstudios.scgextra.entity.projectile.deployedmine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import top.ribs.scguns.init.ModBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DeployedMineRenderer extends EntityRenderer<DeployedMineEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public DeployedMineRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(DeployedMineEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        poseStack.pushPose();

        poseStack.translate(-0.5, 0, -0.5);

        BlockState state = ModBlocks.MINE_UNIT.get().defaultBlockState();
        this.blockRenderer.renderSingleBlock(
                state,
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
    @Override
    public ResourceLocation getTextureLocation(DeployedMineEntity deployedMineEntity) {
        return null;
    }
}