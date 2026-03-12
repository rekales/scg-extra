package net.zincstudios.scgextra.entity.rrc.drone;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DroneEntityRenderer extends GeoEntityRenderer<DroneEntity> {
    
    public DroneEntityRenderer(Context context) {
        super(context, new DroneModel());
    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/drone.png");
    }
}