package net.zincstudios.scgextra.entity.whaler.armoredwhale;

import net.minecraft.util.Mth;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ArmoredWhaleModel <T extends ArmoredWhaleEntity> extends DefaultedEntityGeoModel<T> {

    public ArmoredWhaleModel(){
        super(SCGExtra.asResource("whaler/armored_whale"), false);
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // NOTE: potentially expensive calls, cache if necessary
        CoreGeoBone leftGunBone = getAnimationProcessor().getBone("left_gun");
        CoreGeoBone rightGunBone = getAnimationProcessor().getBone("right_gun");

        if (leftGunBone != null) {
            leftGunBone.setRotY(-Mth.PI/2-animatable.getLeftGunYRot());
        }
        if (rightGunBone != null) {
            rightGunBone.setRotY(-Mth.PI/2-animatable.getRightGunYRot());
        }
    }
}
