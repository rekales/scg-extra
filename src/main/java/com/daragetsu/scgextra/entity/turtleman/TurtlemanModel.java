package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TurtlemanModel<T extends TurtlemanEntity> extends DefaultedEntityGeoModel<T> {

    public TurtlemanModel() {
        super(SCGExtra.asResource("whaler/turtleman"), false);
    }
}