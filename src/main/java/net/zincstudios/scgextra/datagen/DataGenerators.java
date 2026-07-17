package net.zincstudios.scgextra.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.zincstudios.scgextra.SCGExtra;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(),
                new ModEntityTagProvider(output, lookupProvider, SCGExtra.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeServer(),
                new ModItemModelProvider(output, SCGExtra.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));
        generator.addProvider(event.includeClient(),
                new ModItemTagProvider(output, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), SCGExtra.MOD_ID, existingFileHelper));

        generator.addProvider(event.includeClient(),
                new LootTableProvider(output, Collections.emptySet(), List.of(
                        new LootTableProvider.SubProviderEntry(ModEntityLootTableProvider::new, LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(RaidLootSubProvider::new, LootContextParamSets.EMPTY)
                )));

        generator.addProvider(event.includeServer(), new ModWorldGenProvider(output, lookupProvider));
    }
}