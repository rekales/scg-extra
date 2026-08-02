package net.zincstudios.scgextra.entity.neutral.nether.head_hunter;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HeadHunterModel extends DefaultedEntityGeoModel<HeadHunterEntity>{
    public HeadHunterModel() {
        super(SCGExtra.asResource("neutral/head_hunter"), false);
	}
}