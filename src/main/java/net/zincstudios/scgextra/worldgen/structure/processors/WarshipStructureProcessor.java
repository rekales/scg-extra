package net.zincstudios.scgextra.worldgen.structure.processors;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.zincstudios.scgextra.worldgen.structure.ModStructureProcessors;

public class WarshipStructureProcessor extends StructureProcessor{
    public static final Codec<WarshipStructureProcessor> CODEC = Codec.unit(WarshipStructureProcessor::new);
    public WarshipStructureProcessor(){
    }
    @Override
    public StructureBlockInfo process(LevelReader level, BlockPos offset, BlockPos pos, StructureBlockInfo blockInfo, StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings, StructureTemplate template) {
        BlockState state = relativeBlockInfo.state();
        if(state.is(Blocks.SPRUCE_FENCE_GATE)){
            BlockState SBs = Blocks.SPRUCE_STAIRS.defaultBlockState();
            if(state.hasProperty(BlockStateProperties.FACING)){
                SBs.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
            }
            if(state.hasProperty(BlockStateProperties.OPEN)){
                SBs.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING).getOpposite());
                SBs.setValue(BlockStateProperties.HALF, state.getValue(BlockStateProperties.OPEN)==true ? Half.TOP : Half.BOTTOM);
            }
            return new StructureBlockInfo(
                relativeBlockInfo.pos(), 
                SBs,
                relativeBlockInfo.nbt()
            );
        }
        return new StructureBlockInfo(
            relativeBlockInfo.pos(), 
            relativeBlockInfo.state(),
            relativeBlockInfo.nbt()
        );
    }
    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.WARSHIP_PROCESSOR.get();
    }
}