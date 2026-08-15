package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

public class CogGigantesRenderer extends BaseEntityRenderer<CogGigantesEntity> {

    public CogGigantesRenderer(EntityRendererProvider.Context context, GeoModel<CogGigantesEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, "barrel_end"));

    }
}
