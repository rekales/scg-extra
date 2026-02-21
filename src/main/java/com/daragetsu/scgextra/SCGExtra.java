package com.daragetsu.scgextra;

import com.daragetsu.scgextra.data.FactionDataLoader;
import com.daragetsu.scgextra.effects.ModEffects;
import com.daragetsu.scgextra.entity.ModEntities;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

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
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class ServerTickHandler {
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END)return;
            MinecraftServer server = event.getServer();
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            for(ServerPlayer player : players){
                if(!player.getTags().contains("raid-1"))return;
                
            }
        }
    }
}