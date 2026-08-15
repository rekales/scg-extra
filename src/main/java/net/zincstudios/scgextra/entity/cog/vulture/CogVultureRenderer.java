package net.zincstudios.scgextra.entity.cog.vulture;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.BoneGunFlashGeoLayer;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogVultureRenderer extends BaseEntityRenderer<CogVultureEntity> {

    public CogVultureRenderer(EntityRendererProvider.Context context, GeoModel<CogVultureEntity> model) {
        super(context, model);
        this.addRenderLayer(new BoneGunFlashGeoLayer<>(this, Map.of(
                0, "left_flash",
                1, "right_flash"
        )));
    }
}
