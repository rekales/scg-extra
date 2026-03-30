package net.zincstudios.scgextra.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * A simple implementation of IClientItemExtensions that gives a cached renderer
 */
public class SimpleCustomRenderer implements IClientItemExtensions {

    protected BlockEntityWithoutLevelRenderer renderer;

    public SimpleCustomRenderer(BlockEntityWithoutLevelRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return this.renderer;
    }
}