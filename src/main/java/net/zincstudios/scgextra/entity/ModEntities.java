package net.zincstudios.scgextra.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.OffsetRotatedHeadshotBox;
import net.zincstudios.scgextra.entity.common.WeakPointBox;
import net.zincstudios.scgextra.entity.common.WeakPointBoxManager;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import net.zincstudios.scgextra.entity.common.raid_summoner.RaidSummonerEntity;
import net.zincstudios.scgextra.entity.common.raid_summoner.RaidSummonerRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank_buster.FacTankBusterEntity;
import net.zincstudios.scgextra.entity.fac.fac_bluecoat.FacBluecoatEntity;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarEntity;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarRenderer;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionEntity;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankEntity;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankRenderer;
import net.zincstudios.scgextra.entity.fac.fac_trencher.FacTrencherEntity;
import net.zincstudios.scgextra.entity.fac.fac_walker.FacWalkerEntity;
import net.zincstudios.scgextra.entity.fac.shovel_knight.ShovelKnightEntity;
import net.zincstudios.scgextra.entity.fac.trench_sniper.TrenchSniperEntity;
import net.zincstudios.scgextra.entity.fac.trench_goblin.TrenchGoblinEntity;
import net.zincstudios.scgextra.entity.projectile.net.NetEntity;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityModel;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.flaminghead.FlamingHeadEntity;
import net.zincstudios.scgextra.entity.rrc.flaminghead.FlamingHeadRenderer;
import net.zincstudios.scgextra.entity.rrc.oppressor.OppressorEntity;
import net.zincstudios.scgextra.entity.rrc.scrapguard.ScrapGuardEntity;
import net.zincstudios.scgextra.entity.rrc.scrapguard.ScrapGuardRenderer;
import net.zincstudios.scgextra.entity.rrc.tallman.TallmanEntity;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleEntity;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleRenderer;
import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkEntity;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntity;
import net.zincstudios.scgextra.entity.rrc.scout.ScoutEntity;
import net.zincstudios.scgextra.entity.rrc.spring_junkie.SpringJunkieEntity;
import net.zincstudios.scgextra.entity.rrc.spring_junkie.SpringJunkieRenderer;
import net.zincstudios.scgextra.entity.rrc.arc_psycho.ArcPsychoEntity;
import net.zincstudios.scgextra.entity.rrc.arc_psycho.ArcPsychoEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.copper_knight.CopperKnightEntity;
import net.zincstudios.scgextra.entity.rrc.copper_knight.CopperKnightRenderer;

