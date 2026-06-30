package net.zincstudios.scgextra.entity.wreckers.wrecker_dozer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WreckerDozerRenderer extends GeoEntityRenderer<WreckerDozerEntity> {

    public WreckerDozerRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_dozer")));
        this.shadowRadius = 2.2F;
    }

    @Override
    protected float getDeathMaxRotation(WreckerDozerEntity animatable) {
        return 0.0F;
    }
}
