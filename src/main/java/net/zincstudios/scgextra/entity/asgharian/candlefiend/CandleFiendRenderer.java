package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CandleFiendRenderer <T extends CandleFiendEntity> extends GeoEntityRenderer<T> {

    public CandleFiendRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/candle_fiend")));
        this.shadowRadius = 0;  // no way to get the entity type on construction
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (this.shadowRadius == 0) {
            this.shadowRadius = entity.getBbWidth()/2;
        }
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }
}
