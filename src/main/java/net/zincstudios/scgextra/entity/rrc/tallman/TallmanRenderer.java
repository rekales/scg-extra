package net.zincstudios.scgextra.entity.rrc.tallman;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TallmanRenderer <T extends TallmanEntity> extends GeoEntityRenderer<T> {

    public TallmanRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/tallman")));
    }
}