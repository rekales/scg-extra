package net.zincstudios.scgextra.entity.asgharian.surgeon;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.asgharian.EquippedRenderer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AsgharSurgeonRenderer <T extends AsgharSurgeonEntity> extends EquippedRenderer<T> {

    public AsgharSurgeonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/asghar_surgeon")));
    }

    @Override
    protected void addRenderLayers() {
        // no gun render layer
    }
}
