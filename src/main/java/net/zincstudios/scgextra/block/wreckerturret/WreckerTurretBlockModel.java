package net.zincstudios.scgextra.block.wreckerturret;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class WreckerTurretBlockModel<T extends WreckerTurretBlockEntity> extends DefaultedBlockGeoModel<T> {

    public WreckerTurretBlockModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
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
