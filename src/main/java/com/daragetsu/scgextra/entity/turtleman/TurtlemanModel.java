package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class TurtlemanModel<T extends TurtlemanEntity> extends DefaultedEntityGeoModel<T> {

    public TurtlemanModel() {
        super(SCGExtra.asResource("whaler/turtleman"), false);
    }
}