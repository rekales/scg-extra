package net.zincstudios.scgextra.client.model;

import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.block.WreckerTurretBlockEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class WreckerTurretWeaponModel extends GeoModel<WreckerTurretBlockEntity> {
    private static final ResourceLocation MODEL = SCGExtra.asResource("geo/entity/wreckers/wrecker_turret_weapon.geo.json");
    private static final ResourceLocation TEXTURE = SCGExtra.asResource("textures/block/wrecker_turret_weapon.png");
    private static final ResourceLocation ANIMATION = SCGExtra.asResource("animations/entity/wreckers/wrecker_turret_weapon.animation.json");

    @Override
    public ResourceLocation getModelResource(WreckerTurretBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WreckerTurretBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WreckerTurretBlockEntity animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(WreckerTurretBlockEntity animatable, long instanceId, AnimationState<WreckerTurretBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone top = this.getAnimationProcessor().getBone("turret_top");
        if (top == null) {
            return;
        }
        float[] aim = animatable.clientTurretAim();
        if (aim == null) {
            top.setRotY(0.0F);
            top.setRotX(0.0F);
            return;
        }
        top.setRotY(aim[0]);
        top.setRotX(aim[1]);
    }
}
