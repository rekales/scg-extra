package net.zincstudios.scgextra.client;

import net.zincstudios.scgextra.client.particle.LargeFlameParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.block.ModBlockEntities;
import net.zincstudios.scgextra.client.particle.CopperFireBallParticle;
import net.zincstudios.scgextra.client.particle.CopperFlameParticle;
import net.zincstudios.scgextra.client.renderer.WreckerTurretBlockRenderer;
import net.zincstudios.scgextra.debug.EntityHeadBoxDebug;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityModel;
import net.zincstudios.scgextra.particle.ModParticleTypes;

@Mod.EventBusSubscriber(modid = SCGExtra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEventHandler {
    private ModClientEventHandler() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityHeadBoxDebug.register();
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NetEntityModel.LAYER_LOCATION, NetEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.WRECKER_TURRET.get(), WreckerTurretBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.COPPER_FIRE_BALL.get(), CopperFireBallParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.COPPER_FLAME.get(), CopperFlameParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.LARGE_FLAME.get(), LargeFlameParticle.Provider::new);
    }
}

