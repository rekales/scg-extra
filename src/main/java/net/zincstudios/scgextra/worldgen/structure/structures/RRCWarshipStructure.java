package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;
import net.zincstudios.scgextra.worldgen.structure.pieces.RRCWarshipPiece;

public class RRCWarshipStructure extends Structure{
    public static final Codec<RRCWarshipStructure> CODEC = simpleCodec(RRCWarshipStructure::new);
    public RRCWarshipStructure(Structure.StructureSettings config) {
        super(config);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();

        BlockPos pos = new BlockPos(x, 61, z);
        BlockPos frontPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ()+48);
        return Optional.of(new GenerationStub(pos, builder -> {
            builder.addPiece(new RRCWarshipPiece(
                    context.structureTemplateManager(),
                    pos,
                    RRCWarshipPiece.TEMPLATE_BACK
            ));
            builder.addPiece(new RRCWarshipPiece(
                    context.structureTemplateManager(),
                    frontPos,
                    RRCWarshipPiece.TEMPLATE_FRONT
            ));
        }));
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.RRC_WARSHIP_STRUCTURE.get();
    }

}