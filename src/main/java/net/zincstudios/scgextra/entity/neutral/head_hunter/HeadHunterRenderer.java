package net.zincstudios.scgextra.entity.neutral.head_hunter;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HeadHunterRenderer extends GeoEntityRenderer<HeadHunterEntity> {
    public HeadHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/head_hunter"), false));
        this.shadowRadius = 0.65F;
    }

    @Override
    protected float getDeathMaxRotation(HeadHunterEntity animatable) {
        return 0.0F;
    }
}



