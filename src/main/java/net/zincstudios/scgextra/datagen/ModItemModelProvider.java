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

        basicItem(ModItems.FISH_FOLK_SPAWN_EGG.get());
        basicItem(ModItems.TURTLEMAN_SPAWN_EGG.get());
        basicItem(ModItems.SALMONSAUR_SPAWN_EGG.get());
        basicItem(ModItems.GUARDIAN_STATUE_SPAWN_EGG.get());
        basicItem(ModItems.TENTACLIATOR_SPAWN_EGG.get());
        basicItem(ModItems.GLOWING_TENTACLIATOR_SPAWN_EGG.get());
        basicItem(ModItems.PUFFICUS_SPAWN_EGG.get());
        basicItem(ModItems.ARMORED_WHALE_SPAWN_EGG.get());

        basicItem(ModItems.FAC_TRENCHER_SPAWN_EGG.get());
        basicItem(ModItems.FAC_BLUECOAT_SPAWN_EGG.get());
        basicItem(ModItems.TRENCH_GOBLIN_SPAWN_EGG.get());
        basicItem(ModItems.TRENCH_SNIPER_SPAWN_EGG.get());
        basicItem(ModItems.SHOVEL_KNIGHT_SPAWN_EGG.get());
        basicItem(ModItems.FAC_TANK_BUSTER_SPAWN_EGG.get());
        basicItem(ModItems.FAC_LION_SPAWN_EGG.get());
        basicItem(ModItems.FAC_COMMISSAR_SPAWN_EGG.get());
        basicItem(ModItems.FAC_WALKER_SPAWN_EGG.get());
        basicItem(ModItems.FAC_TANK_SPAWN_EGG.get());

        basicItem(ModItems.COPPER_KNIGHT_SPAWN_EGG.get());
        basicItem(ModItems.DRONE_SPAWN_EGG.get());
        basicItem(ModItems.TALLMAN_SPAWN_EGG.get());
        basicItem(ModItems.SCOUT_SPAWN_EGG.get());
        basicItem(ModItems.OPPRESSOR_SPAWN_EGG.get());
        basicItem(ModItems.SPRING_JUNKIE_SPAWN_EGG.get());
        basicItem(ModItems.FLAMING_HEAD_SPAWN_EGG.get());
        basicItem(ModItems.SCRAP_GUARD_SPAWN_EGG.get());
        basicItem(ModItems.ARC_PSYCHO_SPAWN_EGG.get());

        basicItem(ModItems.FAILED_ONE_SPAWN_EGG.get());
        basicItem(ModItems.ASGHAR_SURGEON_SPAWN_EGG.get());
        basicItem(ModItems.ASGHAR_WORKER_SPAWN_EGG.get());
        basicItem(ModItems.ASGHAR_FLAMER_SPAWN_EGG.get());
        basicItem(ModItems.CANDLE_FIEND_SPAWN_EGG.get());
        basicItem(ModItems.SOUL_RIPPER_SPAWN_EGG.get());
    }

    private void spawnEgg(RegistryObject<SpawnEggItem> item) {
        if (item.getId() == null) return;
        withExistingParent(item.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
