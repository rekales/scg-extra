package net.zincstudios.scgextra.entity.cog;

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
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.asgharian.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.cog.bombardier.CogBombardierEntity;
import net.zincstudios.scgextra.entity.cog.centipede.CogCentipedeEntity;
import net.zincstudios.scgextra.entity.cog.devastator.CogDevastatorEntity;
import net.zincstudios.scgextra.entity.cog.gigantes.CogGigantesEntity;
import net.zincstudios.scgextra.entity.cog.juggernaut.CogJuggernautEntity;
import net.zincstudios.scgextra.entity.cog.venator.CogVenatorEntity;
import net.zincstudios.scgextra.entity.cog.vulture.CogVultureEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

public class COGEntities {

    public static final RegistryObject<EntityType<CogVultureEntity>> VULTURE = ENTITY_TYPES
            .register("cog_vulture", () -> EntityType.Builder.of(CogVultureEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 1.7F)
                    .build("cog_vulture"));

    public static final RegistryObject<EntityType<CogDevastatorEntity>> DEVASTATOR = ENTITY_TYPES
            .register("cog_devastator", () -> EntityType.Builder.of(CogDevastatorEntity::new, MobCategory.MONSTER)
                    .sized(1.7F, 3.25F)
                    .build("cog_devastator"));

    public static final RegistryObject<EntityType<CogBombardierEntity>> BOMBARDIER = ENTITY_TYPES
            .register("cog_bombardier", () -> EntityType.Builder.of(CogBombardierEntity::new, MobCategory.MONSTER)
                    .sized(1.6F, 3.1F)
                    .build("cog_bombardier"));

    public static final RegistryObject<EntityType<CogGigantesEntity>> GIGANTES = ENTITY_TYPES
            .register("cog_gigantes", () -> EntityType.Builder.of(CogGigantesEntity::new, MobCategory.MONSTER)
                    .sized(1.8F, 3.4F)
                    .build("cog_gigantes"));

    public static final RegistryObject<EntityType<CogVenatorEntity>> VENATOR = ENTITY_TYPES
            .register("cog_venator", () -> EntityType.Builder.of(CogVenatorEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 1.5F)
                    .build("cog_venator"));

    public static final RegistryObject<EntityType<CogCentipedeEntity>> CENTIPEDE = ENTITY_TYPES
            .register("cog_centipede", () -> EntityType.Builder.of(CogCentipedeEntity::new, MobCategory.MONSTER)
                    .sized(2F, 3F)
                    .build("cog_centipede"));

    public static final RegistryObject<EntityType<CogJuggernautEntity>> JUGGERNAUT = ENTITY_TYPES
            .register("cog_juggernaut", () -> EntityType.Builder.of(CogJuggernautEntity::new, MobCategory.MONSTER)
                    .sized(1.6F, 3.0F)
                    .build("cog_juggernaut"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(COGEntities::registerAttributes);
        modEventBus.addListener(COGEntities::onCommonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(COGEntities::onClientSetup);
        }
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(COGEntities.VULTURE.get(), CogVultureEntity.createAttributes().build());
        event.put(COGEntities.DEVASTATOR.get(), CogDevastatorEntity.createAttributes().build());
        event.put(COGEntities.BOMBARDIER.get(), CogBombardierEntity.createAttributes().build());
        event.put(COGEntities.GIGANTES.get(), CogGigantesEntity.createAttributes().build());
        event.put(COGEntities.VENATOR.get(), CogVenatorEntity.createAttributes().build());
        event.put(COGEntities.CENTIPEDE.get(), CogCentipedeEntity.createAttributes().build());
        event.put(COGEntities.JUGGERNAUT.get(), CogJuggernautEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
//        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_TRENCHER.get(), new BasicHeadshotBox<>(9.0, 20.0));
    }

    @OnlyIn(value = Dist.CLIENT)
    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(COGEntities.VULTURE.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_vulture"))).noDeathTilt());
        EntityRenderers.register(COGEntities.DEVASTATOR.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_devastator"), "chest")).noDeathTilt());
        EntityRenderers.register(COGEntities.BOMBARDIER.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_bombardier"))).noDeathTilt());
        EntityRenderers.register(COGEntities.GIGANTES.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_gigantes"))).noDeathTilt());
        EntityRenderers.register(COGEntities.VENATOR.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_venator"))).noDeathTilt());
        EntityRenderers.register(COGEntities.CENTIPEDE.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_centipede"))).noDeathTilt());
        EntityRenderers.register(COGEntities.JUGGERNAUT.get(), (ctx) -> new BaseEntityRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("cog/cog_juggernaut"))).noDeathTilt());
    }

}
