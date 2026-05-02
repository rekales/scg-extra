package net.zincstudios.scgextra.entity.neutral.inflicted_boar;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.zincstudios.scgextra.SCGExtra;

public class InflictedBoarRenderer extends GeoEntityRenderer<InflictedBoarEntity> {
    public InflictedBoarRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/inflicted_boar"), false));
        this.shadowRadius = 0.8F;
    }

    @Override
    protected float getDeathMaxRotation(InflictedBoarEntity animatable) {
        return 0.0F;
    }
}

