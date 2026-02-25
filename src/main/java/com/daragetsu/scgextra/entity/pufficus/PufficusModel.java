package com.daragetsu.scgextra.entity.pufficus;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class PufficusModel<T extends PufficusEntity> extends DefaultedEntityGeoModel<T> {

    public PufficusModel() {
        super(SCGExtra.asResource("whaler/pufficus"));
    }
}