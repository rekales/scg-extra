package net.zincstudios.scgextra.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.monster.Monster;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

// NOTE: Only use on development, never for release.
public class PlaceholderEntityRenderer<T extends Monster & GeoEntity> extends GeoEntityRenderer<T> {

    public PlaceholderEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder")));
    }
}
