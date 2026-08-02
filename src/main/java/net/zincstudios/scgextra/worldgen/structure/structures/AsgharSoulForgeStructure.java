package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.zincstudios.scgextra.worldgen.structure.pieces.AsgharSoulForgePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;

public class AsgharSoulForgeStructure extends Structure{
    public static final Codec<AsgharSoulForgeStructure> CODEC = simpleCodec(AsgharSoulForgeStructure::new);
    public AsgharSoulForgeStructure(Structure.StructureSettings config) {
        super(config);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();

        BlockPos pos = new BlockPos(x, -16, z);
        return Optional.of(new GenerationStub(pos,
            builder -> builder.addPiece(
                new AsgharSoulForgePiece(
                    context.structureTemplateManager(),
                    pos
                )
            )
        ));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.ASGHAR_SOUL_FORGE_STRUCTURE.get();
    }

}