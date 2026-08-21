package net.zincstudios.scgextra.item.armor;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.zincstudios.scgextra.item.ModItems;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BaseArmorSetPartRenderer extends GeoArmorRenderer<GeoArmorSetPartItem> {

    public BaseArmorSetPartRenderer(GeoModel<GeoArmorSetPartItem> model) {
        super(model);
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void hideHeadWithCommissarHelmet(RenderPlayerEvent.Pre event) {
        PlayerRenderer renderer = event.getRenderer();
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.COMMISSAR_HELMET.get()) {
            model.head.visible = false;
        }
    }
}
