package net.zincstudios.scgextra.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.client.particle.CopperFireBallParticle;
import net.zincstudios.scgextra.client.particle.CopperFlameParticle;
import net.zincstudios.scgextra.debug.EntityHeadBoxDebug;
import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import net.zincstudios.scgextra.entity.common.raid_summoner.RaidSummonerRenderer;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.entity.fac.fac_bluecoat.FacBluecoatRenderer;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarRenderer;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank_buster.FacTankBusterRenderer;
import net.zincstudios.scgextra.entity.fac.fac_trencher.FacTrencherRenderer;
import net.zincstudios.scgextra.entity.fac.trench_goblin.TrenchGoblinRenderer;
import net.zincstudios.scgextra.entity.fac.trench_sniper.TrenchSniperRenderer;
import net.zincstudios.scgextra.entity.neutral.ammo_goblin.AmmoGoblinRenderer;
import net.zincstudios.scgextra.entity.neutral.big_lump.BigLumpRenderer;
import net.zincstudios.scgextra.entity.neutral.end_dweller.EndDwellerRenderer;
import net.zincstudios.scgextra.entity.neutral.end_pod.EndPodRenderer;
import net.zincstudios.scgextra.entity.neutral.end_scorpion.EndScorpionRenderer;
import net.zincstudios.scgextra.entity.neutral.end_stone_crab.EndStoneCrabRenderer;
import net.zincstudios.scgextra.entity.neutral.head_hunter.HeadHunterRenderer;
import net.zincstudios.scgextra.entity.neutral.inflicted_boar.InflictedBoarRenderer;
import net.zincstudios.scgextra.entity.neutral.inflicted_wolf.InflictedWolfRenderer;
import net.zincstudios.scgextra.entity.neutral.mutant_bat.MutantBatRenderer;
import net.zincstudios.scgextra.entity.neutral.netherite_eater.NetheriteEaterRenderer;
import net.zincstudios.scgextra.entity.neutral.nitro_beetle.NitroBeetleRenderer;
import net.zincstudios.scgextra.entity.projectile.SoulFireBallRenderer;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityModel;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.RRCEntities;
import net.zincstudios.scgextra.entity.rrc.arc_psycho.ArcPsychoEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.copper_knight.CopperKnightRenderer;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.flaminghead.FlamingHeadRenderer;
import net.zincstudios.scgextra.entity.rrc.scout.ScoutRenderer;
import net.zincstudios.scgextra.entity.rrc.scrapguard.ScrapGuardRenderer;
import net.zincstudios.scgextra.entity.rrc.spring_junkie.SpringJunkieRenderer;
import net.zincstudios.scgextra.entity.whaler.WhalerEntities;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleRenderer;
import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkRenderer;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueRenderer;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusRenderer;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorRenderer;
import net.zincstudios.scgextra.particle.ModParticleTypes;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import top.ribs.scguns.entity.client.EnemyProjectileRenderer;

