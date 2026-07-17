package net.zincstudios.scgextra.worldgen.structure;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class TrenchesStructureProcessor extends StructureProcessor{
    public static final Codec<TrenchesStructureProcessor> CODEC = Codec.unit(TrenchesStructureProcessor::new);
    public TrenchesStructureProcessor(){
    }
    @Override
    public StructureBlockInfo process(LevelReader level, BlockPos offset, BlockPos pos,
            StructureBlockInfo blockInfo, StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings,
            StructureTemplate template) {
        return new StructureBlockInfo(
            relativeBlockInfo.pos().below(4), 
            relativeBlockInfo.state(), 
            relativeBlockInfo.nbt()
        );
    }
    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.TRENCHES_PROCESSOR.get();
    }
}