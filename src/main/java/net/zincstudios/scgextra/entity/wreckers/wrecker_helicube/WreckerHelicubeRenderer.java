package net.zincstudios.scgextra.entity.wreckers.wrecker_helicube;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WreckerHelicubeRenderer extends GeoEntityRenderer<WreckerHelicubeEntity> {

    private static final ResourceLocation ARM_GEO =
            SCGExtra.asResource("geo/entity/wreckers/wrecker_helicube_arm.geo.json");
    private static final ResourceLocation ARM_TEXTURE =
            SCGExtra.asResource("textures/entity/wreckers/wrecker_helicube_arm.png");
    private static final ResourceLocation ARM_ANIMATION =
            SCGExtra.asResource("animations/entity/wreckers/wrecker_helicube_arm.animation.json");

    public WreckerHelicubeRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("wreckers/wrecker_helicube_cannon")) {
            @Override
            public ResourceLocation getModelResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_GEO : super.getModelResource(animatable);
            }

            @Override
            public ResourceLocation getTextureResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_TEXTURE : super.getTextureResource(animatable);
            }

            @Override
            public ResourceLocation getAnimationResource(WreckerHelicubeEntity animatable) {
                return animatable.isArmVariant() ? ARM_ANIMATION : super.getAnimationResource(animatable);
            }
        });
        this.shadowRadius = 0.4F;
    }

    @Override
    protected float getDeathMaxRotation(WreckerHelicubeEntity animatable) {
        return 0.0F;
    }
}
