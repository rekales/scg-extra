package net.zincstudios.scgextra.entity.neutral.end.end_scorpion;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndScorpionModel extends DefaultedEntityGeoModel<EndScorpionEntity>{
    public EndScorpionModel() {
        super(SCGExtra.asResource("neutral/end_scorpion"), false);
	}
}
