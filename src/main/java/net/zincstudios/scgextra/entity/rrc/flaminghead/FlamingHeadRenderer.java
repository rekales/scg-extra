package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FlamingHeadRenderer<T extends FlamingHeadEntity> extends GeoEntityRenderer<T> {

    public FlamingHeadRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/flaming_head")));
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }
}
