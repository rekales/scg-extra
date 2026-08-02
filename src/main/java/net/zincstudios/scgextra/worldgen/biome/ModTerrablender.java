package net.zincstudios.scgextra.worldgen.biome;

import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;
import terrablender.api.Regions;

public class ModTerrablender {
    public static void registerBiomes() {
        Regions.register(new ModOverworldRegion(ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "overworld"), 10));
    }
}
