package net.zincstudios.scgextra.client.model;

import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.item.custom.WreckerTurretItem;
import software.bernie.geckolib.model.GeoModel;

public class WreckerTurretItemModel extends GeoModel<WreckerTurretItem> {
    private static final ResourceLocation MODEL = SCGExtra.asResource("geo/entity/wreckers/wrecker_turret_weapon.geo.json");
    private static final ResourceLocation TEXTURE = SCGExtra.asResource("textures/block/wrecker_turret_weapon.png");
    private static final ResourceLocation ANIMATION = SCGExtra.asResource("animations/entity/wreckers/wrecker_turret_weapon.animation.json");

    @Override
    public ResourceLocation getModelResource(WreckerTurretItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WreckerTurretItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WreckerTurretItem animatable) {
        return ANIMATION;
    }
}
