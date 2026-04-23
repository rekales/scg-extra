package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.EquippedEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GunnerRenderer <T extends EquippedEntity & GeoEntity> extends BaseEntityRenderer<T> {

    protected final float gunTilt;

    public GunnerRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model, float gunTilt) {
        super(renderManager, model);
        this.gunTilt = gunTilt;
    }

    public GunnerRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        this(renderManager, model, 0);
    }

    protected void addRenderLayers(EntityRendererProvider.Context context) {
        addRenderLayer(new HeldGunGeoLayer<>(this, this.gunTilt));
    }
}