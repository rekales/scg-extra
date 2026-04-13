package net.zincstudios.scgextra.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.zincstudios.scgextra.entity.EntityTypeTags;
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
                .add(ModEntities.SALMONSAUR.get())
                .add(ModEntities.FAC_LION.get());

        tag(ModTags.Entities.VERY_HEAVY)
                .add(ModEntities.GUARDIAN_STATUE.get())
                .add(ModEntities.ARMORED_WHALE.get())
                .add(ModEntities.FAC_WALKER.get())
                .add(ModEntities.FAC_TANK.get());

        tag(ModTags.Entities.WHITE_BLOOD)
                .add(ModEntities.GUARDIAN_STATUE.get());

        tag(ModTags.Entities.CANNOT_BE_LACERATED)
                .add(ModEntities.GUARDIAN_STATUE.get())
                .add(ModEntities.FAC_TANK.get())
                .add(ModEntities.FAC_WALKER.get());

        tag(EntityTypeTags.ASGHARIAN)
                .add(top.ribs.scguns.init.ModEntities.DISSIDENT.get())
                .add(top.ribs.scguns.init.ModEntities.PRAETOR.get())
                .add(top.ribs.scguns.init.ModEntities.ADJUDICATOR.get())
                .add(top.ribs.scguns.init.ModEntities.SUBJUGATOR.get());

        tag(EntityTypeTags.COG)
                .add(top.ribs.scguns.init.ModEntities.COG_KNIGHT.get())
                .add(top.ribs.scguns.init.ModEntities.TRAUMA_UNIT.get())
                .add(top.ribs.scguns.init.ModEntities.COG_MINION.get())
                .add(top.ribs.scguns.init.ModEntities.SKY_CARRIER.get())                .add(top.ribs.scguns.init.ModEntities.COG_KNIGHT.get())
                .add(top.ribs.scguns.init.ModEntities.SIGNAL_BEACON.get())
                .add(top.ribs.scguns.init.ModEntities.SCAMPLER.get())
                .add(top.ribs.scguns.init.ModEntities.SCAMP_TANK.get());

        tag(EntityTypeTags.FAC)
                .add(ModEntities.FAC_TRENCHER.get())
                .add(ModEntities.FAC_BLUECOAT.get())
                .add(ModEntities.TRENCH_GOBLIN.get())
                .add(ModEntities.TRENCH_SNIPER.get())
                .add(ModEntities.SHOVEL_KNIGHT.get())
                .add(ModEntities.FAC_TANK_BUSTER.get())
                .add(ModEntities.FAC_LION.get())
                .add(ModEntities.FAC_COMMISSAR.get())
                .add(ModEntities.FAC_WALKER.get())
                .add(ModEntities.FAC_TANK.get());

        tag(EntityTypeTags.WHALER)
                .add(ModEntities.FISH_FOLK.get())
                .add(ModEntities.TURTLEMAN.get())
                .add(ModEntities.SALMONSAUR.get())
                .add(ModEntities.TENTACLIATOR.get())
                .add(ModEntities.GLOWING_TENTACLIATOR.get())
                .add(ModEntities.PUFFICUS.get())
                .add(ModEntities.GUARDIAN_STATUE.get())
                .add(ModEntities.ARMORED_WHALE.get())
                .add(top.ribs.scguns.init.ModEntities.FINFORCER.get());

        tag(EntityTypeTags.RRC)
                .add(ModEntities.DRONE.get())
                .add(ModEntities.TALLMAN.get())
                .add(ModEntities.SCOUT.get())
                .add(ModEntities.OPPRESSOR.get())
                .add(ModEntities.SPRING_JUNKIE.get())
                .add(ModEntities.SCRAP_GUARD.get())
                .add(ModEntities.ARC_PSYCHO.get())
                .add(ModEntities.FLAMING_HEAD.get())
                .add(ModEntities.COPPER_KNIGHT.get());
    }
}
