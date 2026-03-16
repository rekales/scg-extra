package net.zincstudios.scgextra.entity.rrc.oppressor;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OppressorRenderer <T extends OppressorEntity> extends GeoEntityRenderer<T> {

    public OppressorRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/oppressor")));
    }
}
