package net.zincstudios.scgextra.blocks;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.blocks.electrical_wires.ElectricalWiresBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElectricalWiresBlockEntity>> ELECTRICAL_WIRES = BLOCK_ENTITIES.register("electrical_wires", ()->BlockEntityType.Builder.of(ElectricalWiresBlockEntity::new, ModBlocks.ELECTRICAL_WIRES.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
