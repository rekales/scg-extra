package net.zincstudios.scgextra.entity.neutral.ammo_goblin;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AmmoGoblinRenderer extends GeoEntityRenderer<AmmoGoblinEntity> {
    public AmmoGoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/ammo_goblin"), false));
        this.shadowRadius = 0.5F;
    }
}



