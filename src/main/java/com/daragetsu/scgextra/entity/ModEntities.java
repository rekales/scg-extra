package com.daragetsu.scgextra.entity;

import com.daragetsu.scgextra.SCGExtra;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import com.daragetsu.scgextra.entity.fishfolk.FishFolkModel;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkRenderer;
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
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(FishFolkModel.LAYER_LOCATION, FishFolkModel::createBodyLayer);
        event.registerLayerDefinition(TurtleManModel.LAYER_LOCATION, TurtleManModel::createBodyLayer);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), FishFolkEntity.createAttributes().build());
    }
}