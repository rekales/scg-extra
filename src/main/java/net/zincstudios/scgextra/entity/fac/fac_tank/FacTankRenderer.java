package net.zincstudios.scgextra.entity.fac.fac_tank;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FacTankRenderer extends GeoEntityRenderer<FacTankEntity> {

    public FacTankRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_tank")));
    }

    @Override
    protected float getDeathMaxRotation(FacTankEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
