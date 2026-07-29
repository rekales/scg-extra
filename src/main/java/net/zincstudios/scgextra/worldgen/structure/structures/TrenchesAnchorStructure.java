package net.zincstudios.scgextra.worldgen.structure.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.zincstudios.scgextra.worldgen.structure.ModStructures;

public class TrenchesAnchorStructure extends Structure{
public static final Codec<TrenchesAnchorStructure> CODEC = RecordCodecBuilder.<TrenchesAnchorStructure>mapCodec((codex) -> codex.group(
            settingsCodec(codex),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.heightmap)
    ).apply(codex, TrenchesAnchorStructure::new)).codec();

    public final Holder<StructureTemplatePool> startPool;
    public final HeightProvider startHeight;
    public final Optional<Heightmap.Types> heightmap;

    public TrenchesAnchorStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> pool, HeightProvider height, Optional<Heightmap.Types> map) {
        super(config);
        this.startPool = pool;
        this.startHeight = height;
        this.heightmap = map;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkpos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMiddleBlockX(), i, chunkpos.getMiddleBlockZ());
        if(context.chunkGenerator().getBaseColumn(context.chunkPos().getMiddleBlockX(), context.chunkPos().getMiddleBlockZ(), context.heightAccessor(), context.randomState()).getBlock(blockpos.getY()).is(Blocks.WATER)){
            return Optional.empty();
        }
        if(blockpos.getY()>70){
            return Optional.empty();
        }
        return JigsawPlacement.addPieces(
            context, 
            this.startPool, 
            Optional.empty(), 
            6, 
            blockpos, 
            false, 
            this.heightmap, 
            64
        );
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.TRENCHES_ANCHOR_STRUCTURE.get();
    }
    
}
