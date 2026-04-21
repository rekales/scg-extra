package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CandleFiendRenderer <T extends CandleFiendEntity> extends GeoEntityRenderer<T> {

    public CandleFiendRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/candle_fiend")));
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }
}
