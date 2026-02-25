package net.zincstudios.scgextra.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.zincstudios.scgextra.SCGExtra;

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
        generator.addProvider(event.includeClient(),
                new ModItemTagProvider(output, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), SCGExtra.MOD_ID, existingFileHelper));
    }
}