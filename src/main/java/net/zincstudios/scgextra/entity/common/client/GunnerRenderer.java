package net.zincstudios.scgextra.entity.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GunnerRenderer <T extends GunnerEntity & GeoEntity> extends GeoEntityRenderer<T> {

    protected boolean noDeathTilt = false;
    protected boolean noDeathRedTint = false;

    public GunnerRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);

        addRenderLayer(new GunGeoLayer<>(this));
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);

        if (this.noDeathTilt && animatable != null && animatable.deathTime > 0) {
            float deathRotation = (animatable.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(-Math.min(Mth.sqrt(deathRotation), 1) * getDeathMaxRotation(animatable)));
        }
    }


    // Factory methods

    public GunnerRenderer<T> noDeathTilt() {
        this.noDeathTilt = true;

        return this;
    }

    public GunnerRenderer<T> noDeathRedTint() {
        this.noDeathRedTint = true;
        // NOTE: can't be assed to do it today since nothing uses it yet.

        return this;
    }
}
