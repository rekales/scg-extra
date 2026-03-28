package net.zincstudios.scgextra.entity.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GunnerRenderer <T extends GunnerEntity & GeoEntity> extends GeoEntityRenderer<T> {

    protected boolean noDeathTilt = false;
    protected boolean noDeathRedTint = false;
    protected boolean hasCustomShadowRadius = false;

    public GunnerRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        this.shadowRadius = 0;  // no way to get the entity type on construction

        addRenderLayer(new GunGeoLayer<>(this));
    }

    /**
     * Constructor for extending and adding a custom gun render layer
     */
    protected GunnerRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, boolean ignored) {
        super(renderManager, model);
        this.shadowRadius = 0;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!this.hasCustomShadowRadius && this.shadowRadius == 0) {
            this.shadowRadius = entity.getBbWidth()/2;
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        if (this.noDeathTilt) return 0;
        return 90f;
    }


    // Factory methods

    public GunnerRenderer<T> noDeathTilt() {
        this.noDeathTilt = true;

        return this;
    }

    public GunnerRenderer<T> noDeathRedTint() {
        this.noDeathRedTint = true;
        // NOTE: can't be assed to do it since nothing uses it yet.

        return this;
    }

    public GunnerRenderer<T> customShadowRadius(float shadowRadius) {
        this.hasCustomShadowRadius = true;
        this.shadowRadius = shadowRadius;

        return this;
    }
}
