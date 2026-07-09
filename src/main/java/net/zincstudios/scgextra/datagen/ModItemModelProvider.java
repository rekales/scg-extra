package net.zincstudios.scgextra.datagen;


import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.item.ModItems;

import java.util.Objects;

@SuppressWarnings({"UnusedReturnValue", "unused", "removal"})
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WALKER_MG.get());
        basicItem(ModItems.END_SHELL.get());

        basicItem("medal/", ModItems.MEDAL_OF_SURVIVOR);
        basicItem("medal/", ModItems.MEDAL_OF_IRON_WILL);
        basicItem("medal/", ModItems.MEDAL_OF_DEFIANCE);
        basicItem("medal/", ModItems.MEDAL_OF_WONDER);
        basicItem("medal/", ModItems.MEDAL_OF_FIERY_RAGE);
        basicItem("medal/", ModItems.MEDAL_OF_OBEDIENCE);
        basicItem("medal/", ModItems.MEDAL_OF_CRUELTY);
        basicItem("medal/", ModItems.MEDAL_OF_ENLIGHTENMENT);
        basicItem("medal/", ModItems.MEDAL_OF_CONQUEROR);

        withExistingParent("oppressor_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("oppressor_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("oppressor_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("oppressor_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("commissar_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("commissar_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("commissar_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("commissar_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("treated_iron_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("treated_iron_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("treated_iron_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("treated_iron_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("leviathan_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("leviathan_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("leviathan_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("leviathan_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("golden_idol_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("golden_idol_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("golden_idol_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("golden_idol_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("enlightened_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("enlightened_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("enlightened_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("enlightened_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("juggernaut_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("juggernaut_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("juggernaut_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("juggernaut_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("ritual_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("ritual_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("ritual_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("ritual_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

        withExistingParent("pioneer_helmet", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_helmet"));
        withExistingParent("pioneer_chestplate", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_chestplate"));
        withExistingParent("pioneer_leggings", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_leggings"));
        withExistingParent("pioneer_boots", mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/iron_boots"));

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

        basicItem(ModItems.INFLICTED_BOAR_SPAWN_EGG.get());
        basicItem(ModItems.INFLICTED_WOLF_SPAWN_EGG.get());
        basicItem(ModItems.AMMO_GOBLIN_SPAWN_EGG.get());
        basicItem(ModItems.BIG_LUMP_SPAWN_EGG.get());
        basicItem(ModItems.MUTANT_BAT_SPAWN_EGG.get());
        basicItem(ModItems.NITRO_BEETLE_SPAWN_EGG.get());
        basicItem(ModItems.HEAD_HUNTER_SPAWN_EGG.get());
        basicItem(ModItems.NETHERITE_EATER_SPAWN_EGG.get());
        basicItem(ModItems.END_POD_SPAWN_EGG.get());
        basicItem(ModItems.END_DWELLER_SPAWN_EGG.get());
        basicItem(ModItems.END_STONE_CRAB_SPAWN_EGG.get());
        basicItem(ModItems.END_SCORPION_SPAWN_EGG.get());

        basicItem(ModItems.COG_VULTURE_SPAWN_EGG.get());
        basicItem(ModItems.COG_DEVASTATOR_SPAWN_EGG.get());
        basicItem(ModItems.COG_BOMBARDIER_SPAWN_EGG.get());
        basicItem(ModItems.COG_GIGANTES_SPAWN_EGG.get());
        basicItem(ModItems.COG_VENATOR_SPAWN_EGG.get());
        basicItem(ModItems.COG_CENTIPEDE_SPAWN_EGG.get());
        basicItem(ModItems.COG_JUGGERNAUT_SPAWN_EGG.get());
    }

    public ItemModelBuilder basicItem(RegistryObject<? extends Item> item) {
        return basicItem(item.get());
    }

    public ItemModelBuilder basicItem(String prefix, RegistryObject<? extends Item> item) {
        return basicItem(prefix, item.get());
    }

    public ItemModelBuilder basicItem(String prefix, Item item) {
        return basicItem(prefix, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ItemModelBuilder basicItem(String prefix, ResourceLocation item) {
        return getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + prefix + item.getPath()));
    }

    private void spawnEgg(RegistryObject<SpawnEggItem> item) {
        if (item.getId() == null) return;
        withExistingParent(item.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
