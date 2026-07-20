package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;
import net.zincstudios.scgextra.worldgen.structure.pieces.DestroyedMinesPiece;

public class DestroyedMinesStructure extends Structure{
    public static final Codec<DestroyedMinesStructure> CODEC = simpleCodec(DestroyedMinesStructure::new);

    public DestroyedMinesStructure(Structure.StructureSettings config) {
        super(config);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        int y = context.chunkGenerator().getFirstOccupiedHeight(
            x,
            z,
            Heightmap.Types.WORLD_SURFACE_WG,
            context.heightAccessor(),
            context.randomState()
        );

        BlockPos pos = new BlockPos(x, y, z);
        return Optional.of(new GenerationStub(pos,
            builder -> builder.addPiece(
                new DestroyedMinesPiece(
                    context.structureTemplateManager(),
                    pos
                )
            )
        ));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DESTROYED_MINE_STRUCTURE.get();
    }
}