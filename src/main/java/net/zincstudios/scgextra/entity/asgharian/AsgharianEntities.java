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
import net.zincstudios.scgextra.entity.asgharian.failedone.FailedOneEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

public class AsgharianEntities {

    public static final RegistryObject<EntityType<FailedOneEntity>> FAILED_ONE = ENTITY_TYPES
            .register("failed_one", () -> EntityType.Builder.of(FailedOneEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("failed_one"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(AsgharianEntities::registerAttributes);
        modEventBus.addListener(AsgharianEntities::onCommonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(AsgharianEntities::onClientSetup);
        }
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(AsgharianEntities.FAILED_ONE.get(), (ctx) -> new EquippedRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("asgharian/failed_one")), 90).noDeathTilt());
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(AsgharianEntities.FAILED_ONE.get(), FailedOneEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {

    }
}
