package net.zincstudios.scgextra.entity.rrc.scout;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ScoutModel extends DefaultedEntityGeoModel<ScoutEntity>{

    public ScoutModel() {
        super(SCGExtra.asResource("rrc/scout"), false);
    }
    
}
