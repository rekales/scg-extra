package net.zincstudios.scgextra.entity.cog.vulture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.GunFlashHandler;
import net.zincstudios.scgextra.entity.common.client.MobRenderUtils;
import software.bernie.geckolib.model.GeoModel;
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogVultureRenderer extends BaseEntityRenderer<CogVultureEntity> {

    public CogVultureRenderer(EntityRendererProvider.Context context, GeoModel<CogVultureEntity> model) {
        super(context, model);
    }

    @Override
    public void render(CogVultureEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        renderMuzzleFlash(poseStack, entity, entityYaw, getRenderType(entity, getTextureLocation(entity), bufferSource, partialTick), bufferSource);
    }

    // TODO: turn to bone referenced transforms
    private void renderMuzzleFlash(PoseStack poseStack, CogVultureEntity entity, float entityYaw ,RenderType renderType, MultiBufferSource bufferSource) {
        Set<Integer> flashes = GunFlashHandler.getFlashesToRender(entity.getId());
        if (flashes.isEmpty()) return;

        Gun.Display.Flash flash = ModItems.VALORA.get().getGun().getDisplay().getFlash();
        if (flash == null) return;
        ResourceLocation flashTexture = ResourceLocation.fromNamespaceAndPath(ScorchedGuns.MODID,
                "textures/effect/" + flash.getTextureLocation() + ".png");

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        for (int index : flashes) {
            poseStack.pushPose();
            if (index == 0) {
                poseStack.translate(-0.45,1.2,0.13);
            } else if (index == 1) {
                poseStack.translate(0.45,1.2,0.13);
            }
            RandomSource rand = RandomSource.create(entity.level().getGameTime()/2 * entity.getId());
            rand.nextFloat();  // because it's the same value on first get
            MobRenderUtils.renderMuzzleFlash(poseStack, renderType, bufferSource, flashTexture, false, rand, 0.5f, entity);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
