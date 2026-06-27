package net.zincstudios.scgextra.entity.fac;

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
import net.zincstudios.scgextra.entity.common.OffsetRotatedHeadshotBox;
import net.zincstudios.scgextra.entity.common.WeakPointBox;
import net.zincstudios.scgextra.entity.common.WeakPointBoxManager;
import net.zincstudios.scgextra.entity.common.client.GunHoldingMobRenderer;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import net.zincstudios.scgextra.entity.common.client.ItemHoldingMobRenderer;
import net.zincstudios.scgextra.entity.fac.fac_bluecoat.FacBluecoatEntity;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarEntity;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarRenderer;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionEntity;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankEntity;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankRenderer;
import net.zincstudios.scgextra.entity.fac.fac_tank_buster.FacTankBusterEntity;
import net.zincstudios.scgextra.entity.fac.fac_tank_buster.FacTankBusterRenderer;
import net.zincstudios.scgextra.entity.fac.fac_trencher.FacTrencherEntity;
import net.zincstudios.scgextra.entity.fac.fac_walker.FacWalkerEntity;
import net.zincstudios.scgextra.entity.fac.shovel_knight.ShovelKnightEntity;
import net.zincstudios.scgextra.entity.fac.trench_goblin.TrenchGoblinEntity;
import net.zincstudios.scgextra.entity.fac.trench_sniper.TrenchSniperEntity;
import net.zincstudios.scgextra.entity.fac.trench_sniper.TrenchSniperRenderer;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.common.headshot.BasicHeadshotBox;
import top.ribs.scguns.common.headshot.RotatedHeadshotBox;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

public class FACEntities {

    public static final RegistryObject<EntityType<FacTrencherEntity>> FAC_TRENCHER = ENTITY_TYPES
            .register("fac_trencher", () -> EntityType.Builder.of(FacTrencherEntity::new, MobCategory.MONSTER)
                    .sized(0.68F, 1.82F)
                    .build("fac_trencher"));

