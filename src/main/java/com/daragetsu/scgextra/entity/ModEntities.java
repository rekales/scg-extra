package com.daragetsu.scgextra.entity;

import com.daragetsu.scgextra.SCGExtra;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import com.daragetsu.scgextra.entity.fishfolk.FishFolkModel;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkRenderer;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueEntity;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueModel;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueRenderer;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursEntity;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursModel;
import com.daragetsu.scgextra.entity.salmonsaurs.SalmonsaursRenderer;
import com.daragetsu.scgextra.entity.turtleman.TurtleManEntity;
import com.daragetsu.scgextra.entity.turtleman.TurtleManModel;
import com.daragetsu.scgextra.entity.turtleman.TurtleManRenderer;
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

    public static final RegistryObject<EntityType<TurtleManEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtleManEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("turtleman"));

    public static final RegistryObject<EntityType<SalmonsaursEntity>> SALMONSAURS = ENTITY_TYPES
            .register("salmonsaurs", () -> EntityType.Builder.of(SalmonsaursEntity::new, MobCategory.MONSTER)
                    .sized(1.5F, 2F).build("salmonsaurs"));

    public static final RegistryObject<EntityType<GuardianStatueEntity>> GUARDIAN_STATUE = ENTITY_TYPES
            .register("guardian_statue", () -> EntityType.Builder.of(GuardianStatueEntity::new, MobCategory.MONSTER)
                    .sized(3F, 6.75F).build("guardian_statue"));


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
        EntityRenderers.register(ModEntities.TURTLEMAN.get(), TurtleManRenderer::new);
        EntityRenderers.register(ModEntities.SALMONSAURS.get(), SalmonsaursRenderer::new);
        EntityRenderers.register(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueRenderer::new);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(FishFolkModel.LAYER_LOCATION, FishFolkModel::createBodyLayer);
        event.registerLayerDefinition(TurtleManModel.LAYER_LOCATION, TurtleManModel::createBodyLayer);
        event.registerLayerDefinition(SalmonsaursModel.LAYER_LOCATION, SalmonsaursModel::createBodyLayer);
        event.registerLayerDefinition(GuardianStatueModel.LAYER_LOCATION, GuardianStatueModel::createBodyLayer);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), TurtleManEntity.createAttributes().build());
        event.put(ModEntities.SALMONSAURS.get(), SalmonsaursEntity.createAttributes().build());
        event.put(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
    }
}