package net.zincstudios.scgextra;

import net.minecraftforge.fml.config.ModConfig;
import net.zincstudios.scgextra.datagen.DataGenerators;
import net.zincstudios.scgextra.debug.EntityHeadBoxDebug;
import net.zincstudios.scgextra.effects.ModEffects;
import net.zincstudios.scgextra.entity.ModEntities;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.zincstudios.scgextra.item.ModItems;
import net.zincstudios.scgextra.sounds.ModSounds;

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
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modEventBus.addListener(CommonConfig::onLoad);
        modEventBus.addListener(CommonConfig::onReload);

        EntityHeadBoxDebug.register();

        modEventBus.addListener(DataGenerators::gatherData);
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