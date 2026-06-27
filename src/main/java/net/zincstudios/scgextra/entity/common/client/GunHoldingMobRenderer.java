package net.zincstudios.scgextra.entity.common.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GunHoldingMobRenderer<T extends Mob & GeoEntity> extends BaseEntityRenderer<T> {

    protected final float gunTilt;

    public GunHoldingMobRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, float gunTilt) {
        super(renderManager, model);
        this.gunTilt = gunTilt;
    }

    public GunHoldingMobRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        this(renderManager, model, 0);
    }

    protected void addRenderLayers(EntityRendererProvider.Context context) {
        addRenderLayer(new HeldGunGeoLayer<>(this, this.gunTilt));
    }
}