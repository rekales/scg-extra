package net.zincstudios.scgextra.item.armor;

import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BaseArmorSetPartRenderer extends GeoArmorRenderer<GeoArmorSetPartItem> {

    public <I extends GeoArmorSetPartItem> BaseArmorSetPartRenderer(I armorItem) {
        super(armorItem);
    }

    public BaseArmorSetPartRenderer(GeoModel<GeoArmorSetPartItem> model) {
        super(model);
    }

}
