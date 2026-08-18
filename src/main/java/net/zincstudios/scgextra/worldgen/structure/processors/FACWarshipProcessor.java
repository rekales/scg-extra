package net.zincstudios.scgextra.worldgen.structure.processors;

import com.mojang.serialization.Codec;

import net.zincstudios.scgextra.worldgen.structure.ModStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;

public class FACWarshipProcessor extends StructureProcessor{
    public static final Codec<FACWarshipProcessor> CODEC = Codec.unit(FACWarshipProcessor::new);
    public FACWarshipProcessor(){
    }
    @Override
    public StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos,
            StructureBlockInfo blockInfo, StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        return super.processBlock(level, offset, pos, blockInfo, relativeBlockInfo, settings.setKeepLiquids(false));
    }
    @Override
    public StructureBlockInfo process(LevelReader p_74140_, BlockPos p_74141_, BlockPos p_74142_,
            StructureBlockInfo p_74143_, StructureBlockInfo p_74144_, StructurePlaceSettings p_74145_,
            StructureTemplate template) {
        return super.process(p_74140_, p_74141_, p_74142_, p_74143_, p_74144_, p_74145_.setKeepLiquids(false), template);
    }
    @Override
    public StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureEntityInfo rawEntityInfo,
            StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
        return super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings.setKeepLiquids(false), template);
    }
    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.WARSHIP_PROCESSOR.get();
    }
}
