package net.zincstudios.scgextra.entity.neutral.end_stone_crab;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EndStoneCrabRenderer extends GeoEntityRenderer<EndStoneCrabEntity> {
    public EndStoneCrabRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/end_stone_crab"), false));
        this.shadowRadius = 0.85F;
    }

    @Override
    protected float getDeathMaxRotation(EndStoneCrabEntity animatable) {
        return 0.0F;
    }
}

