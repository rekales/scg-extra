package net.zincstudios.scgextra.entity.neutral.nether.nitro_beetle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.zincstudios.scgextra.SCGExtra;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NitroBeetleRenderer extends GeoEntityRenderer<NitroBeetleEntity> {

    public NitroBeetleRenderer(Context context) {
        super(context, new NitroBeetleModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(NitroBeetleEntity pEntity) {
        return SCGExtra.asResource("textures/entity/neutral/nitro_beetle.png");
    }
    @Override
    protected void applyRotations(NitroBeetleEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable != null && animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
    }
    
}