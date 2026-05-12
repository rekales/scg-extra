package net.zincstudios.scgextra.entity.neutral.nitro_beetle;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NitroBeetleRenderer extends GeoEntityRenderer<NitroBeetleEntity> {
    public NitroBeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/nitro_beetle"), false));
        this.shadowRadius = 0.4F;
    }
}



