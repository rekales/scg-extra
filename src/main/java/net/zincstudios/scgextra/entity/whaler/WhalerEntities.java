package net.zincstudios.scgextra.entity.whaler;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleEntity;
import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkEntity;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueEntity;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusEntity;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.turtleman.TurtlemanEntity;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.common.headshot.BasicHeadshotBox;

import static net.zincstudios.scgextra.entity.ModEntities.ENTITY_TYPES;

public class WhalerEntities {

    public static final RegistryObject<EntityType<FishFolkEntity>> FISH_FOLK = ENTITY_TYPES
            .register("fish_folk", () -> EntityType.Builder.of(FishFolkEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("fish_folk"));

    public static final RegistryObject<EntityType<TurtlemanEntity>> TURTLEMAN = ENTITY_TYPES
            .register("turtleman", () -> EntityType.Builder.of(TurtlemanEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(1F, 2.3F)
                    .build("turtleman"));

    public static final RegistryObject<EntityType<SalmonsaurEntity>> SALMONSAUR = ENTITY_TYPES
            .register("salmonsaur", () -> EntityType.Builder.of(SalmonsaurEntity::new, MobCategory.MONSTER)
                    .sized(2F, 2.7F).build("salmonsaur"));

    public static final RegistryObject<EntityType<GuardianStatueEntity>> GUARDIAN_STATUE = ENTITY_TYPES
            .register("guardian_statue", () -> EntityType.Builder.of(GuardianStatueEntity::new, MobCategory.MONSTER)
                    .setUpdateInterval(1)
                    .sized(2F, 6.75F)
                    .build("guardian_statue"));

    public static final RegistryObject<EntityType<TentacliatorEntity>> TENTACLIATOR = ENTITY_TYPES
            .register("tentacliator", () -> EntityType.Builder.of(TentacliatorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("tentacliator"));

    public static final RegistryObject<EntityType<GlowingTentacliatorEntity>> GLOWING_TENTACLIATOR = ENTITY_TYPES
            .register("glowing_tentacliator", () -> EntityType.Builder.of(GlowingTentacliatorEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("glowing_tentacliator"));

    public static final RegistryObject<EntityType<PufficusEntity>> PUFFICUS = ENTITY_TYPES
            .register("pufficus", () -> EntityType.Builder.of(PufficusEntity::new, MobCategory.MONSTER)
                    .sized(1F, 3F).build("pufficus"));

    public static final RegistryObject<EntityType<ArmoredWhaleEntity>> ARMORED_WHALE = ENTITY_TYPES
            .register("armored_whale", () -> EntityType.Builder.of(ArmoredWhaleEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(5.5F, 6F)
                    .build("armored_whale"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(WhalerEntities::registerAttributes);
        modEventBus.addListener(WhalerEntities::onCommonSetup);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(WhalerEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(WhalerEntities.TURTLEMAN.get(), TurtlemanEntity.createAttributes().build());
        event.put(WhalerEntities.SALMONSAUR.get(), SalmonsaurEntity.createAttributes().build());
        event.put(WhalerEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
        event.put(WhalerEntities.TENTACLIATOR.get(), TentacliatorEntity.createAttributes().build());
        event.put(WhalerEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorEntity.createAttributes().build());
        event.put(WhalerEntities.PUFFICUS.get(), PufficusEntity.createAttributes().build());
        event.put(WhalerEntities.ARMORED_WHALE.get(), ArmoredWhaleEntity.createAttributes().build());
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        BoundingBoxManager.registerHeadshotBox(WhalerEntities.TURTLEMAN.get(), new BasicHeadshotBox<>(11.0, 28.0));
    }

}
