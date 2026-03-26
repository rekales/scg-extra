package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArcPsychoEntityRenderer extends GeoEntityRenderer<ArcPsychoEntity> {

    public ArcPsychoEntityRenderer(Context context) {
        super(context, new ArcPsychoModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public ResourceLocation getTextureLocation(ArcPsychoEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/arc_psycho.png");
    }
    @Override
    protected void applyRotations(ArcPsychoEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (animatable != null && animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
    }
}