package net.zincstudios.scgextra.entity.neutral.big_lump;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BigLumpRenderer extends GeoEntityRenderer<BigLumpEntity> {
    public BigLumpRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/big_lump"), false));
        this.shadowRadius = 0.95F;
    }

    @Override
    protected float getDeathMaxRotation(BigLumpEntity animatable) {
        return 0.0F;
    }
}


