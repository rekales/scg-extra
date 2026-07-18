package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;

public class TrenchesStructure extends Structure{
    public static final Codec<TrenchesStructure> CODEC = RecordCodecBuilder.<TrenchesStructure>mapCodec((codex) -> codex.group(
            settingsCodec(codex),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("heightmap").forGetter(structure -> structure.heightmap)
    ).apply(codex, TrenchesStructure::new)).codec();

    public final Holder<StructureTemplatePool> startPool;
    public final HeightProvider startHeight;
    public final Optional<Heightmap.Types> heightmap;

    public TrenchesStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> pool, HeightProvider height, Optional<Heightmap.Types> map) {
        super(config);
        this.startPool = pool;
        this.startHeight = height;
        this.heightmap = map;
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
        )-4;

        if(y>72){
            return Optional.empty();
        }

        BlockPos pos = new BlockPos(x, y, z);
        return JigsawPlacement.addPieces(
            context, 
            this.startPool, 
            Optional.empty(), 
            4, 
            pos, 
            false, 
            this.heightmap, 
            64
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.TRENCHES_STRUCTURE.get();
    }

}