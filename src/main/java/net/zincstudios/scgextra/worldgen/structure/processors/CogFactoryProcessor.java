package net.zincstudios.scgextra.worldgen.structure.processors;

import com.mojang.serialization.Codec;

import net.zincstudios.scgextra.worldgen.structure.ModStructureProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;

public class CogFactoryProcessor extends StructureProcessor{
    public static final Codec<CogFactoryProcessor> CODEC = Codec.unit(CogFactoryProcessor::new);
    public CogFactoryProcessor(){
    }
    @Override
    public StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureEntityInfo rawEntityInfo,
            StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
        if(placementSettings.getRandom(entityInfo.blockPos).nextFloat()<0.9){
            return null;
        }
        return super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);
    }
    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.COG_FACTORY_PROCESSOR.get();
    }
}
