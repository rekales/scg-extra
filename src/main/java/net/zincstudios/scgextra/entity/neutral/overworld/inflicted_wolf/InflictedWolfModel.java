package net.zincstudios.scgextra.entity.neutral.overworld.inflicted_wolf;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class InflictedWolfModel extends DefaultedEntityGeoModel<InflictedWolfEntity>{
    public InflictedWolfModel() {
        super(SCGExtra.asResource("neutral/inflicted_wolf"), false);
	}
}