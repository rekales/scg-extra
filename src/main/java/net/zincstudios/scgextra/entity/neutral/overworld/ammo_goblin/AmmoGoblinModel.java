package net.zincstudios.scgextra.entity.neutral.overworld.ammo_goblin;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AmmoGoblinModel extends DefaultedEntityGeoModel<AmmoGoblinEntity>{
    public AmmoGoblinModel() {
        super(SCGExtra.asResource("neutral/ammo_goblin"), false);
	}
}