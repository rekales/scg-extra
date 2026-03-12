package net.zincstudios.scgextra.entity.whaler.turtleman;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TurtlemanModel<T extends TurtlemanEntity> extends DefaultedEntityGeoModel<T> {

    public TurtlemanModel() {
        super(SCGExtra.asResource("whaler/turtleman"), false);
    }
}