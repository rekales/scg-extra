package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArcPsychoEntityRenderer extends GeoEntityRenderer<ArcPsychoEntity> {

    public ArcPsychoEntityRenderer(Context context) {
        super(context, new ArcPsychoModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public ResourceLocation getTextureLocation(ArcPsychoEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/arc_psycho.png");
    }
}