package com.daragetsu.scgextra.entity;

import com.daragetsu.scgextra.SCGExtra;
import com.daragetsu.scgextra.entity.Net.NetEntity;
import com.daragetsu.scgextra.entity.Net.NetEntityModel;
import com.daragetsu.scgextra.entity.Net.NetEntityRenderer;
import com.daragetsu.scgextra.entity.armored_whale.ArmoredWhaleEntity;
import com.daragetsu.scgextra.entity.armored_whale.ArmoredWhaleRenderer;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import com.daragetsu.scgextra.entity.fishfolk.FishFolkRenderer;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueEntity;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueRenderer;
import com.daragetsu.scgextra.entity.pufficus.PufficusEntity;
import com.daragetsu.scgextra.entity.pufficus.PufficusRenderer;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursEntity;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursModel;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursRenderer;
import com.daragetsu.scgextra.entity.tentacliator.TentacliatorEntity;
import com.daragetsu.scgextra.entity.tentacliator.TentacliatorRenderer;
import com.daragetsu.scgextra.entity.turtleman.TurtlemanEntity;
import com.daragetsu.scgextra.entity.turtleman.TurtlemanRenderer;
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

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK = ENTITY_TYPES
            .register("fish_folk", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F).build("fish_folk"));

    public static final RegistryObject<EntityType<TurtlemanEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtlemanEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(0.6F, 1.95F)
                    .build("turtleman"));

    public static final RegistryObject<EntityType<SalmonsaursEntity>> SALMONSAURS = ENTITY_TYPES
            .register("salmonsaurs", () -> EntityType.Builder.of(SalmonsaursEntity::new, MobCategory.MONSTER)
                    .sized(2F, 2.5F).build("salmonsaurs"));

    public static final RegistryObject<EntityType<GuardianStatueEntity>> GUARDIAN_STATUE = ENTITY_TYPES
            .register("guardian_statue", () -> EntityType.Builder.of(GuardianStatueEntity::new, MobCategory.MONSTER)
                    .sized(3F, 6.75F).build("guardian_statue"));

    public static final RegistryObject<EntityType<TentacliatorEntity>> TENTACLIATOR = ENTITY_TYPES
            .register("tentacliator", () -> EntityType.Builder.of(TentacliatorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("tentacliator"));

    public static final RegistryObject<EntityType<PufficusEntity>> PUFFICUS = ENTITY_TYPES
            .register("pufficus", () -> EntityType.Builder.of(PufficusEntity::new, MobCategory.MONSTER)
                    .sized(1F, 3F).build("pufficus"));

    public static final RegistryObject<EntityType<ArmoredWhaleEntity>> ARMORED_WHALE = ENTITY_TYPES
            .register("armored_whale", () -> EntityType.Builder.of(ArmoredWhaleEntity::new, MobCategory.MONSTER)
                    .sized(5F, 5F)//i think we should use child elements like enderdragon but not sure
                    .build("armored_whale"));

    public static final RegistryObject<EntityType<NetEntity>> NET = ENTITY_TYPES
            .register("net", () -> EntityType.Builder.<NetEntity>of(NetEntity::new, MobCategory.MISC).sized(4F, 1F).clientTrackingRange(4).updateInterval(20).build("net"));


    public static void register(IEventBus modEventBus){
        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(ModEntities::registerLayers);
        modEventBus.addListener(ModEntities::registerAttributes);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ModEntities::onClientSetup);
        }
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.FISH_FOLK.get(), FishFolkRenderer::new);
        EntityRenderers.register(ModEntities.TURTLEMAN.get(), TurtlemanRenderer::new);
        EntityRenderers.register(ModEntities.SALMONSAURS.get(), SalmonsaursRenderer::new);
        EntityRenderers.register(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueRenderer::new);
        EntityRenderers.register(ModEntities.TENTACLIATOR.get(), TentacliatorRenderer::new);
        EntityRenderers.register(ModEntities.PUFFICUS.get(), PufficusRenderer::new);
        EntityRenderers.register(ModEntities.NET.get(), NetEntityRenderer::new);
        EntityRenderers.register(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleRenderer::new);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
//        event.registerLayerDefinition(FishFolkModel.LAYER_LOCATION, FishFolkModel::createBodyLayer);
//        event.registerLayerDefinition(TurtlemanModel.LAYER_LOCATION, TurtlemanModel::createBodyLayer);
        event.registerLayerDefinition(SalmonsaursModel.LAYER_LOCATION, SalmonsaursModel::createBodyLayer);
        event.registerLayerDefinition(NetEntityModel.LAYER_LOCATION, NetEntityModel::createBodyLayer);
//        event.registerLayerDefinition(GuardianStatueModel.LAYER_LOCATION, GuardianStatueModel::createBodyLayer);
//        event.registerLayerDefinition(TentacliatorModel.LAYER_LOCATION, TentacliatorModel::createBodyLayer);
//        event.registerLayerDefinition(PufficusModel.LAYER_LOCATION, PufficusModel::createBodyLayer);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), TurtlemanEntity.createAttributes().build());
        event.put(ModEntities.SALMONSAURS.get(), SalmonsaursEntity.createAttributes().build());
        event.put(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
        event.put(ModEntities.TENTACLIATOR.get(), TentacliatorEntity.createAttributes().build());
        event.put(ModEntities.PUFFICUS.get(), PufficusEntity.createAttributes().build());
        event.put(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleEntity.createAttributes().build());
    }
}