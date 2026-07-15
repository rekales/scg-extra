package net.zincstudios.scgextra.entity.neutral.end.end_pod;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndPodModel extends DefaultedEntityGeoModel<EndPodEntity>{
    public EndPodModel() {
        super(SCGExtra.asResource("neutral/end_pod"), false);
	}
}
