package net.zincstudios.scgextra.entity.cog.centipede;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

public class CogCentipedeRenderer extends BaseEntityRenderer<CogCentipedeEntity> {

    public CogCentipedeRenderer(EntityRendererProvider.Context context, GeoModel<CogCentipedeEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, "barrel_end"));
    }
}
