package net.zincstudios.scgextra.entity.neutral.mutant_bat;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MutantBatModel extends DefaultedEntityGeoModel<MutantBatEntity>{
    public MutantBatModel() {
        super(SCGExtra.asResource("neutral/mutant_bat"), false);
	}
}