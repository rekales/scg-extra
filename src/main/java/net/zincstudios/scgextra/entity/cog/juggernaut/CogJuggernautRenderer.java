package net.zincstudios.scgextra.entity.cog.juggernaut;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.entity.asgharian.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.asgharian.HeldGunGeoLayer;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CogJuggernautRenderer extends BaseEntityRenderer<CogJuggernautEntity> {

    public CogJuggernautRenderer(EntityRendererProvider.Context context, GeoModel<CogJuggernautEntity> model) {
        super(context, model);
    }

    @Override
    protected void addRenderLayers(EntityRendererProvider.Context context) {
        addRenderLayer(new HeldGunGeoLayer<>(this, -60));
    }
}