@Mod.EventBusSubscriber(modid = SCGExtra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEventHandler {
    private ModClientEventHandler() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(WhalerEntities.FISH_FOLK.get(), FishFolkRenderer::new);
        EntityRenderers.register(WhalerEntities.TURTLEMAN.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("whaler/turtleman")), -10));
        EntityRenderers.register(WhalerEntities.SALMONSAUR.get(), SalmonsaurRenderer::new);
        EntityRenderers.register(WhalerEntities.GUARDIAN_STATUE.get(), GuardianStatueRenderer::new);
        EntityRenderers.register(WhalerEntities.TENTACLIATOR.get(), TentacliatorRenderer::new);
        EntityRenderers.register(WhalerEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorRenderer::new);
        EntityRenderers.register(WhalerEntities.PUFFICUS.get(), PufficusRenderer::new);
        EntityRenderers.register(WhalerEntities.ARMORED_WHALE.get(), ArmoredWhaleRenderer::new);
        EntityRenderers.register(ModEntities.NET.get(), NetEntityRenderer::new);
        EntityRenderers.register(ModEntities.WHALE_PROJECTILE.get(), EnemyProjectileRenderer::new);
        EntityRenderers.register(ModEntities.FIRE_PROJECTILE.get(), EnemyProjectileRenderer::new);
        EntityRenderers.register(ModEntities.RAID_SUMMONER.get(), RaidSummonerRenderer::new);
        EntityRenderers.register(FACEntities.FAC_TRENCHER.get(), (ctx) -> new FacTrencherRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_BLUECOAT.get(), (ctx) -> new FacBluecoatRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.TRENCH_GOBLIN.get(), (ctx) -> new TrenchGoblinRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.TRENCH_SNIPER.get(), (ctx) -> new TrenchSniperRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.SHOVEL_KNIGHT.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_shovel_knight")), -10).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_TANK_BUSTER.get(), (ctx) -> new FacTankBusterRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_LION.get(), (ctx) -> new FacLionRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_COMMISSAR.get(), (ctx) -> new FacCommissarRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_WALKER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_walker")), -10).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_TANK.get(), FacTankRenderer::new);

        EntityRenderers.register(RRCEntities.DRONE.get(), DroneEntityRenderer::new);
        EntityRenderers.register(RRCEntities.TALLMAN.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/tallman"))).noDeathTilt());
        EntityRenderers.register(RRCEntities.SCOUT.get(), (ctx) -> new ScoutRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(RRCEntities.OPPRESSOR.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/oppressor"))).noDeathTilt());
        EntityRenderers.register(RRCEntities.SPRING_JUNKIE.get(), SpringJunkieRenderer::new);
        EntityRenderers.register(RRCEntities.FLAMING_HEAD.get(), FlamingHeadRenderer::new);
        EntityRenderers.register(RRCEntities.SCRAP_GUARD.get(), (ctx) -> new ScrapGuardRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(RRCEntities.ARC_PSYCHO.get(), ArcPsychoEntityRenderer::new);
        EntityRenderers.register(RRCEntities.COPPER_KNIGHT.get(), (ctx) -> new CopperKnightRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(ModEntities.LARGE_SOUL_FIREBALL.get(), SoulFireBallRenderer::new);
        EntityRenderers.register(ModEntities.INFLICTED_BOAR.get(), InflictedBoarRenderer::new);
        EntityRenderers.register(ModEntities.INFLICTED_WOLF.get(), InflictedWolfRenderer::new);
        EntityRenderers.register(ModEntities.AMMO_GOBLIN.get(), AmmoGoblinRenderer::new);
        EntityRenderers.register(ModEntities.BIG_LUMP.get(), BigLumpRenderer::new);
        EntityRenderers.register(ModEntities.MUTANT_BAT.get(), MutantBatRenderer::new);
        EntityRenderers.register(ModEntities.NITRO_BEETLE.get(), NitroBeetleRenderer::new);
        EntityRenderers.register(ModEntities.HEAD_HUNTER.get(), HeadHunterRenderer::new);
        EntityRenderers.register(ModEntities.NETHERITE_EATER.get(), NetheriteEaterRenderer::new);
        EntityRenderers.register(ModEntities.END_POD.get(), EndPodRenderer::new);
        EntityRenderers.register(ModEntities.END_DWELLER.get(), EndDwellerRenderer::new);
        EntityRenderers.register(ModEntities.END_STONE_CRAB.get(), EndStoneCrabRenderer::new);
        EntityRenderers.register(ModEntities.END_SCORPION.get(), EndScorpionRenderer::new);

        EntityHeadBoxDebug.register();
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NetEntityModel.LAYER_LOCATION, NetEntityModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.COPPER_FIRE_BALL.get(), CopperFireBallParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.COPPER_FLAME.get(), CopperFlameParticle.Provider::new);
    }
}
