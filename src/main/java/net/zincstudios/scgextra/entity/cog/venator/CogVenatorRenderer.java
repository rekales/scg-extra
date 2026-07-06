package net.zincstudios.scgextra.entity.cog.venator;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

public class CogVenatorRenderer extends BaseEntityRenderer<CogVenatorEntity> {

    public CogVenatorRenderer(EntityRendererProvider.Context context, GeoModel<CogVenatorEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, "barrel_end"));
    }
}
