package net.zincstudios.scgextra.entity.fac.tank;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

public class FacTankRenderer extends BaseEntityRenderer<FacTankEntity> {

    public FacTankRenderer(EntityRendererProvider.Context context, GeoModel<FacTankEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, Map.of(
                0, "left_flash",
                1, "right_flash"
        )));
    }
}
