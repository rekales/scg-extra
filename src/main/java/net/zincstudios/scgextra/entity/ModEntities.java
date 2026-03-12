package net.zincstudios.scgextra.entity;

import net.minecraftforge.common.MinecraftForge;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.projectile.net.NetEntity;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityModel;
import net.zincstudios.scgextra.entity.projectile.net.NetEntityRenderer;
import net.zincstudios.scgextra.entity.rrc.tallman.TallmanEntity;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleEntity;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleRenderer;
import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkEntity;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntity;

import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorRenderer;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueEntity;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueRenderer;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusEntity;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusRenderer;
import net.zincstudios.scgextra.entity.whaler.raid_summoner.RaidSummonerEntity;
import net.zincstudios.scgextra.entity.whaler.raid_summoner.RaidSummonerRenderer;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurEntity;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurRenderer;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorRenderer;
import net.zincstudios.scgextra.entity.whaler.turtleman.TurtlemanEntity;
import net.zincstudios.scgextra.entity.whaler.turtleman.TurtlemanRenderer;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntityRenderer;
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
import top.ribs.scguns.entity.client.EnemyProjectileRenderer;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK = ENTITY_TYPES
            .register("fish_folk", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("fish_folk"));

    public static final RegistryObject<EntityType<TurtlemanEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtlemanEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(0.6F, 1.95F)
                    .build("turtleman"));

    public static final RegistryObject<EntityType<SalmonsaurEntity>> SALMONSAUR = ENTITY_TYPES
            .register("salmonsaur", () -> EntityType.Builder.of(SalmonsaurEntity::new, MobCategory.MONSTER)
                    .sized(2F, 3.2F).build("salmonsaur"));

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
                    .sized(2F, 3.9F).build("drone"));

    public static final RegistryObject<EntityType<TallmanEntity>> TALLMAN = ENTITY_TYPES
            .register("tallman", () -> EntityType.Builder.of(TallmanEntity::new, MobCategory.MONSTER)
                    .sized(1F, 2F)
                    .build("tallman"));

    public static void register(IEventBus modEventBus){
        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(ModEntities::registerLayers);
        modEventBus.addListener(ModEntities::registerAttributes);
        MinecraftForge.EVENT_BUS.addListener(EntityAdjustments::onEntityJoin);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ModEntities::onClientSetup);
        }
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FISH_FOLK.get(), FishFolkRenderer::new);
        EntityRenderers.register(ModEntities.TURTLEMAN.get(), TurtlemanRenderer::new);
        EntityRenderers.register(ModEntities.SALMONSAUR.get(), SalmonsaurRenderer::new);
        EntityRenderers.register(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueRenderer::new);
        EntityRenderers.register(ModEntities.TENTACLIATOR.get(), TentacliatorRenderer::new);
        EntityRenderers.register(ModEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorRenderer::new);
        EntityRenderers.register(ModEntities.PUFFICUS.get(), PufficusRenderer::new);
        EntityRenderers.register(ModEntities.NET.get(), NetEntityRenderer::new);
        EntityRenderers.register(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleRenderer::new);
        EntityRenderers.register(ModEntities.WHALE_PROJECTILE.get(), EnemyProjectileRenderer::new);
        EntityRenderers.register(ModEntities.RAID_SUMMONER.get(), RaidSummonerRenderer::new);
        EntityRenderers.register(ModEntities.DRONE.get(), DroneEntityRenderer::new);
        EntityRenderers.register(ModEntities.TALLMAN.get(), PlaceholderEntityRenderer::new);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(NetEntityModel.LAYER_LOCATION, NetEntityModel::createBodyLayer);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), TurtlemanEntity.createAttributes().build());
        event.put(ModEntities.SALMONSAUR.get(), SalmonsaurEntity.createAttributes().build());
        event.put(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
        event.put(ModEntities.TENTACLIATOR.get(), TentacliatorEntity.createAttributes().build());
        event.put(ModEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorEntity.createAttributes().build());
        event.put(ModEntities.PUFFICUS.get(), PufficusEntity.createAttributes().build());
        event.put(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleEntity.createAttributes().build());
        event.put(ModEntities.RAID_SUMMONER.get(), RaidSummonerEntity.createAttributes().build());
        event.put(ModEntities.DRONE.get(), DroneEntity.createAttributes().build());
        event.put(ModEntities.TALLMAN.get(), TallmanEntity.createAttributes().build());
    }
}