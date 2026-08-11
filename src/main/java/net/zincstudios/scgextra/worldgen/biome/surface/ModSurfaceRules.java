package net.zincstudios.scgextra.worldgen.biome.surface;

import net.zincstudios.scgextra.worldgen.biome.ModBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class ModSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);

    private static final SurfaceRules.RuleSource GRAVEL_1 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.15D, 0.15D), GRAVEL);
    private static final SurfaceRules.RuleSource GRAVEL_2 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.BADLANDS_SURFACE, -0.1D, 0.1D), GRAVEL);

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);

        SurfaceRules.RuleSource GRASS_SURFACE = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.WARZONE_BIOME),
                SurfaceRules.ifTrue(
                    SurfaceRules.ON_FLOOR,
                    SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                            SurfaceRules.noiseCondition(Noises.PATCH, -0.1D, 0.1D),
                            MUD
                        ),
                        GRAVEL_2,
                        GRASS_SURFACE
                    )
                )
            )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
