package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;
import net.zincstudios.scgextra.worldgen.structure.pieces.WarshipPiece;

public class WarshipStructure extends Structure{
    public static final Codec<WarshipStructure> CODEC = simpleCodec(WarshipStructure::new);
    public WarshipStructure(Structure.StructureSettings config) {
        super(config);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();

        BlockPos pos = new BlockPos(x, 61, z);
        return Optional.of(new GenerationStub(pos,
            builder -> builder.addPiece(
                new WarshipPiece(
                    context.structureTemplateManager(),
                    pos
                )
            )
        ));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.WARSHIP_STRUCTURE.get();
    }

}