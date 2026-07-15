package net.zincstudios.scgextra.entity.neutral.end.end_dweller;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndDwellerModel extends DefaultedEntityGeoModel<EndDwellerEntity>{
    public EndDwellerModel() {
        super(SCGExtra.asResource("neutral/end_dweller"), false);
	}
}
