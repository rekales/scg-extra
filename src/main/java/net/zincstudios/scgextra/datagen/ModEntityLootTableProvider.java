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
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.zincstudios.scgextra.entity.ModEntities;
import top.ribs.scguns.init.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ModEntityLootTableProvider extends EntityLootSubProvider {

    protected ModEntityLootTableProvider() {
        super(FeatureFlags.REGISTRY.allFlags(), FeatureFlagSet.of());
    }

    @Override
    public void generate() {
        loot(ModEntities.FISH_FOLK.get())
                .range(ModItems.FROG_DART.get(), 1, 3)
                .range(ModItems.ADVANCED_ROUND.get(), 1, 3)
                .constant(Items.TROPICAL_FISH, 1);

    }

    private LootTableEntryBuilder loot(EntityType<?> type) {
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
                    .setRolls(ConstantValue.exactly(1)).add( LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                    )
            );

            return this;
        }

        public LootTableEntryBuilder range(Item item, int min, int max) {
            this.lootTableRef.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1)).add( LootItem.lootTableItem(item)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                    )
            );
            return this;
        }
    }
}
