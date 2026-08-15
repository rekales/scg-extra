package net.zincstudios.scgextra.worldgen.structure;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.worldgen.structure.processors.CogFactoryProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister
            .create(Registries.STRUCTURE_PROCESSOR, SCGExtra.MOD_ID);

    public static final RegistryObject<StructureProcessorType<CogFactoryProcessor>> COG_FACTORY_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("cog_factory_processor", ()->()->CogFactoryProcessor.CODEC);

    public static void register(IEventBus modEventBus) {
        STRUCTURE_PROCESSOR_TYPES.register(modEventBus);
    }
}
