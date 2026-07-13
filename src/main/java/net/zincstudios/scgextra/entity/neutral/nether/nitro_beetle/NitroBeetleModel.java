package net.zincstudios.scgextra.entity.neutral.nether.nitro_beetle;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NitroBeetleModel extends DefaultedEntityGeoModel<NitroBeetleEntity>{
    public NitroBeetleModel() {
        super(SCGExtra.asResource("neutral/nitro_beetle"), false);
	}
}