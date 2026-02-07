package com.daragetsu.scgextra.entity;

import com.daragetsu.scgextra.Main;
import com.daragetsu.scgextra.entity.FishFolk.FishFolkEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Main.MOD_ID);

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK_ENTITY = ENTITY_TYPES.register("fish_folk_entity", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("fish_folk_entity"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}