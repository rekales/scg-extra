package net.zincstudios.scgextra.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.item.ModItems;


public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        spawnEgg(ModItems.FISH_FOLK_SPAWN_EGG);
        spawnEgg(ModItems.TURTLEMAN_SPAWN_EGG);
        spawnEgg(ModItems.SALMONSAUR_SPAWN_EGG);
        spawnEgg(ModItems.GUARDIAN_STATUE_SPAWN_EGG);
        spawnEgg(ModItems.TENTACLIATOR_SPAWN_EGG);
        spawnEgg(ModItems.GLOWING_TENTACLIATOR_SPAWN_EGG);
        spawnEgg(ModItems.PUFFICUS_SPAWN_EGG);
        spawnEgg(ModItems.ARMORED_WHALE_SPAWN_EGG);
    }

    private void spawnEgg(RegistryObject<SpawnEggItem> item) {
        if (item.getId() == null) return;
        withExistingParent(item.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}