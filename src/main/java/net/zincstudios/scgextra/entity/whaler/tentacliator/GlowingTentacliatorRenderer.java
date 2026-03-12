package net.zincstudios.scgextra.entity.whaler.tentacliator;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;

public class GlowingTentacliatorRenderer<T extends TentacliatorEntity> extends TentacliatorRenderer<T> {

    public GlowingTentacliatorRenderer(EntityRendererProvider.Context context) {
        super(context, new TentacliatorModel<T>()
                .withAltTexture(SCGExtra.asResource("whaler/glowing_tentacliator")));
    }
}
