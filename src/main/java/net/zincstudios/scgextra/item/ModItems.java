package net.zincstudios.scgextra.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.asgharian.AsgharianEntities;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.entity.rrc.RRCEntities;

@SuppressWarnings("unused")
public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister
            .create(ForgeRegistries.ITEMS, SCGExtra.MOD_ID);

    // NOTE: custom tier or nah?
    public static final RegistryObject<AtlanticMaceItem> ATLANTIC_MACE = ITEMS.register("atlantic_mace",
            () -> new AtlanticMaceItem(Tiers.IRON, 7, -3.2F, new Item.Properties())
    );

    public static final RegistryObject<CavalrySaberItem> CAVALRY_SABER = ITEMS.register("cavalry_saber",
            () -> new CavalrySaberItem(Tiers.IRON, 3, -2.2F, new Item.Properties())
    );

    public static final RegistryObject<SpearShovelItem> SPEAR_SHOVEL = ITEMS.register("spear_shovel",
            () -> new SpearShovelItem(Tiers.IRON, 2.5F, -2.8F, new Item.Properties())
    );

    public static final RegistryObject<Item> WALKER_MG = ITEMS.register("walker_mg",
            () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<SpawnEggItem>
            // Whaler
            FISH_FOLK_SPAWN_EGG = basicSpawnEgg(ModEntities.FISH_FOLK),
            TURTLEMAN_SPAWN_EGG = basicSpawnEgg(ModEntities.TURTLEMAN),
            SALMONSAUR_SPAWN_EGG = basicSpawnEgg(ModEntities.SALMONSAUR),
            GUARDIAN_STATUE_SPAWN_EGG = basicSpawnEgg(ModEntities.GUARDIAN_STATUE),
            TENTACLIATOR_SPAWN_EGG = basicSpawnEgg(ModEntities.TENTACLIATOR),
            GLOWING_TENTACLIATOR_SPAWN_EGG = basicSpawnEgg(ModEntities.GLOWING_TENTACLIATOR),
            PUFFICUS_SPAWN_EGG = basicSpawnEgg(ModEntities.PUFFICUS),
            ARMORED_WHALE_SPAWN_EGG = basicSpawnEgg(ModEntities.ARMORED_WHALE),

            // RRC
            COPPER_KNIGHT_SPAWN_EGG = basicSpawnEgg(RRCEntities.COPPER_KNIGHT),
            DRONE_SPAWN_EGG = basicSpawnEgg(RRCEntities.DRONE),
            TALLMAN_SPAWN_EGG = basicSpawnEgg(RRCEntities.TALLMAN),
            SCOUT_SPAWN_EGG = basicSpawnEgg(RRCEntities.SCOUT),
            OPPRESSOR_SPAWN_EGG = basicSpawnEgg(RRCEntities.OPPRESSOR),
            SPRING_JUNKIE_SPAWN_EGG = basicSpawnEgg(RRCEntities.SPRING_JUNKIE),
            FLAMING_HEAD_SPAWN_EGG = basicSpawnEgg(RRCEntities.FLAMING_HEAD),
            SCRAP_GUARD_SPAWN_EGG = basicSpawnEgg(RRCEntities.SCRAP_GUARD),
            ARC_PSYCHO_SPAWN_EGG = basicSpawnEgg(RRCEntities.ARC_PSYCHO),

            // FAC
            FAC_TRENCHER_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_TRENCHER),
            FAC_BLUECOAT_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_BLUECOAT),
            TRENCH_GOBLIN_SPAWN_EGG = basicSpawnEgg(FACEntities.TRENCH_GOBLIN),
            TRENCH_SNIPER_SPAWN_EGG = basicSpawnEgg(FACEntities.TRENCH_SNIPER),
            SHOVEL_KNIGHT_SPAWN_EGG = basicSpawnEgg(FACEntities.SHOVEL_KNIGHT),
            FAC_TANK_BUSTER_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_TANK_BUSTER),
            FAC_LION_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_LION),
            FAC_COMMISSAR_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_COMMISSAR),
            FAC_WALKER_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_WALKER),
            FAC_TANK_SPAWN_EGG = basicSpawnEgg(FACEntities.FAC_TANK),

            // Asgharian
            FAILED_ONE_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.FAILED_ONE),
            ASGHAR_SURGEON_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.ASGHAR_SURGEON),
            ASGHAR_WORKER_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.ASGHAR_WORKER),
            ASGHAR_FLAMER_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.ASGHAR_FLAMER),
            CANDLE_FIEND_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.CANDLE_FIEND),
            SOUL_RIPPER_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.SOUL_RIPPER);


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

    private static RegistryObject<SpawnEggItem> basicSpawnEgg(RegistryObject<? extends EntityType<? extends Mob>> type) {
        assert type.getId() != null;
        return ITEMS.register(type.getId().getPath() + "_spawn_egg",
                () -> new ForgeSpawnEggItem(
                        type,
                        0xFFFFFF,
                        0xFFFFFF,
                        new Item.Properties()
                ));
    }
}
