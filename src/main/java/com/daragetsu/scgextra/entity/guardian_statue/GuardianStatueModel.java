package com.daragetsu.scgextra.entity.guardian_statue;

import com.daragetsu.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GuardianStatueModel<T extends GuardianStatueEntity> extends DefaultedEntityGeoModel<T> {

    public GuardianStatueModel() {
        super(SCGExtra.asResource("whaler/guardian_statue"), false);
    }
}