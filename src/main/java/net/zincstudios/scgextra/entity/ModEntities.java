package net.zincstudios.scgextra.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.OffsetRotatedHeadshotBox;
import net.zincstudios.scgextra.entity.common.WeakPointBox;
import net.zincstudios.scgextra.entity.common.WeakPointBoxManager;
import net.zincstudios.scgextra.entity.common.raid_summoner.RaidSummonerEntity;
import net.zincstudios.scgextra.entity.fac.fac_tank_buster.FacTankBusterEntity;
import net.zincstudios.scgextra.entity.fac.fac_bluecoat.FacBluecoatEntity;
import net.zincstudios.scgextra.entity.fac.fac_commissar.FacCommissarEntity;
import net.zincstudios.scgextra.entity.fac.fac_lion.FacLionEntity;
import net.zincstudios.scgextra.entity.fac.fac_tank.FacTankEntity;
import net.zincstudios.scgextra.entity.fac.fac_trencher.FacTrencherEntity;
import net.zincstudios.scgextra.entity.fac.fac_walker.FacWalkerEntity;
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
import net.zincstudios.scgextra.entity.fac.shovel_knight.ShovelKnightEntity;
import net.zincstudios.scgextra.entity.fac.trench_sniper.TrenchSniperEntity;
import net.zincstudios.scgextra.entity.fac.trench_goblin.TrenchGoblinEntity;
import net.zincstudios.scgextra.entity.projectile.net.NetEntity;
import net.zincstudios.scgextra.entity.rrc.flaminghead.FlamingHeadEntity;
import net.zincstudios.scgextra.entity.rrc.oppressor.OppressorEntity;
import net.zincstudios.scgextra.entity.rrc.scrapguard.ScrapGuardEntity;
import net.zincstudios.scgextra.entity.rrc.tallman.TallmanEntity;
import net.zincstudios.scgextra.entity.whaler.armoredwhale.ArmoredWhaleEntity;
import net.zincstudios.scgextra.entity.whaler.fishfolk.FishFolkEntity;
import net.zincstudios.scgextra.entity.rrc.drone.DroneEntity;
import net.zincstudios.scgextra.entity.rrc.scout.ScoutEntity;
import net.zincstudios.scgextra.entity.rrc.spring_junkie.SpringJunkieEntity;
import net.zincstudios.scgextra.entity.rrc.arc_psycho.ArcPsychoEntity;
import net.zincstudios.scgextra.entity.rrc.copper_knight.CopperKnightEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.GlowingTentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.guardian_statue.GuardianStatueEntity;
import net.zincstudios.scgextra.entity.projectile.ArmoredWhaleProjectileEntity;
import net.zincstudios.scgextra.entity.projectile.FireProjectile;
import net.zincstudios.scgextra.entity.whaler.pufficus.PufficusEntity;
import net.zincstudios.scgextra.entity.whaler.salmonsaur.SalmonsaurEntity;
import net.zincstudios.scgextra.entity.whaler.tentacliator.TentacliatorEntity;
import net.zincstudios.scgextra.entity.whaler.turtleman.TurtlemanEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.common.headshot.BasicHeadshotBox;
import top.ribs.scguns.common.headshot.RotatedHeadshotBox;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SCGExtra.MOD_ID);

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
                    .sized(5.5F, 6F)//i think we should use child elements like enderdragon but not sure
                    .build("armored_whale"));

    public static final RegistryObject<EntityType<RaidSummonerEntity>> RAID_SUMMONER = ENTITY_TYPES
            .register("raid_summoner", () -> EntityType.Builder.of(RaidSummonerEntity::new, MobCategory.MONSTER)
                    .updateInterval(1)
                    .sized(0.5F, 0.5F)
                    .build("raid_summoner"));

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
                    
    //RRC
    public static final RegistryObject<EntityType<DroneEntity>> DRONE = ENTITY_TYPES
            .register("drone", () -> EntityType.Builder.of(DroneEntity::new, MobCategory.MONSTER)
                    .sized(2.7F, 3.9F).build("drone"));

    public static final RegistryObject<EntityType<TallmanEntity>> TALLMAN = ENTITY_TYPES
            .register("tallman", () -> EntityType.Builder.of(TallmanEntity::new, MobCategory.MONSTER)
                    .sized(1F, 4.5F)
                    .build("tallman"));

    public static final RegistryObject<EntityType<ScoutEntity>> SCOUT = ENTITY_TYPES
            .register("scout", () -> EntityType.Builder.of(ScoutEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build("scout"));

    public static final RegistryObject<EntityType<OppressorEntity>> OPPRESSOR = ENTITY_TYPES
            .register("oppressor", () -> EntityType.Builder.of(OppressorEntity::new, MobCategory.MONSTER)
                    .sized(2F, 4F)
                    .build("oppressor"));

    public static final RegistryObject<EntityType<SpringJunkieEntity>> SPRING_JUNKIE = ENTITY_TYPES
            .register("spring_junkie", () -> EntityType.Builder.of(SpringJunkieEntity::new, MobCategory.MONSTER)
                    .sized(1.30F, 3.5F)
                    .build("spring_junkie"));

    public static final RegistryObject<EntityType<FlamingHeadEntity>> FLAMING_HEAD = ENTITY_TYPES
            .register("flaming_head", () -> EntityType.Builder.of(FlamingHeadEntity::new, MobCategory.MONSTER)
                    .sized(2.6F, 5F)
                    .setUpdateInterval(1)
                    .build("flaming_head"));

    public static final RegistryObject<EntityType<ScrapGuardEntity>> SCRAP_GUARD = ENTITY_TYPES
            .register("scrap_guard", () -> EntityType.Builder.of(ScrapGuardEntity::new, MobCategory.MONSTER)
                    .sized(1.17F, 3F)
                    .build("scrap_guard"));

    public static final RegistryObject<EntityType<ArcPsychoEntity>> ARC_PSYCHO = ENTITY_TYPES
            .register("arc_psycho", () -> EntityType.Builder.of(ArcPsychoEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2F)
                    .build("arc_psycho"));

    public static final RegistryObject<EntityType<CopperKnightEntity>> COPPER_KNIGHT = ENTITY_TYPES
            .register("copper_knight", () -> EntityType.Builder.of(CopperKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2F)
                    .build("copper_knight"));

    public static final RegistryObject<EntityType<FireProjectile>> FIRE_PROJECTILE = ENTITY_TYPES
            .register("fire_projectile", () -> EntityType.Builder.of(FireProjectile::create, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .build("fire_projectile"));

    //neutral
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


    public static void register(IEventBus modEventBus){
        ENTITY_TYPES.register(modEventBus);

        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModEntities::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(EntityAdjustments::onEntityJoin);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FISH_FOLK.get(), FishFolkEntity.createAttributes().build());
        event.put(ModEntities.TURTLEMAN.get(), TurtlemanEntity.createAttributes().build());
        event.put(ModEntities.SALMONSAUR.get(), SalmonsaurEntity.createAttributes().build());
        event.put(ModEntities.GUARDIAN_STATUE.get(), GuardianStatueEntity.createAttributes().build());
        event.put(ModEntities.TENTACLIATOR.get(), TentacliatorEntity.createAttributes().build());
        event.put(ModEntities.GLOWING_TENTACLIATOR.get(), GlowingTentacliatorEntity.createAttributes().build());
        event.put(ModEntities.PUFFICUS.get(), PufficusEntity.createAttributes().build());
        event.put(ModEntities.ARMORED_WHALE.get(), ArmoredWhaleEntity.createAttributes().build());
        event.put(ModEntities.RAID_SUMMONER.get(), RaidSummonerEntity.createAttributes().build());
        event.put(ModEntities.FAC_TRENCHER.get(), FacTrencherEntity.createAttributes().build());
        event.put(ModEntities.FAC_BLUECOAT.get(), FacBluecoatEntity.createAttributes().build());
        event.put(ModEntities.TRENCH_GOBLIN.get(), TrenchGoblinEntity.createAttributes().build());
        event.put(ModEntities.TRENCH_SNIPER.get(), TrenchSniperEntity.createAttributes().build());
        event.put(ModEntities.SHOVEL_KNIGHT.get(), ShovelKnightEntity.createAttributes().build());
        event.put(ModEntities.FAC_TANK_BUSTER.get(), FacTankBusterEntity.createAttributes().build());
        event.put(ModEntities.FAC_LION.get(), FacLionEntity.createAttributes().build());
        event.put(ModEntities.FAC_COMMISSAR.get(), FacCommissarEntity.createAttributes().build());
        event.put(ModEntities.FAC_WALKER.get(), FacWalkerEntity.createAttributes().build());
        event.put(ModEntities.FAC_TANK.get(), FacTankEntity.createAttributes().build());

        event.put(ModEntities.DRONE.get(), DroneEntity.createAttributes().build());
        event.put(ModEntities.TALLMAN.get(), TallmanEntity.createAttributes().build());
        event.put(ModEntities.SCOUT.get(), ScoutEntity.createAttributes().build());
        event.put(ModEntities.OPPRESSOR.get(), OppressorEntity.createAttributes().build());
        event.put(ModEntities.SPRING_JUNKIE.get(), SpringJunkieEntity.createAttributes().build());
        event.put(ModEntities.FLAMING_HEAD.get(), FlamingHeadEntity.createAttributes().build());
        event.put(ModEntities.SCRAP_GUARD.get(), ScrapGuardEntity.createAttributes().build());
        event.put(ModEntities.ARC_PSYCHO.get(), ArcPsychoEntity.createAttributes().build());
        event.put(ModEntities.COPPER_KNIGHT.get(), CopperKnightEntity.createAttributes().build());

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

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(
                    ModEntities.END_DWELLER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil::canSpawnEndMonster
            );
            SpawnPlacements.register(
                    ModEntities.END_STONE_CRAB.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil::canSpawnEndMonster
            );
            SpawnPlacements.register(
                    ModEntities.END_SCORPION.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil::canSpawnEndMonster
            );
            SpawnPlacements.register(
                    ModEntities.END_POD.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (type, level, spawnReason, pos, random) ->
                            spawnReason == net.minecraft.world.entity.MobSpawnType.SPAWN_EGG
                                    || spawnReason == net.minecraft.world.entity.MobSpawnType.COMMAND
                                    || net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil.canSpawnEndSurface(level, pos)
            );
            SpawnPlacements.register(
                    ModEntities.HEAD_HUNTER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.NETHERITE_EATER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.AMMO_GOBLIN.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.BIG_LUMP.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.MUTANT_BAT.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MutantBatEntity::checkMutantBatSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.NITRO_BEETLE.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.INFLICTED_BOAR.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Mob::checkMobSpawnRules
            );
            SpawnPlacements.register(
                    ModEntities.INFLICTED_WOLF.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkAnyLightMonsterSpawnRules
            );
        });

        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_TRENCHER.get(), new BasicHeadshotBox<>(9.0, 20.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_BLUECOAT.get(), new BasicHeadshotBox<>(9.0, 22.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.TRENCH_GOBLIN.get(), new OffsetRotatedHeadshotBox<>(8.0, 7.0, 21.0, 0.5F, 5.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.TRENCH_SNIPER.get(), new OffsetRotatedHeadshotBox<>(9.0, 9.0, 30.0, 0.0F, 2.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.SHOVEL_KNIGHT.get(), new BasicHeadshotBox<>(9.0, 24.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_COMMISSAR.get(), new OffsetRotatedHeadshotBox<>(10.0, 18.0, 30.0, 0.0F, 0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.TURTLEMAN.get(), new BasicHeadshotBox<>(11.0, 28.0));
        BoundingBoxManager.registerHeadshotBox(ModEntities.DRONE.get(), new RotatedHeadshotBox<>(15.0, 28.0, 20, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FLAMING_HEAD.get(), new OffsetRotatedHeadshotBox<>(10.0F, 55.0, 13, 70, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_TANK_BUSTER.get(), new OffsetRotatedHeadshotBox<>(10.0, 9.0, 24.0, 0.0F, 2.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_LION.get(), new RotatedHeadshotBox<>(10, 36.0, 7.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_WALKER.get(), new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.FAC_TANK.get(), new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.OPPRESSOR.get(), new OffsetRotatedHeadshotBox<>(10.0, 15.0, 60.0, 0.0F, 6.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.SCRAP_GUARD.get(), new OffsetRotatedHeadshotBox<>(11.0, 14.0, 40.0, 0.0F, 1.5, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.SPRING_JUNKIE.get(), new OffsetRotatedHeadshotBox<>(9.0, 20.0, 34.0, 0.0F, 0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.COPPER_KNIGHT.get(), new OffsetRotatedHeadshotBox<>(8, 9.5, 28.0, 0.0F, 2.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.INFLICTED_BOAR.get(), new OffsetRotatedHeadshotBox<>(13, 13, 10.0, 0.0F, 8.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.INFLICTED_WOLF.get(), new OffsetRotatedHeadshotBox<>(12, 12, 10.0, 0.0F, 9.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.AMMO_GOBLIN.get(), new OffsetRotatedHeadshotBox<>(8, 8, 14.0, 0.0F, 4.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.BIG_LUMP.get(), new OffsetRotatedHeadshotBox<>(12.0, 12.0, 37.0, -35.0F, 15.5, false, true, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.MUTANT_BAT.get(), new OffsetRotatedHeadshotBox<>(12, 12, 18.0, 0.0F, 6.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.NITRO_BEETLE.get(), new OffsetRotatedHeadshotBox<>(5, 5, 2.0, 0.0F, 3.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.HEAD_HUNTER.get(), new OffsetRotatedHeadshotBox<>(9, 9, 28.0, 0.0F, 3,-8, false, true, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.NETHERITE_EATER.get(), new OffsetRotatedHeadshotBox<>(15, 15, 28, 0.0F, 8.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.END_POD.get(), new OffsetRotatedHeadshotBox<>(0, 0, 0, 0F, 0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.END_DWELLER.get(), new OffsetRotatedHeadshotBox<>(10, 10, 10.0, 0.0F, 10.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.END_STONE_CRAB.get(), new OffsetRotatedHeadshotBox<>(25, 25, 10.0, 0.0F, 6.0, false, true));
        BoundingBoxManager.registerHeadshotBox(ModEntities.END_SCORPION.get(), new OffsetRotatedHeadshotBox<>(10, 7, 1.5, 0.0F, 7.0, false, true));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.FLAMING_HEAD.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(10.0F, 55.0, 13, -70, false, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.FAC_WALKER.get(), new WeakPointBox<>(new RotatedHeadshotBox<>(14.0, 44.0, 4.0, false, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.FAC_TANK.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(13.0, 16.0, 9.0, 0.0F, false, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.HEAD_HUNTER.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(9.0, 9.0, 34.0, 22.0F, 3, 2, false, true, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.BIG_LUMP.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(12.0, 12.0, 37.0, 40.0F, 18.0, false, true, true)));
        WeakPointBoxManager.registerWeakPointBox(ModEntities.BIG_LUMP.get(), new WeakPointBox<>(new OffsetRotatedHeadshotBox<>(12.0, 12.0, 37.0, -60.0F, 18.0, false, true, true)));
    }

    // TODO: registration helper
}
