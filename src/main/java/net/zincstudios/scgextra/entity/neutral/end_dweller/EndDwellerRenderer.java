package net.zincstudios.scgextra.entity.neutral.end_dweller;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EndDwellerRenderer extends GeoEntityRenderer<EndDwellerEntity> {
    public EndDwellerRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/end_dweller"), false));
    }
}

