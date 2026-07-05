package net.zincstudios.scgextra.worldgen.biome;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class ModOverworldRegion extends Region {
public ModOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
            VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

            new ParameterUtils.ParameterPointListBuilder()
                    .temperature(
                        ParameterUtils.Temperature.FULL_RANGE
                    )
                    .humidity(
                        ParameterUtils.Humidity.FULL_RANGE
                    )
                    .continentalness(
                        ParameterUtils.Continentalness.NEAR_INLAND,
                        ParameterUtils.Continentalness.MID_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND
                    )
                    .erosion(
                        ParameterUtils.Erosion.EROSION_5,
                        ParameterUtils.Erosion.EROSION_6
                    )
                    .depth(
                        ParameterUtils.Depth.SURFACE, 
                        ParameterUtils.Depth.FLOOR
                    )
                    .weirdness(
                        ParameterUtils.Weirdness.FULL_RANGE 
                    )
                    .build().forEach(
                        point -> builder.add(
                            point, 
                            ModBiomes.WARZONE_BIOME
                        )
                    );

            builder.build().forEach(mapper);
    }
}