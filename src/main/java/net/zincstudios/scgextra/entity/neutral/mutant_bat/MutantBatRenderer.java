package net.zincstudios.scgextra.entity.neutral.mutant_bat;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MutantBatRenderer extends GeoEntityRenderer<MutantBatEntity> {
    public MutantBatRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/mutant_bat"), false));
        this.shadowRadius = 0.7F;
    }

    @Override
    protected float getDeathMaxRotation(MutantBatEntity animatable) {
        return 0.0F;
    }
}



