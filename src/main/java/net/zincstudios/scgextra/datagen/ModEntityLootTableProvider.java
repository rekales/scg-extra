package net.zincstudios.scgextra.datagen;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.zincstudios.scgextra.entity.ModEntities;
import top.ribs.scguns.init.ModBlocks;
import top.ribs.scguns.init.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModEntityLootTableProvider extends EntityLootSubProvider {

    protected ModEntityLootTableProvider() {
        super(FeatureFlags.REGISTRY.allFlags(), FeatureFlagSet.of());
    }

    @Override
    public void generate() {

        basicLoot(ModEntities.FISH_FOLK.get())
                .range(ModItems.FROG_DART.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .constant(Items.TROPICAL_FISH, 1);

        basicLoot(ModEntities.SALMONSAUR.get())
                .constant(Items.COD, 1)
                .chance(Items.SADDLE,  0.2f);

        basicLoot(ModEntities.TURTLEMAN.get())
                .range(ModItems.SHOTBALL.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .constant(Items.SCUTE, 1);

        basicLoot(ModEntities.TENTACLIATOR.get())
                .range(ModItems.FROG_DART.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .range(Items.INK_SAC, 1, 3);

        basicLoot(ModEntities.GLOWING_TENTACLIATOR.get())
                .range(ModItems.FROG_DART.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .range(Items.GLOW_INK_SAC, 1, 3);

        basicLoot(ModEntities.PUFFICUS.get())
                .range(ModItems.FROG_DART.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .constant(Items.PUFFERFISH, 1);

        basicLoot(ModEntities.GUARDIAN_STATUE.get())
                .constant(ModItems.OCEAN_FLARE.get(), 1)
                .range(ModItems.BLUEPRINT_SCRAP.get(), 1, 2)
                .range(Items.HEART_OF_THE_SEA, 1, 3)
                .range(Items.PRISMARINE_SHARD, 3, 9);

        basicLoot(ModEntities.ARMORED_WHALE.get())
                .range(ModItems.BLUEPRINT_SCRAP.get(), 1, 2)
                .constant(ModItems.LEVIATHAN_TOOTH.get(), 1)
                .range(ModBlocks.SUPPLY_CRATE.get().asItem(), 3, 6);


        basicLoot(ModEntities.COPPER_KNIGHT.get())
                .range(Items.COPPER_INGOT, 1, 3)
                .altsChance(List.of(
                        ModItems.SCRAP_HELMET.get(),
                        ModItems.SCRAP_CHESTPLATE.get(),
                        ModItems.SCRAP_LEGGINGS.get(),
                        ModItems.SCRAP_BOOTS.get()
                ), 0.1F);

        basicLoot(ModEntities.DRONE.get())
                .range(Items.COPPER_INGOT, 1, 3)
                .range(Items.GUNPOWDER, 3, 5);

        basicLoot(ModEntities.TALLMAN.get())
                .range(Items.COPPER_INGOT, 1, 3);

        basicLoot(ModEntities.SCOUT.get())
                .range(ModItems.SMALL_COPPER_CASING.get(), 1, 3);

        basicLoot(ModEntities.OPPRESSOR.get())
                .range(ModItems.BLUEPRINT_SCRAP.get(), 1, 2)
                .range(Items.COPPER_INGOT, 1, 3);

        basicLoot(ModEntities.SPRING_JUNKIE.get())
                .range(Items.GUNPOWDER, 1, 3)
                .range(ModItems.FLECHETTE.get(), 1, 3);

        basicLoot(ModEntities.FLAMING_HEAD.get())
                .constant(ModItems.RUSTY_MEDAL.get(), 1)
                .range(ModItems.BLAZE_FUEL.get(), 3, 6)
                .range(Items.COPPER_BLOCK, 1, 3);

        basicLoot(ModEntities.SCRAP_GUARD.get())
                .range(Items.COPPER_INGOT, 1, 3)
                .range(ModItems.BLUEPRINT_SCRAP.get(), 1, 2)
                .range(ModItems.NEEDLE.get(), 1, 3);

        basicLoot(ModEntities.ARC_PSYCHO.get())
                .range(Items.REDSTONE, 1, 3)
                .range(ModItems.SHOCK_CELL.get(), 1, 3);

    }

    // don't use this for more complex loot tables
    private LootTableEntryBuilder basicLoot(EntityType<?> type) {
        LootTable.Builder lootTableRef = LootTable.lootTable();
        add(type, lootTableRef);
        return LootTableEntryBuilder.of(lootTableRef);
    }

    // Reviewing the implementation of the LootProvider class, it should be
    // fine to mutate the builder after adding
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    private static class LootTableEntryBuilder {

        private final LootTable.Builder lootTableRef;

        private LootTableEntryBuilder(LootTable.Builder lootTableRef) {
            this.lootTableRef = lootTableRef;
        }

        public static LootTableEntryBuilder of(LootTable.Builder lootTableRef) {
            return new LootTableEntryBuilder(lootTableRef);
        }

        public LootTable.Builder getLootTableRef() {
            return this.lootTableRef;
        }

        public LootTableEntryBuilder constant(Item item, int count) {
            this.lootTableRef.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                    )
            );

            return this;
        }

        public LootTableEntryBuilder range(Item item, int min, int max) {
            this.lootTableRef.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add( LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                    )
            );
            return this;
        }

        public LootTableEntryBuilder chance(Item item, float chance) {
            return this.chanceConstant(item, chance, 1);
        }

        public LootTableEntryBuilder chanceConstant(Item item, float chance, int count) {
            this.lootTableRef.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(chance))
                    .add(LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                    )
            );
            return this;
        }

        public LootTableEntryBuilder altsChance(List<Item> items, float chance) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(chance));

            for (Item item : items) {
                poolBuilder.add(LootItem.lootTableItem(item).setWeight(1));
            }

            this.lootTableRef.withPool(poolBuilder);

            return this;
        }
    }
}
