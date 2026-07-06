package net.zincstudios.scgextra.entity.wreckers.wrecker_green;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WreckerGreenRenderer extends GunnerRenderer<WreckerGreenEntity> {

    public WreckerGreenRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_green")));
        this.noDeathTilt();
    }

    @Override
    public void render(WreckerGreenEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        this.model.getBone("rocket").ifPresent(bone -> bone.setHidden(entity.isRocketHidden()));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
