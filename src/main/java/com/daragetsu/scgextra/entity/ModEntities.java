package com.daragetsu.scgextra.entity;

import com.daragetsu.scgextra.SCGExtra;
import com.daragetsu.scgextra.entity.fishfolk.FishFolkEntity;

import com.daragetsu.scgextra.entity.turtleman.TurtleManEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK_ENTITY = ENTITY_TYPES
            .register("fish_folk", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F).build("fish_folk"));

    public static final RegistryObject<EntityType<TurtleManEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtleManEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("turtleman"));


    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}