package net.zincstudios.scgextra.entity.neutral.netherite_eater;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NetheriteEaterRenderer extends GeoEntityRenderer<NetheriteEaterEntity> {
    public NetheriteEaterRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/netherite_eater"), false));
        this.shadowRadius = 0.95F;
    }
}



