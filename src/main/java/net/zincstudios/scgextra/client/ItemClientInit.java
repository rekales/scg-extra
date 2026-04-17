package net.zincstudios.scgextra.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public final class ItemClientInit {
    private ItemClientInit() {
    }
    
    public static void initializeItem(Consumer<IClientItemExtensions> consumer, String item){
        registerGeoItemRenderer(consumer, item);
    }

    private static void registerGeoItemRenderer(Consumer<IClientItemExtensions> consumer, String modelName) {
        final BlockEntityWithoutLevelRenderer renderer =
                new GeoItemRenderer<>(new DefaultedItemGeoModel<>(SCGExtra.asResource(modelName)));
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
