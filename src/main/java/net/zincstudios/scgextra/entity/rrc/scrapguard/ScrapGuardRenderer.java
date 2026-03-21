package net.zincstudios.scgextra.entity.rrc.scrapguard;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScrapGuardRenderer<T extends ScrapGuardEntity> extends GeoEntityRenderer<T> {

    public ScrapGuardRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/scrap_guard")));
    }
}
