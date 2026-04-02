package net.zincstudios.scgextra.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
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

    // NOTE: custom tier or nah?
    public static final RegistryObject<AtlanticMaceItem> ATLANTIC_MACE = ITEMS.register("atlantic_mace",
            () -> new AtlanticMaceItem(Tiers.IRON, 7, -3.2F, new Item.Properties())
    );

    public static final RegistryObject<SpawnEggItem> FISH_FOLK_SPAWN_EGG = ITEMS.register("fish_folk_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.FISH_FOLK,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> TURTLEMAN_SPAWN_EGG = ITEMS.register("turtleman_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.TURTLEMAN,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> SALMONSAUR_SPAWN_EGG = ITEMS.register("salmonsaur_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.SALMONSAUR,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> GUARDIAN_STATUE_SPAWN_EGG = ITEMS.register("guardian_statue_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.GUARDIAN_STATUE,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> TENTACLIATOR_SPAWN_EGG = ITEMS.register("tentacliator_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.TENTACLIATOR,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> GLOWING_TENTACLIATOR_SPAWN_EGG = ITEMS.register("glowing_tentacliator_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.GLOWING_TENTACLIATOR,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> PUFFICUS_SPAWN_EGG = ITEMS.register("pufficus_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.PUFFICUS,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> ARMORED_WHALE_SPAWN_EGG = ITEMS.register("armored_whale_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.ARMORED_WHALE,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> COPPER_KNIGHT_SPAWN_EGG = ITEMS.register("copper_knight_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.COPPER_KNIGHT,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> DRONE_SPAWN_EGG = ITEMS.register("drone_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.DRONE,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> TALLMAN_SPAWN_EGG = ITEMS.register("tallman_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.TALLMAN,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> SCOUT_SPAWN_EGG = ITEMS.register("scout_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.SCOUT,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> OPPRESSOR_SPAWN_EGG = ITEMS.register("oppressor_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.OPPRESSOR,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> SPRING_JUNKIE_SPAWN_EGG = ITEMS.register("spring_junkie_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.SPRING_JUNKIE,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> FLAMING_HEAD_SPAWN_EGG = ITEMS.register("flaming_head_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.FLAMING_HEAD,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> SCRAP_GUARD_SPAWN_EGG = ITEMS.register("scrap_guard_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.SCRAP_GUARD,
                    0xFFFFFF,
                    0xFFFFFF,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<SpawnEggItem> ARC_PSYCHO_SPAWN_EGG = ITEMS.register("arc_psycho_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.ARC_PSYCHO,
                    0xFFFFFF,
                    0xFFFFFF,
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

    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CREATIVE_TAB.getKey()) {
            ITEMS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(event::accept);
        }
    }
}
