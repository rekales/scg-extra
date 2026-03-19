package net.zincstudios.scgextra.entity.rrc.spring_junkie;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SpringJunkieModel extends DefaultedEntityGeoModel<SpringJunkieEntity>{
     public SpringJunkieModel() {
        super(SCGExtra.asResource("rrc/spring_junkie"), false);
	}
}
