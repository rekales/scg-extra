package net.zincstudios.scgextra.entity.neutral.end.end_stone_crab;

import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EndStoneCrabModel extends DefaultedEntityGeoModel<EndStoneCrabEntity>{
    public EndStoneCrabModel() {
        super(SCGExtra.asResource("neutral/end_stone_crab"), false);
	}
}
