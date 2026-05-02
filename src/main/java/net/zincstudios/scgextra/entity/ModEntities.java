package net.zincstudios.scgextra.entity;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.asgharian.AsgharianEntities;
import net.zincstudios.scgextra.entity.common.raid_summoner.RaidSummonerEntity;
import net.zincstudios.scgextra.entity.fac.FACEntities;
import net.zincstudios.scgextra.entity.neutral.ammo_goblin.AmmoGoblinEntity;
import net.zincstudios.scgextra.entity.neutral.big_lump.BigLumpEntity;
import net.zincstudios.scgextra.entity.neutral.end_dweller.EndDwellerEntity;
import net.zincstudios.scgextra.entity.neutral.end_pod.EndPodEntity;
import net.zincstudios.scgextra.entity.neutral.end_scorpion.EndScorpionEntity;
import net.zincstudios.scgextra.entity.neutral.end_stone_crab.EndStoneCrabEntity;
import net.zincstudios.scgextra.entity.neutral.head_hunter.HeadHunterEntity;
import net.zincstudios.scgextra.entity.neutral.inflicted_boar.InflictedBoarEntity;
import net.zincstudios.scgextra.entity.neutral.inflicted_wolf.InflictedWolfEntity;
import net.zincstudios.scgextra.entity.neutral.mutant_bat.MutantBatEntity;
import net.zincstudios.scgextra.entity.neutral.netherite_eater.NetheriteEaterEntity;
import net.zincstudios.scgextra.entity.neutral.nitro_beetle.NitroBeetleEntity;
import net.zincstudios.scgextra.entity.projectile.SoulFireball;
import net.zincstudios.scgextra.entity.projectile.net.NetEntity;
import net.zincstudios.scgextra.entity.rrc.RRCEntities;
import net.zincstudios.scgextra.entity.whaler.WhalerEntities;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.entity.projectile.FireProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

    public static final RegistryObject<EntityType<RaidSummonerEntity>> RAID_SUMMONER = ENTITY_TYPES
            .register("raid_summoner", () -> EntityType.Builder.of(RaidSummonerEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(0.5F, 0.5F)
                    .build("raid_summoner"));

    public static final RegistryObject<EntityType<NetEntity>> NET = ENTITY_TYPES
            .register("net", () -> EntityType.Builder.<NetEntity>of(NetEntity::new, MobCategory.MISC).sized(4F, 1F).clientTrackingRange(4).updateInterval(20).build("net"));

    // Copied from SCGuns ModEntities.ENEMY_PROJECTILE
    public static final RegistryObject<EntityType<ArmoredWhaleProjectileEntity>> WHALE_PROJECTILE = ENTITY_TYPES
            .register("whale_tank_projectile", () -> EntityType.Builder.of(ArmoredWhaleProjectileEntity::create, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("whale_tank_projectile"));

    public static final RegistryObject<EntityType<FireProjectile>> FIRE_PROJECTILE = ENTITY_TYPES
            .register("fire_projectile", () -> EntityType.Builder.of(FireProjectile::create, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("fire_projectile"));

    public static final RegistryObject<EntityType<SoulFireball>> LARGE_SOUL_FIREBALL = ENTITY_TYPES
            .register("soul_fireball", () -> EntityType.Builder.of(
                    (EntityType<SoulFireball> type, Level level) -> new SoulFireball(type, level), MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(3)
                    .build("soul_fireball"));

    public static final RegistryObject<EntityType<InflictedBoarEntity>> INFLICTED_BOAR = ENTITY_TYPES
            .register("inflicted_boar", () -> EntityType.Builder.of(InflictedBoarEntity::new, MobCategory.MONSTER)
                    .sized(1.7F, 1.7F)
                    .build("inflicted_boar"));
    public static final RegistryObject<EntityType<InflictedWolfEntity>> INFLICTED_WOLF = ENTITY_TYPES
            .register("inflicted_wolf", () -> EntityType.Builder.of(InflictedWolfEntity::new, MobCategory.MONSTER)
                    .sized(1.8F, 1.8F)
                    .build("inflicted_wolf"));
    public static final RegistryObject<EntityType<AmmoGoblinEntity>> AMMO_GOBLIN = ENTITY_TYPES
            .register("ammo_goblin", () -> EntityType.Builder.of(AmmoGoblinEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 1.5F)
                    .build("ammo_goblin"));
    public static final RegistryObject<EntityType<BigLumpEntity>> BIG_LUMP = ENTITY_TYPES
            .register("big_lump", () -> EntityType.Builder.of(BigLumpEntity::new, MobCategory.MONSTER)
                    .sized(3F, 3.5F)
                    .build("big_lump"));
    public static final RegistryObject<EntityType<MutantBatEntity>> MUTANT_BAT = ENTITY_TYPES
            .register("mutant_bat", () -> EntityType.Builder.of(MutantBatEntity::new, MobCategory.MONSTER)
                    .sized(2F, 2.2F)
                    .setUpdateInterval(1)
                    .build("mutant_bat"));
    public static final RegistryObject<EntityType<NitroBeetleEntity>> NITRO_BEETLE = ENTITY_TYPES
            .register("nitro_beetle", () -> EntityType.Builder.of(NitroBeetleEntity::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.9F, 0.9F)
                    .setUpdateInterval(1)
                    .build("nitro_beetle"));
    public static final RegistryObject<EntityType<HeadHunterEntity>> HEAD_HUNTER = ENTITY_TYPES
            .register("head_hunter", () -> EntityType.Builder.of(HeadHunterEntity::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.1F, 2.8F)
                    .build("head_hunter"));
    public static final RegistryObject<EntityType<NetheriteEaterEntity>> NETHERITE_EATER = ENTITY_TYPES
            .register("netherite_eater", () -> EntityType.Builder.of(NetheriteEaterEntity::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(1.85F, 2.8F)
                    .build("netherite_eater"));
    public static final RegistryObject<EntityType<EndPodEntity>> END_POD = ENTITY_TYPES
            .register("end_pod", () -> EntityType.Builder.of(EndPodEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.2F)
                    .build("end_pod"));
    public static final RegistryObject<EntityType<EndDwellerEntity>> END_DWELLER = ENTITY_TYPES
            .register("end_dweller", () -> EntityType.Builder.of(EndDwellerEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 1.5F)
                    .setUpdateInterval(1)
                    .build("end_dweller"));
    public static final RegistryObject<EntityType<EndStoneCrabEntity>> END_STONE_CRAB = ENTITY_TYPES
            .register("end_stone_crab", () -> EntityType.Builder.of(EndStoneCrabEntity::new, MobCategory.MONSTER)
                    .sized(3.3F, 3F)
                    .build("end_stone_crab"));
    public static final RegistryObject<EntityType<EndScorpionEntity>> END_SCORPION = ENTITY_TYPES
            .register("end_scorpion", () -> EntityType.Builder.of(EndScorpionEntity::new, MobCategory.MONSTER)
                    .sized(2.5F, 0.5F)
                    .setUpdateInterval(1)
                    .build("end_scorpion"));

    public static void register(IEventBus modEventBus) {
        WhalerEntities.register(modEventBus);
        RRCEntities.register(modEventBus);
        FACEntities.register(modEventBus);
        AsgharianEntities.register(modEventBus);

        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(ModEntities::registerAttributes);
        MinecraftForge.EVENT_BUS.addListener(EntityAdjustments::onEntityJoin);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.RAID_SUMMONER.get(), RaidSummonerEntity.createAttributes().build());
        event.put(ModEntities.INFLICTED_BOAR.get(), InflictedBoarEntity.createAttributes().build());
        event.put(ModEntities.INFLICTED_WOLF.get(), InflictedWolfEntity.createAttributes().build());
        event.put(ModEntities.AMMO_GOBLIN.get(), AmmoGoblinEntity.createAttributes().build());
        event.put(ModEntities.BIG_LUMP.get(), BigLumpEntity.createAttributes().build());
        event.put(ModEntities.MUTANT_BAT.get(), MutantBatEntity.createAttributes().build());
        event.put(ModEntities.NITRO_BEETLE.get(), NitroBeetleEntity.createAttributes().build());
        event.put(ModEntities.HEAD_HUNTER.get(), HeadHunterEntity.createAttributes().build());
        event.put(ModEntities.NETHERITE_EATER.get(), NetheriteEaterEntity.createAttributes().build());
        event.put(ModEntities.END_POD.get(), EndPodEntity.createAttributes().build());
        event.put(ModEntities.END_DWELLER.get(), EndDwellerEntity.createAttributes().build());
        event.put(ModEntities.END_STONE_CRAB.get(), EndStoneCrabEntity.createAttributes().build());
        event.put(ModEntities.END_SCORPION.get(), EndScorpionEntity.createAttributes().build());
    }
}
