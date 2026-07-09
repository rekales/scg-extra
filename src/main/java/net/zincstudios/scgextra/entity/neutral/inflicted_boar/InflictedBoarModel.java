package net.zincstudios.scgextra.entity.neutral.inflicted_boar;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class InflictedBoarModel extends DefaultedEntityGeoModel<InflictedBoarEntity>{
    public InflictedBoarModel() {
        super(SCGExtra.asResource("neutral/inflicted_boar"), false);
	}
}