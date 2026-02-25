package net.zincstudios.scgextra.entity.pufficus;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PufficusModel<T extends PufficusEntity> extends DefaultedEntityGeoModel<T> {

    public PufficusModel() {
        super(SCGExtra.asResource("whaler/pufficus"));
    }
}