    public static final RegistryObject<EntityType<FacBluecoatEntity>> FAC_BLUECOAT = ENTITY_TYPES
            .register("fac_bluecoat", () -> EntityType.Builder.of(FacBluecoatEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("fac_bluecoat"));

    public static final RegistryObject<EntityType<TrenchGoblinEntity>> TRENCH_GOBLIN = ENTITY_TYPES
            .register("trench_goblin", () -> EntityType.Builder.of(TrenchGoblinEntity::new, MobCategory.MONSTER)
                    .sized(0.72F, 1.72F)
                    .build("trench_goblin"));

    public static final RegistryObject<EntityType<TrenchSniperEntity>> TRENCH_SNIPER = ENTITY_TYPES
            .register("trench_sniper", () -> EntityType.Builder.of(TrenchSniperEntity::new, MobCategory.MONSTER)
                    .sized(0.72F, 2.22F)
                    .build("trench_sniper"));

    public static final RegistryObject<EntityType<ShovelKnightEntity>> SHOVEL_KNIGHT = ENTITY_TYPES
            .register("shovel_knight", () -> EntityType.Builder.of(ShovelKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.78F, 2.08F)
                    .build("shovel_knight"));

    public static final RegistryObject<EntityType<FacTankBusterEntity>> FAC_TANK_BUSTER = ENTITY_TYPES
            .register("fac_tank_buster", () -> EntityType.Builder.of(FacTankBusterEntity::new, MobCategory.MONSTER)
                    .sized(1.02F, 2.02F)
                    .build("fac_tank_buster"));

    public static final RegistryObject<EntityType<FacLionEntity>> FAC_LION = ENTITY_TYPES
            .register("fac_lion", () -> EntityType.Builder.of(FacLionEntity::new, MobCategory.MONSTER)
                    .sized(1.3F, 2.9F)
                    .build("fac_lion"));

    public static final RegistryObject<EntityType<FacCommissarEntity>> FAC_COMMISSAR = ENTITY_TYPES
            .register("fac_commissar", () -> EntityType.Builder.of(FacCommissarEntity::new, MobCategory.MONSTER)
                    .sized(0.96F, 2.22F)
                    .build("fac_commissar"));

    public static final RegistryObject<EntityType<FacWalkerEntity>> FAC_WALKER = ENTITY_TYPES
            .register("fac_walker", () -> EntityType.Builder.of(FacWalkerEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(1.4F, 3.85F)
                    .build("fac_walker"));

    public static final RegistryObject<EntityType<FacTankEntity>> FAC_TANK = ENTITY_TYPES
            .register("fac_tank", () -> EntityType.Builder.of(FacTankEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(2.5F, 3.7F)
                    .build("fac_tank"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(FACEntities::registerAttributes);
        modEventBus.addListener(FACEntities::onCommonSetup);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(FACEntities::onClientSetup);
        }
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FACEntities.FAC_TRENCHER.get(), FacTrencherEntity.createAttributes().build());
        event.put(FACEntities.FAC_BLUECOAT.get(), FacBluecoatEntity.createAttributes().build());
        event.put(FACEntities.TRENCH_GOBLIN.get(), TrenchGoblinEntity.createAttributes().build());
        event.put(FACEntities.TRENCH_SNIPER.get(), TrenchSniperEntity.createAttributes().build());
        event.put(FACEntities.SHOVEL_KNIGHT.get(), ShovelKnightEntity.createAttributes().build());
        event.put(FACEntities.FAC_TANK_BUSTER.get(), FacTankBusterEntity.createAttributes().build());
        event.put(FACEntities.FAC_LION.get(), FacLionEntity.createAttributes().build());
        event.put(FACEntities.FAC_COMMISSAR.get(), FacCommissarEntity.createAttributes().build());
        event.put(FACEntities.FAC_WALKER.get(), FacWalkerEntity.createAttributes().build());
        event.put(FACEntities.FAC_TANK.get(), FacTankEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_TRENCHER.get(), new BasicHeadshotBox<>(9.0, 20.0));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_BLUECOAT.get(), new BasicHeadshotBox<>(9.0, 22.0));
        BoundingBoxManager.registerHeadshotBox(FACEntities.TRENCH_GOBLIN.get(), new OffsetRotatedHeadshotBox<>(8.0, 7.0, 21.0, 0.5F, 5.0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.TRENCH_SNIPER.get(), new OffsetRotatedHeadshotBox<>(9.0, 9.0, 30.0, 0.0F, 2.0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.SHOVEL_KNIGHT.get(), new BasicHeadshotBox<>(9.0, 24.0));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_COMMISSAR.get(), new OffsetRotatedHeadshotBox<>(10.0, 18.0, 30.0, 0.0F, 0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_TANK_BUSTER.get(), new OffsetRotatedHeadshotBox<>(10.0, 9.0, 24.0, 0.0F, 2.0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_LION.get(), new RotatedHeadshotBox<>(10, 36.0, 7.0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_WALKER.get(), new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true));
        BoundingBoxManager.registerHeadshotBox(FACEntities.FAC_TANK.get(), new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true));

        WeakPointBoxManager.registerWeakPointBox(FACEntities.FAC_WALKER.get(), new WeakPointBox<>(new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true)));
        WeakPointBoxManager.registerWeakPointBox(FACEntities.FAC_TANK.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true)));
    }

    @OnlyIn(value = Dist.CLIENT)
    private static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(FACEntities.FAC_TRENCHER.get(), (ctx) -> new GunHoldingMobRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_trencher"))));
        EntityRenderers.register(FACEntities.FAC_BLUECOAT.get(), (ctx) -> new GunHoldingMobRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_bluecoat"))));
        EntityRenderers.register(FACEntities.TRENCH_GOBLIN.get(), (ctx) -> new ItemHoldingMobRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_trench_goblin"))));
        EntityRenderers.register(FACEntities.TRENCH_SNIPER.get(), (ctx) -> new TrenchSniperRenderer(ctx).noDeathTilt());

        EntityRenderers.register(FACEntities.SHOVEL_KNIGHT.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_shovel_knight")), -10).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_TANK_BUSTER.get(), (ctx) -> new FacTankBusterRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_LION.get(), (ctx) -> new FacLionRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_COMMISSAR.get(), (ctx) -> new FacCommissarRenderer(ctx).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_WALKER.get(), (ctx) -> new GunnerRenderer<>(ctx,
                new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_walker")), -10).noDeathTilt());
        EntityRenderers.register(FACEntities.FAC_TANK.get(), FacTankRenderer::new);
    }
}
