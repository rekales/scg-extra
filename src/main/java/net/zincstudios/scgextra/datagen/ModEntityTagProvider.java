package net.zincstudios.scgextra.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.zincstudios.scgextra.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import top.ribs.scguns.init.ModTags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
public class ModEntityTagProvider extends EntityTypeTagsProvider {

    public ModEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Entities.WATER)
                .add(ModEntities.FISH_FOLK.get())
                .add(ModEntities.TURTLEMAN.get())
                .add(ModEntities.SALMONSAUR.get())
                .add(ModEntities.GUARDIAN_STATUE.get())
                .add(ModEntities.TENTACLIATOR.get())
                .add(ModEntities.GLOWING_TENTACLIATOR.get())
                .add(ModEntities.PUFFICUS.get())
                .add(ModEntities.ARMORED_WHALE.get());

        tag(ModTags.Entities.HEAVY)
                .add(ModEntities.TURTLEMAN.get())
                .add(ModEntities.SALMONSAUR.get());

        tag(ModTags.Entities.VERY_HEAVY)
                .add(ModEntities.GUARDIAN_STATUE.get())
                .add(ModEntities.ARMORED_WHALE.get());

        tag(ModTags.Entities.WHITE_BLOOD)
                .add(ModEntities.GUARDIAN_STATUE.get());

        tag(ModTags.Entities.CANNOT_BE_LACERATED)
                .add(ModEntities.GUARDIAN_STATUE.get());
    }
}
