package net.zincstudios.scgextra.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.ModEntities;

@SuppressWarnings("unused")
public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister
            .create(ForgeRegistries.ITEMS, SCGExtra.MOD_ID);

    public static final RegistryObject<SpawnEggItem> FISH_FOLK_SPAWN_EGG = ITEMS.register("fish_folk_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.FISH_FOLK,
                    0xB34141,
                    0x72BF9C,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> TURTLEMAN_SPAWN_EGG = ITEMS.register("turtleman_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.TURTLEMAN,
                    0x49C24F,
                    0xD6CFAA,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> SALMONSAUR_SPAWN_EGG = ITEMS.register("salmonsaur_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.SALMONSAUR,
                    0x973637,
                    0x888240,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> GUARDIAN_STATUE_SPAWN_EGG = ITEMS.register("guardian_statue_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.GUARDIAN_STATUE,
                    0xDFDDD7,
                    0x9A9BAF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> TENTACLIATOR_SPAWN_EGG = ITEMS.register("tentacliator_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.TENTACLIATOR,
                    0x3B5C71,
                    0xDBC373,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> GLOWING_TENTACLIATOR_SPAWN_EGG = ITEMS.register("glowing_tentacliator_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.GLOWING_TENTACLIATOR,
                    0x419D9E,
                    0x84CDA6,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> PUFFICUS_SPAWN_EGG = ITEMS.register("pufficus_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PUFFICUS,
                    0xC58A2E,
                    0xF2E9D0,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> ARMORED_WHALE_SPAWN_EGG = ITEMS.register("armored_whale_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.ARMORED_WHALE,
                    0x692831,
                    0x567076,
                    new Item.Properties()
            )
    );

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModItems::buildContents);
    }

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, SCGExtra.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("your_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup."+SCGExtra.MOD_ID+".tab"))
                    .icon(() -> new ItemStack(FISH_FOLK_SPAWN_EGG.get()))
                    .build()
    );

    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CREATIVE_TAB.getKey()) {
            ITEMS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(event::accept);
        }
    }
}
