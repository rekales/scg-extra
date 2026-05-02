package net.zincstudios.scgextra.entity.neutral.end_scorpion;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EndScorpionRenderer extends GeoEntityRenderer<EndScorpionEntity> {
    public EndScorpionRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/end_scorpion"), false));
        this.shadowRadius = 0.9F;
    }

    @Override
    protected float getDeathMaxRotation(EndScorpionEntity entity) {
        return 0.0F;
    }
}

