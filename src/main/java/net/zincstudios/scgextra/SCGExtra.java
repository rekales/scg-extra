package net.zincstudios.scgextra;

import net.zincstudios.scgextra.data.FactionDataLoader;
import net.zincstudios.scgextra.effects.ModEffects;
import net.zincstudios.scgextra.entity.ModEntities;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod(SCGExtra.MOD_ID)
public class SCGExtra
{
    public static final String MOD_ID = "scgextra";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SCGExtra(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        ModEntities.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.addListener(FactionDataLoader::onAddReloadListeners);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    @SuppressWarnings("removal")
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

/* Refactoring Notes:
- Following recommendations for asset paths
- Adding subdirectories per faction for scalability when adding more entities
- Using DefaultedEntityGeoModel which also provides automatic head rotation control
- Adding a way for quick entity models with texture variations, suing vanilla's system
- Duplicated Tentacliator's texture as placeholder for the glow squid variant
- I think "salmonsaurus" is the correct spelling? based on other dinosaur names at least.
- Decided to use that "salmonsaur" internally, or maybe "salmonsaurus" is more apt?
- I think you forgor to change the salmonsaurus to a geomodel
- Changing entity equipment to be data based
- (Changes) Ended up changing the gun equipment to match with requirements.
- (Changes) Ended up removing the armor equipment, not sure why that's there
- I'm not sure if there's a drop-only whaler guns, only checked guns that uses the whaler blueprint.
- TODO: We'll need to prepare a two-handed pose for the fishfolk
 */