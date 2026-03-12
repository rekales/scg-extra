package net.zincstudios.scgextra.entity.rrc.drone;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class DroneModel extends DefaultedEntityGeoModel<DroneEntity>{
    public DroneModel() {
        super(SCGExtra.asResource("rrc/drone"), false);
	}
    public void setCustomAnimations(DroneEntity animatable, long instanceId, AnimationState<DroneEntity> animationState) {
    };
}