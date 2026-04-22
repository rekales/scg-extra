package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.asgharian.candlefiend.CandleFiendEntity;
import net.zincstudios.scgextra.entity.asgharian.candlefiend.CandleFiendRenderer;
import net.zincstudios.scgextra.entity.asgharian.failedone.FailedOneEntity;
import net.zincstudios.scgextra.entity.asgharian.flamer.AsgharFlamerEntity;
import net.zincstudios.scgextra.entity.asgharian.surgeon.AsgharSurgeonEntity;
import net.zincstudios.scgextra.entity.asgharian.surgeon.AsgharSurgeonRenderer;
import net.zincstudios.scgextra.entity.asgharian.worker.AsgharWorkerEntity;
import net.zincstudios.scgextra.entity.asgharian.worker.AsgharWorkerRenderer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

public class AsgharianEntities {

    public static final RegistryObject<EntityType<FailedOneEntity>> FAILED_ONE = ENTITY_TYPES
            .register("failed_one", () -> EntityType.Builder.of(FailedOneEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("failed_one"));

    public static final RegistryObject<EntityType<AsgharSurgeonEntity>> ASGHAR_SURGEON = ENTITY_TYPES
            .register("asghar_surgeon", () -> EntityType.Builder.of(AsgharSurgeonEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.3F)
                    .build("asghar_surgeon"));

    public static final RegistryObject<EntityType<AsgharWorkerEntity>> ASGHAR_WORKER = ENTITY_TYPES
            .register("asghar_worker", () -> EntityType.Builder.of(AsgharWorkerEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.1F)
                    .build("asghar_worker"));

    public static final RegistryObject<EntityType<AsgharFlamerEntity>> ASGHAR_FLAMER = ENTITY_TYPES
            .register("asghar_flamer", () -> EntityType.Builder.of(AsgharFlamerEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 2.1F)
                    .build("asghar_flamer"));

    public static final RegistryObject<EntityType<CandleFiendEntity>> CANDLE_FIEND = ENTITY_TYPES
            .register("candle_fiend", () -> EntityType.Builder.of(CandleFiendEntity::new, MobCategory.MONSTER)
                    .sized(1.55F, 3.2F)
                    .setUpdateInterval(1)
                    .build("candle_fiend"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(AsgharianEntities::registerAttributes);
        modEventBus.addListener(AsgharianEntities::onCommonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(AsgharianEntities::onClientSetup);
        }
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(AsgharianEntities.FAILED_ONE.get(), (ctx) -> new EquippedRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/failed_one")), 0).noDeathTilt());
        EntityRenderers.register(AsgharianEntities.ASGHAR_SURGEON.get(), (ctx) -> new AsgharSurgeonRenderer<>(ctx).noDeathTilt());
        EntityRenderers.register(AsgharianEntities.ASGHAR_WORKER.get(), AsgharWorkerRenderer::new);
        EntityRenderers.register(AsgharianEntities.ASGHAR_FLAMER.get(), (ctx) -> new EquippedRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/asghar_flamer")), 10).noDeathTilt());
        EntityRenderers.register(AsgharianEntities.CANDLE_FIEND.get(), CandleFiendRenderer::new);

    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(AsgharianEntities.FAILED_ONE.get(), FailedOneEntity.createAttributes().build());
        event.put(AsgharianEntities.ASGHAR_SURGEON.get(), AsgharSurgeonEntity.createAttributes().build());
        event.put(AsgharianEntities.ASGHAR_WORKER.get(), AsgharWorkerEntity.createAttributes().build());
        event.put(AsgharianEntities.ASGHAR_FLAMER.get(), AsgharFlamerEntity.createAttributes().build());
        event.put(AsgharianEntities.CANDLE_FIEND.get(), CandleFiendEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
    }
}
