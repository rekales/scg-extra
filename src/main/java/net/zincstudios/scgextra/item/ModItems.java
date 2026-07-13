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
import net.zincstudios.scgextra.entity.cog.COGEntities;
import net.zincstudios.scgextra.entity.asgharian.AsgharianEntities;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.entity.neutral.NeutralEntities;
import net.zincstudios.scgextra.entity.rrc.RRCEntities;
import net.zincstudios.scgextra.entity.whaler.WhalerEntities;
import top.ribs.scguns.item.GunItem;

import java.util.Set;

@SuppressWarnings("unused")
public class ModItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister
            .create(ForgeRegistries.ITEMS, SCGExtra.MOD_ID);

    public static final RegistryObject<GunItem> PLACEHOLDER_GUN = ITEMS.register("placeholder_gun",
            () -> new GunItem(new Item.Properties())
    );

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
            FISH_FOLK_SPAWN_EGG = basicSpawnEgg(WhalerEntities.FISH_FOLK),
            TURTLEMAN_SPAWN_EGG = basicSpawnEgg(WhalerEntities.TURTLEMAN),
            SALMONSAUR_SPAWN_EGG = basicSpawnEgg(WhalerEntities.SALMONSAUR),
            GUARDIAN_STATUE_SPAWN_EGG = basicSpawnEgg(WhalerEntities.GUARDIAN_STATUE),
            TENTACLIATOR_SPAWN_EGG = basicSpawnEgg(WhalerEntities.TENTACLIATOR),
            GLOWING_TENTACLIATOR_SPAWN_EGG = basicSpawnEgg(WhalerEntities.GLOWING_TENTACLIATOR),
            PUFFICUS_SPAWN_EGG = basicSpawnEgg(WhalerEntities.PUFFICUS),
            ARMORED_WHALE_SPAWN_EGG = basicSpawnEgg(WhalerEntities.ARMORED_WHALE),

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
            SOUL_RIPPER_SPAWN_EGG = basicSpawnEgg(AsgharianEntities.SOUL_RIPPER),

            // COG
            COG_VULTURE_SPAWN_EGG = basicSpawnEgg(COGEntities.VULTURE),
            COG_DEVASTATOR_SPAWN_EGG = basicSpawnEgg(COGEntities.DEVASTATOR),
            COG_BOMBARDIER_SPAWN_EGG = basicSpawnEgg(COGEntities.BOMBARDIER),
            COG_GIGANTES_SPAWN_EGG = basicSpawnEgg(COGEntities.GIGANTES),
            COG_VENATOR_SPAWN_EGG = basicSpawnEgg(COGEntities.VENATOR),
            COG_CENTIPEDE_SPAWN_EGG = basicSpawnEgg(COGEntities.CENTIPEDE),
            COG_JUGGERNAUT_SPAWN_EGG = basicSpawnEgg(COGEntities.JUGGERNAUT),
            
            // Neutral
            INFLICTED_BOAR_SPAWN_EGG = basicSpawnEgg(NeutralEntities.INFLICTED_BOAR),
            INFLICTED_WOLF_SPAWN_EGG = basicSpawnEgg(NeutralEntities.INFLICTED_WOLF),
            AMMO_GOBLIN_SPAWN_EGG = basicSpawnEgg(NeutralEntities.AMMO_GOBLIN),
            BIG_LUMP_SPAWN_EGG = basicSpawnEgg(NeutralEntities.BIG_LUMP),
            MUTANT_BAT_SPAWN_EGG = basicSpawnEgg(NeutralEntities.MUTANT_BAT),
            NITRO_BEETLE_SPAWN_EGG = basicSpawnEgg(NeutralEntities.NITRO_BEETLE),
            HEAD_HUNTER_SPAWN_EGG = basicSpawnEgg(NeutralEntities.HEAD_HUNTER)
            ;

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModItems::buildContents);
    }

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, SCGExtra.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("scgextra_tab",//pretty sure this shouldn't be 'your_tab'
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup."+SCGExtra.MOD_ID+".tab"))
                    .icon(() -> new ItemStack(FISH_FOLK_SPAWN_EGG.get()))
                    .build()
    );

    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CREATIVE_TAB.getKey()) {
            Set<RegistryObject<GunItem>> notInTab = Set.of(  // Turn to RegistryObject<Item> later
                    PLACEHOLDER_GUN
            );

            ITEMS.getEntries().stream()
                    .filter(item -> !notInTab.contains(item))
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

