package net.zincstudios.scgextra.entity.rrc.spring_junkie;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpringJunkieRenderer extends GeoEntityRenderer<SpringJunkieEntity>{
    public SpringJunkieRenderer(Context context) {
        super(context, new SpringJunkieModel());
        this.shadowRadius = 1F;
    }
    @Override
    public ResourceLocation getTextureLocation(SpringJunkieEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/spring_junkie.png");
    }
}