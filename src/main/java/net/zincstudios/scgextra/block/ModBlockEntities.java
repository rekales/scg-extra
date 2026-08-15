package net.zincstudios.scgextra.block;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.block.electrical_wires.ElectricalWiresBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.block.wreckerturret.WreckerTurretBlockEntity;

@SuppressWarnings("DataFlowIssue")  // TODO: figure out why it shouldn't be null and what to use
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElectricalWiresBlockEntity>> ELECTRICAL_WIRES = BLOCK_ENTITIES
            .register("electrical_wires", ()->BlockEntityType.Builder.of(ElectricalWiresBlockEntity::new, ModBlocks.ELECTRICAL_WIRES.get()).build(null));

    public static final RegistryObject<BlockEntityType<WreckerTurretBlockEntity>> WRECKER_TURRET = BLOCK_ENTITIES
            .register("wrecker_turret", () -> BlockEntityType.Builder
                    .of(WreckerTurretBlockEntity::new, ModBlocks.WRECKER_TURRET.get())
                    .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
