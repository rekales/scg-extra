package net.zincstudios.scgextra.entity.asgharian.worker;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AsgharWorkerRenderer <T extends AsgharWorkerEntity> extends GeoEntityRenderer<T> {

    public AsgharWorkerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/asghar_worker")));
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}