package net.zincstudios.scgextra.worldgen.tree;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.zincstudios.scgextra.worldgen.ModConfiguredFeatures;

public class WarzoneSpruceTreeGrower extends AbstractTreeGrower{

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource arg0, boolean arg1) {
        return ModConfiguredFeatures.WARZONE_SPRUCE_KEY;
    }
    
}
