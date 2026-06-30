package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WreckerHelicubeRenderer extends GeoEntityRenderer<WreckerHelicubeEntity> {

    public WreckerHelicubeRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_helicube_cannon")));
        this.shadowRadius = 0.4F;
    }

    @Override
    protected float getDeathMaxRotation(WreckerHelicubeEntity animatable) {
        return 0.0F;
    }
}
