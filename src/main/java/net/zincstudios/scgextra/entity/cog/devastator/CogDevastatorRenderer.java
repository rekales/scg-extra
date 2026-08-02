package net.zincstudios.scgextra.entity.cog.devastator;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

public class CogDevastatorRenderer extends BaseEntityRenderer<CogDevastatorEntity> {

    public CogDevastatorRenderer(EntityRendererProvider.Context context, GeoModel<CogDevastatorEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, Map.of(
                0, "mg_flash",
                1, "sg_flash",
                2, "gg_flash"
        )));
    }
}
