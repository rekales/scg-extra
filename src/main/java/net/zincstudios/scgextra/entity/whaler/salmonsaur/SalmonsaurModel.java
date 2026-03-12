package net.zincstudios.scgextra.entity.whaler.salmonsaur;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SalmonsaurModel <T extends SalmonsaurEntity> extends DefaultedEntityGeoModel<T> {

    public SalmonsaurModel() {
        super(SCGExtra.asResource("whaler/salmonsaur"), false);
    }
}