package net.zincstudios.scgextra.blocks;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.blocks.barbed_wires.BarbedWireBlock;
import net.zincstudios.scgextra.blocks.electrical_wires.ElectricalWiresBlock;
import net.zincstudios.scgextra.item.ModItems;
import net.zincstudios.scgextra.worldgen.tree.WarzoneSpruceTreeGrower;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SCGExtra.MOD_ID);

    public static final RegistryObject<Block> BARBED_WIRES = registerBlock("barbed_wires", () -> new BarbedWireBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN).noCollission().strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> ELECTRICAL_WIRES = registerBlock("electrical_wires", () -> new ElectricalWiresBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN).noCollission().strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> WARZONE_SPRUCE_SAPLING = BLOCKS.register("warzone_spruce_sapling",
            () -> new SaplingBlock(new WarzoneSpruceTreeGrower(), BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
