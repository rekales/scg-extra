package net.zincstudios.scgextra.entity.neutral.overworld.big_lump;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BigLumpModel extends DefaultedEntityGeoModel<BigLumpEntity>{
    public BigLumpModel() {
        super(SCGExtra.asResource("neutral/big_lump"), false);
	}
}