import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorRenderer;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueEntity;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueRenderer;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.entity.projectile.FireProjectile;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusEntity;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusRenderer;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurEntity;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorRenderer;
import net.zincstudios.scgextra.entity.whaler.turtleman.TurtlemanEntity;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.scout.ScoutRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.common.headshot.BasicHeadshotBox;
import top.ribs.scguns.common.headshot.RotatedHeadshotBox;
import top.ribs.scguns.entity.client.EnemyProjectileRenderer;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK = ENTITY_TYPES
            .register("fish_folk", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("fish_folk"));

    public static final RegistryObject<EntityType<TurtlemanEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtlemanEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(1F, 2.3F)
                    .build("turtleman"));

    public static final RegistryObject<EntityType<SalmonsaurEntity>> SALMONSAUR = ENTITY_TYPES
            .register("salmonsaur", () -> EntityType.Builder.of(SalmonsaurEntity::new, MobCategory.MONSTER)
                    .sized(2F, 2.7F).build("salmonsaur"));

    public static final RegistryObject<EntityType<GuardianStatueEntity>> GUARDIAN_STATUE = ENTITY_TYPES
            .register("guardian_statue", () -> EntityType.Builder.of(GuardianStatueEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(2F, 6.75F)
                    .build("guardian_statue"));

    public static final RegistryObject<EntityType<TentacliatorEntity>> TENTACLIATOR = ENTITY_TYPES
            .register("tentacliator", () -> EntityType.Builder.of(TentacliatorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("tentacliator"));
    
    public static final RegistryObject<EntityType<GlowingTentacliatorEntity>> GLOWING_TENTACLIATOR = ENTITY_TYPES
            .register("glowing_tentacliator", () -> EntityType.Builder.of(GlowingTentacliatorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("glowing_tentacliator"));

    public static final RegistryObject<EntityType<PufficusEntity>> PUFFICUS = ENTITY_TYPES
            .register("pufficus", () -> EntityType.Builder.of(PufficusEntity::new, MobCategory.MONSTER)
                    .sized(1F, 3F).build("pufficus"));

    public static final RegistryObject<EntityType<ArmoredWhaleEntity>> ARMORED_WHALE = ENTITY_TYPES
            .register("armored_whale", () -> EntityType.Builder.of(ArmoredWhaleEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(5.5F, 6F)//i think we should use child elements like enderdragon but not sure
                    .build("armored_whale"));

    public static final RegistryObject<EntityType<RaidSummonerEntity>> RAID_SUMMONER = ENTITY_TYPES
            .register("raid_summoner", () -> EntityType.Builder.of(RaidSummonerEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(0.5F, 0.5F)
                    .build("raid_summoner"));

    public static final RegistryObject<EntityType<FacTrencherEntity>> FAC_TRENCHER = ENTITY_TYPES
            .register("fac_trencher", () -> EntityType.Builder.of(FacTrencherEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("fac_trencher"));

    public static final RegistryObject<EntityType<FacBluecoatEntity>> FAC_BLUECOAT = ENTITY_TYPES
            .register("fac_bluecoat", () -> EntityType.Builder.of(FacBluecoatEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("fac_bluecoat"));

    public static final RegistryObject<EntityType<TrenchGoblinEntity>> TRENCH_GOBLIN = ENTITY_TYPES
            .register("trench_goblin", () -> EntityType.Builder.of(TrenchGoblinEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.2F)
                    .build("trench_goblin"));

    public static final RegistryObject<EntityType<TrenchSniperEntity>> TRENCH_SNIPER = ENTITY_TYPES
            .register("trench_sniper", () -> EntityType.Builder.of(TrenchSniperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("trench_sniper"));

    public static final RegistryObject<EntityType<ShovelKnightEntity>> SHOVEL_KNIGHT = ENTITY_TYPES
            .register("shovel_knight", () -> EntityType.Builder.of(ShovelKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.72F, 2.2F)
                    .build("shovel_knight"));

    public static final RegistryObject<EntityType<FacTankBusterEntity>> FAC_TANK_BUSTER = ENTITY_TYPES
            .register("fac_tank_buster", () -> EntityType.Builder.of(FacTankBusterEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.95F)
                    .build("fac_tank_buster"));

    public static final RegistryObject<EntityType<FacLionEntity>> FAC_LION = ENTITY_TYPES
            .register("fac_lion", () -> EntityType.Builder.of(FacLionEntity::new, MobCategory.MONSTER)
                    .sized(1.9F, 2.95F)
                    .build("fac_lion"));

    public static final RegistryObject<EntityType<FacCommissarEntity>> FAC_COMMISSAR = ENTITY_TYPES
            .register("fac_commissar", () -> EntityType.Builder.of(FacCommissarEntity::new, MobCategory.MONSTER)
                    .sized(0.81F, 2.15F)
                    .build("fac_commissar"));

    public static final RegistryObject<EntityType<FacWalkerEntity>> FAC_WALKER = ENTITY_TYPES
            .register("fac_walker", () -> EntityType.Builder.of(FacWalkerEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(1.4F, 3.85F)
                    .build("fac_walker"));

    public static final RegistryObject<EntityType<FacTankEntity>> FAC_TANK = ENTITY_TYPES
            .register("fac_tank", () -> EntityType.Builder.of(FacTankEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(2.5F, 3.7F)
                    .build("fac_tank"));

    public static final RegistryObject<EntityType<NetEntity>> NET = ENTITY_TYPES
            .register("net", () -> EntityType.Builder.<NetEntity>of(NetEntity::new, MobCategory.MISC).sized(4F, 1F).clientTrackingRange(4).updateInterval(20).build("net"));

    // Copied from SCGuns ModEntities.ENEMY_PROJECTILE
    public static final RegistryObject<EntityType<ArmoredWhaleProjectileEntity>> WHALE_PROJECTILE = ENTITY_TYPES
            .register("whale_tank_projectile", () -> EntityType.Builder.of(ArmoredWhaleProjectileEntity::create, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("whale_tank_projectile"));
                    
    //RRC
    public static final RegistryObject<EntityType<DroneEntity>> DRONE = ENTITY_TYPES
            .register("drone", () -> EntityType.Builder.of(DroneEntity::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F).build("drone"));

    public static final RegistryObject<EntityType<TallmanEntity>> TALLMAN = ENTITY_TYPES
            .register("tallman", () -> EntityType.Builder.of(TallmanEntity::new, MobCategory.MONSTER)
                    .sized(1F, 4.5F)
                    .build("tallman"));

    public static final RegistryObject<EntityType<ScoutEntity>> SCOUT = ENTITY_TYPES
            .register("scout", () -> EntityType.Builder.of(ScoutEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("scout"));

    public static final RegistryObject<EntityType<OppressorEntity>> OPPRESSOR = ENTITY_TYPES
            .register("oppressor", () -> EntityType.Builder.of(OppressorEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 5F)
                    .build("oppressor"));

    public static final RegistryObject<EntityType<SpringJunkieEntity>> SPRING_JUNKIE = ENTITY_TYPES
            .register("spring_junkie", () -> EntityType.Builder.of(SpringJunkieEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 4F)
                    .build("spring_junkie"));

    public static final RegistryObject<EntityType<FlamingHeadEntity>> FLAMING_HEAD = ENTITY_TYPES
            .register("flaming_head", () -> EntityType.Builder.of(FlamingHeadEntity::new, MobCategory.MONSTER)
                    .sized(2.6F, 5F)
                    .setUpdateInterval(1)
                    .build("flaming_head"));

    public static final RegistryObject<EntityType<ScrapGuardEntity>> SCRAP_GUARD = ENTITY_TYPES
            .register("scrap_guard", () -> EntityType.Builder.of(ScrapGuardEntity::new, MobCategory.MONSTER)
                    .sized(1.3F, 3F)
                    .build("scrap_guard"));

    public static final RegistryObject<EntityType<ArcPsychoEntity>> ARC_PSYCHO = ENTITY_TYPES
            .register("arc_psycho", () -> EntityType.Builder.of(ArcPsychoEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2F)
                    .build("arc_psycho"));

    public static final RegistryObject<EntityType<CopperKnightEntity>> COPPER_KNIGHT = ENTITY_TYPES
            .register("copper_knight", () -> EntityType.Builder.of(CopperKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2F)
                    .build("copper_knight"));

    public static final RegistryObject<EntityType<FireProjectile>> FIRE_PROJECTILE = ENTITY_TYPES
            .register("fire_projectile", () -> EntityType.Builder.of(FireProjectile::create, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("fire_projectile"));


    public static void register(IEventBus modEventBus){
        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(ModEntities::registerLayers);
        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModEntities::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(EntityAdjustments::onEntityJoin);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ModEntities::onClientSetup);
        }

    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FISH_FOLK.get(), FishFolkRenderer::new);
        EntityRenderers.register(ModEntities.TURTLEMAN.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("whaler/turtleman")), -10));
        EntityRenderers.register(ModEntities.SALMONSAUR.get(), SalmonsaurRenderer::new);
        EntityRenderers.register(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueRenderer::new);
        EntityRenderers.register(ModEntities.TENTACLIATOR.get(), TentacliatorRenderer::new);
        EntityRenderers.register(ModEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorRenderer::new);
        EntityRenderers.register(ModEntities.PUFFICUS.get(), PufficusRenderer::new);
        EntityRenderers.register(ModEntities.NET.get(), NetEntityRenderer::new);
        EntityRenderers.register(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleRenderer::new);
        EntityRenderers.register(ModEntities.WHALE_PROJECTILE.get(), EnemyProjectileRenderer::new);
        EntityRenderers.register(ModEntities.FIRE_PROJECTILE.get(), EnemyProjectileRenderer::new);
        EntityRenderers.register(ModEntities.RAID_SUMMONER.get(), RaidSummonerRenderer::new);
        EntityRenderers.register(ModEntities.FAC_TRENCHER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_BLUECOAT.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.TRENCH_GOBLIN.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder"))).noDeathTilt());
        EntityRenderers.register(ModEntities.TRENCH_SNIPER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.SHOVEL_KNIGHT.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_shovel_knight")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_TANK_BUSTER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("placeholder")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_LION.get(), (ctx) -> new FacLionRenderer(ctx).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_COMMISSAR.get(), (ctx) -> new FacCommissarRenderer(ctx).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_WALKER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_walker")), -10).noDeathTilt());
        EntityRenderers.register(ModEntities.FAC_TANK.get(), FacTankRenderer::new);

        EntityRenderers.register(ModEntities.DRONE.get(), DroneEntityRenderer::new);
        EntityRenderers.register(ModEntities.TALLMAN.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/tallman"))).noDeathTilt());
        EntityRenderers.register(ModEntities.SCOUT.get(), (ctx) -> new ScoutRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(ModEntities.OPPRESSOR.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/oppressor"))).noDeathTilt());
        EntityRenderers.register(ModEntities.SPRING_JUNKIE.get(), SpringJunkieRenderer::new);
        EntityRenderers.register(ModEntities.FLAMING_HEAD.get(), FlamingHeadRenderer::new);
        EntityRenderers.register(ModEntities.SCRAP_GUARD.get(), (ctx) -> new ScrapGuardRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(ModEntities.ARC_PSYCHO.get(), ArcPsychoEntityRenderer::new);
        EntityRenderers.register(ModEntities.COPPER_KNIGHT.get(), (ctx) -> new CopperKnightRenderer<>(ctx).noDeathTilt());
    }

    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(NetEntityModel.LAYER_LOCATION, NetEntityModel::createBodyLayer);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), TurtlemanEntity.createAttributes().build());
        event.put(ModEntities.SALMONSAUR.get(), SalmonsaurEntity.createAttributes().build());
        event.put(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
        event.put(ModEntities.TENTACLIATOR.get(), TentacliatorEntity.createAttributes().build());
        event.put(ModEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorEntity.createAttributes().build());
        event.put(ModEntities.PUFFICUS.get(), PufficusEntity.createAttributes().build());
        event.put(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleEntity.createAttributes().build());
        event.put(ModEntities.RAID_SUMMONER.get(), RaidSummonerEntity.createAttributes().build());
        event.put(ModEntities.FAC_TRENCHER.get(), FacTrencherEntity.createAttributes().build());
        event.put(ModEntities.FAC_BLUECOAT.get(), FacBluecoatEntity.createAttributes().build());
        event.put(ModEntities.TRENCH_GOBLIN.get(), TrenchGoblinEntity.createAttributes().build());
        event.put(ModEntities.TRENCH_SNIPER.get(), TrenchSniperEntity.createAttributes().build());
        event.put(ModEntities.SHOVEL_KNIGHT.get(), ShovelKnightEntity.createAttributes().build());
        event.put(ModEntities.FAC_TANK_BUSTER.get(), FacTankBusterEntity.createAttributes().build());
        event.put(ModEntities.FAC_LION.get(), FacLionEntity.createAttributes().build());
        event.put(ModEntities.FAC_COMMISSAR.get(), FacCommissarEntity.createAttributes().build());
        event.put(ModEntities.FAC_WALKER.get(), FacWalkerEntity.createAttributes().build());
        event.put(ModEntities.FAC_TANK.get(), FacTankEntity.createAttributes().build());

        event.put(ModEntities.DRONE.get(), DroneEntity.createAttributes().build());
        event.put(ModEntities.TALLMAN.get(), TallmanEntity.createAttributes().build());
        event.put(ModEntities.SCOUT.get(), ScoutEntity.createAttributes().build());
        event.put(ModEntities.OPPRESSOR.get(), OppressorEntity.createAttributes().build());
        event.put(ModEntities.SPRING_JUNKIE.get(), SpringJunkieEntity.createAttributes().build());
        event.put(ModEntities.FLAMING_HEAD.get(), FlamingHeadEntity.createAttributes().build());
        event.put(ModEntities.SCRAP_GUARD.get(), ScrapGuardEntity.createAttributes().build());
        event.put(ModEntities.ARC_PSYCHO.get(), ArcPsychoEntity.createAttributes().build());
        event.put(ModEntities.COPPER_KNIGHT.get(), CopperKnightEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        BoundingBoxManager.registerHeadshotBox(ModEntities.TURTLEMAN.get(), new BasicHeadshotBox<>(11.0, 28.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.DRONE.get(), new RotatedHeadshotBox<>(15.0, 28.0, 20, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FLAMING_HEAD.get(), new OffsetRotatedHeadshotBox<>(10.0F, 55.0, 13, 70, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_TANK_BUSTER.get(), new BasicHeadshotBox<>(8.0, 24.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_WALKER.get(), new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_TANK.get(), new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true));

        WeakPointBoxManager.registerWeakPointBox(ModEntities.FLAMING_HEAD.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(10.0F, 55.0, 13, -70, false, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.FAC_WALKER.get(), new WeakPointBox<>(new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.FAC_TANK.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true)));
    }

    // TODO: registration helper
}
