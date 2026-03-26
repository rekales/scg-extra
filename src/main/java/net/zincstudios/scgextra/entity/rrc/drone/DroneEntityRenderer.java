package net.zincstudios.scgextra.entity.rrc.drone;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DroneEntityRenderer extends GeoEntityRenderer<DroneEntity> {
    
    public DroneEntityRenderer(Context context) {
        super(context, new DroneModel());
        this.shadowRadius = 1F;
    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/drone.png");
    }
    @Override
    protected void applyRotations(DroneEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable != null && animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
    }
}