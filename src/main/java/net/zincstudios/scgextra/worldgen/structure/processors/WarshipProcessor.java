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

public class WarshipProcessor extends StructureProcessor{
    public static final Codec<WarshipProcessor> CODEC = Codec.unit(WarshipProcessor::new);
    public WarshipProcessor(){
    }
    @Override
    public StructureBlockInfo process(LevelReader level, BlockPos pos, BlockPos pos2, StructureBlockInfo sbi, StructureBlockInfo sbi2, StructurePlaceSettings settings, StructureTemplate template) {
        return super.process(level, pos, pos2, sbi, sbi2, settings.setKeepLiquids(false), template);
    }
    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.WARSHIP_PROCESSOR.get();
    }
}
