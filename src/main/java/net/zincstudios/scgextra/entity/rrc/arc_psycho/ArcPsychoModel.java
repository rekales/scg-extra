package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class ArcPsychoModel extends DefaultedEntityGeoModel<ArcPsychoEntity>{
    public ArcPsychoModel() {
        super(SCGExtra.asResource("rrc/arc_psycho"), false);
	}
    public void setCustomAnimations(ArcPsychoEntity animatable, long instanceId, AnimationState<ArcPsychoEntity> animationState) {
    };
}