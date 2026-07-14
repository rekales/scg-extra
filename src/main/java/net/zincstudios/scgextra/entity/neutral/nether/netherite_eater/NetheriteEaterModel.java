package net.zincstudios.scgextra.entity.neutral.nether.netherite_eater;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NetheriteEaterModel extends DefaultedEntityGeoModel<NetheriteEaterEntity>{
    public NetheriteEaterModel() {
        super(SCGExtra.asResource("neutral/netherite_eater"), false);
	}
}