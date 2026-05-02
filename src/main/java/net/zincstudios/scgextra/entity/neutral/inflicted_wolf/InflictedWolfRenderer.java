package net.zincstudios.scgextra.entity.neutral.inflicted_wolf;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class InflictedWolfRenderer extends GeoEntityRenderer<InflictedWolfEntity> {
    public InflictedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/inflicted_wolf"), false));
        this.shadowRadius = 0.6F;
    }

    @Override
    protected float getDeathMaxRotation(InflictedWolfEntity animatable) {
        return 0.0F;
    }
}

