package net.zincstudios.scgextra.entity.neutral;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

import net.zincstudios.scgextra.entity.common.OffsetRotatedHeadshotBox;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.entity.neutral.ammo_goblin.AmmoGoblinEntity;
import net.zincstudios.scgextra.entity.neutral.ammo_goblin.AmmoGoblinRenderer;
import net.zincstudios.scgextra.entity.neutral.big_lump.BigLumpEntity;
import net.zincstudios.scgextra.entity.neutral.big_lump.BigLumpRenderer;
import net.zincstudios.scgextra.entity.neutral.inflicted_boar.InflictedBoarEntity;
import net.zincstudios.scgextra.entity.neutral.inflicted_boar.InflictedBoarRenderer;
import net.zincstudios.scgextra.entity.neutral.inflicted_wolf.InflictedWolfEntity;
import net.zincstudios.scgextra.entity.neutral.inflicted_wolf.InflictedWolfRenderer;
import top.ribs.scguns.common.BoundingBoxManager;

public class NeutralEntities {
    
    public static final RegistryObject<EntityType<InflictedBoarEntity>> INFLICTED_BOAR = ENTITY_TYPES.register(
        "inflicted_boar", 
        () -> EntityType.Builder.of(
            InflictedBoarEntity::new, 
            MobCategory.MONSTER
        )
        .sized(1.3964844F, 1.4F)
        .clientTrackingRange(8)
        .build("inflicted_boar")
    );
    
    public static final RegistryObject<EntityType<InflictedWolfEntity>> INFLICTED_WOLF = ENTITY_TYPES.register(
        "inflicted_wolf", 
        () -> EntityType.Builder.of(
            InflictedWolfEntity::new, 
            MobCategory.MONSTER
        )
        .sized(1.3964844F, 1.4F)
        .build("inflicted_wolf")
    );
    
    public static final RegistryObject<EntityType<AmmoGoblinEntity>> AMMO_GOBLIN = ENTITY_TYPES.register(
        "ammo_goblin", 
        () -> EntityType.Builder.of(
            AmmoGoblinEntity::new, 
            MobCategory.MONSTER
        )
        .sized(0.6F, 1.4F)
        .build("ammo_goblin")
    );
    
    public static final RegistryObject<EntityType<BigLumpEntity>> BIG_LUMP = ENTITY_TYPES.register(
        "big_lump", 
        () -> EntityType.Builder.of(
            BigLumpEntity::new, 
            MobCategory.MONSTER
        )
        .sized(3F, 3F)
        .build("big_lump")
    );
    
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NeutralEntities::registerAttributes);
        modEventBus.addListener(NeutralEntities::onCommonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(NeutralEntities::onClientSetup);
        }
    }
    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(NeutralEntities.INFLICTED_BOAR.get(), InflictedBoarEntity.createAttributes().build());
        event.put(NeutralEntities.INFLICTED_WOLF.get(), InflictedBoarEntity.createAttributes().build());
        event.put(NeutralEntities.AMMO_GOBLIN.get(), AmmoGoblinEntity.createAttributes().build());
        event.put(NeutralEntities.BIG_LUMP.get(), BigLumpEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        BoundingBoxManager.registerHeadshotBox(NeutralEntities.INFLICTED_BOAR.get(), new OffsetRotatedHeadshotBox<>(
            8, 
            9.5, 
            15.0, 
            0.0F, 
            8.0, 
            false, 
            true
        ));
        BoundingBoxManager.registerHeadshotBox(NeutralEntities.INFLICTED_WOLF.get(), new OffsetRotatedHeadshotBox<>(
            8, 
            9.5, 
            15.0, 
            0.0F, 
            10.0, 
            false, 
            true
        ));
        BoundingBoxManager.registerHeadshotBox(NeutralEntities.AMMO_GOBLIN.get(), new OffsetRotatedHeadshotBox<>(
            8, 
            9.5, 
            15.0, 
            0.0F, 
            5.0, 
            false, 
            true
        ));
        BoundingBoxManager.registerHeadshotBox(NeutralEntities.BIG_LUMP.get(), new OffsetRotatedHeadshotBox<>(
            8, 
            9.5, 
            2.0, 
            0.0F, 
            15.0, 
            false, 
            true
        ));
    }
    @OnlyIn(value = Dist.CLIENT)
    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(NeutralEntities.INFLICTED_BOAR.get(), InflictedBoarRenderer::new);
        EntityRenderers.register(NeutralEntities.INFLICTED_WOLF.get(), InflictedWolfRenderer::new);
        EntityRenderers.register(NeutralEntities.AMMO_GOBLIN.get(), AmmoGoblinRenderer::new);
        EntityRenderers.register(NeutralEntities.BIG_LUMP.get(), BigLumpRenderer::new);
    }
}
