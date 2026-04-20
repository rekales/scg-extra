package net.zincstudios.scgextra.entity.asgharian;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.EquippedEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: refactor then use

@ParametersAreNonnullByDefault
public class EquippedRenderer <T extends EquippedEntity & GeoEntity> extends GeoEntityRenderer<T> {

    protected boolean noDeathTilt = false;
    protected boolean noDeathRedTint = false;  // NOTE: can't be assed to do it since nothing uses it yet.
    protected boolean hasCustomShadowRadius = false;

    private final float weaponTilt;

    public EquippedRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, float weaponTilt) {
        super(renderManager, model);
        this.shadowRadius = 0;  // no way to get the entity type on construction
        this.weaponTilt = weaponTilt;

        addRenderLayers();
    }

    public EquippedRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        this(renderManager, model, 0);
    }

    protected void addRenderLayers() {
        addRenderLayer(new EquipmentGeoLayer<>(this, this.weaponTilt));
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

    public EquippedRenderer<T> noDeathTilt() {
        this.noDeathTilt = true;
        return this;
    }

    public EquippedRenderer<T> noDeathRedTint() {
        this.noDeathRedTint = true;
        return this;
    }

    public EquippedRenderer<T> customShadowRadius(float shadowRadius) {
        this.hasCustomShadowRadius = true;
        this.shadowRadius = shadowRadius;
        return this;
    }
}
