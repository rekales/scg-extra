package net.zincstudios.scgextra.entity.whaler.pufficus;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunGeoLayer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PufficusRenderer<T extends PufficusEntity> extends GeoEntityRenderer<T> {

    public PufficusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("whaler/pufficus")));

        // Not actually gunner but I'll use it anyway
        addRenderLayer(new GunGeoLayer<>(this, -25));
    }
}