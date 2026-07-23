package net.zincstudios.scgextra;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.zincstudios.scgextra.block.ModBlockEntities;
import net.zincstudios.scgextra.block.ModBlocks;
import net.zincstudios.scgextra.data.RaidDataLoader;
import net.zincstudios.scgextra.datagen.DataGenerators;
import net.zincstudios.scgextra.debug.DevTestCommands;
import net.zincstudios.scgextra.effects.ModEffects;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.neutral.head_hunter.HeadHunterSpawnReplacement;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.zincstudios.scgextra.item.ArmorPiercingHandler;
import net.zincstudios.scgextra.item.ModItems;
import net.zincstudios.scgextra.network.ModNetwork;
import net.zincstudios.scgextra.particle.ModParticleTypes;
import net.zincstudios.scgextra.raid.WaveRaidManager;
import net.zincstudios.scgextra.sounds.ModSounds;
import net.zincstudios.scgextra.sounds.WreckerDeathSounds;

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

        ModEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticleTypes.register(modEventBus);

        ModNetwork.register();

        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modEventBus.addListener(CommonConfig::onLoad);
        modEventBus.addListener(CommonConfig::onReload);

        MinecraftForge.EVENT_BUS.addListener(WaveRaidManager::onLevelTick);
        MinecraftForge.EVENT_BUS.addListener(WaveRaidManager::onLevelLoad);
        MinecraftForge.EVENT_BUS.addListener(Faction::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(HeadHunterSpawnReplacement::onFinalizeSpawn);
        MinecraftForge.EVENT_BUS.addListener(RaidDataLoader::onAddReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(DevTestCommands::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(ArmorPiercingHandler::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(ArmorPiercingHandler::onLivingDamage);
        MinecraftForge.EVENT_BUS.addListener(WreckerDeathSounds::onLivingDeath);

        modEventBus.addListener(DataGenerators::gatherData);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    @SuppressWarnings("removal")
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}