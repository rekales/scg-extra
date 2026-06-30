package net.zincstudios.scgextra.entity.wreckers.wrecker_turret;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WreckerTurretRenderer extends GunnerRenderer<WreckerTurretEntity> {
    private static final double PILLAR_OFFSET_Z = 12.5D / 16D;

    public WreckerTurretRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_turret")));
        this.noDeathTilt();
    }

    @Override
    protected void applyRotations(WreckerTurretEntity animatable, PoseStack poseStack, float ageInTicks,
                                  float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        poseStack.translate(0.0D, 0.0D, PILLAR_OFFSET_Z);
    }
}
