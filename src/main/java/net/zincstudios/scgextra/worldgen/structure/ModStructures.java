package net.zincstudios.scgextra.worldgen.structure;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.worldgen.structure.structures.TrenchesStructure;
import net.zincstudios.scgextra.worldgen.structure.structures.WarshipStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister
            .create(Registries.STRUCTURE_TYPE, SCGExtra.MOD_ID);

    public static final RegistryObject<StructureType<TrenchesStructure>> TRENCHES_STRUCTURE =
        STRUCTURE_TYPES.register(
            "trenches",
            () -> () -> TrenchesStructure.CODEC
        );

    public static final RegistryObject<StructureType<WarshipStructure>> WARSHIP_STRUCTURE =
        STRUCTURE_TYPES.register(
            "warship_structure",
            () -> () -> WarshipStructure.CODEC
        );

    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
    }
}
