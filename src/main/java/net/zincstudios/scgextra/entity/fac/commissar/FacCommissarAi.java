package net.zincstudios.scgextra.entity.fac.commissar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.ModBrainActivities;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.ModBrainSensors;
import net.zincstudios.scgextra.entity.common.brain.*;
import net.zincstudios.scgextra.entity.common.gun.IntervalTriggerSampler;
import net.zincstudios.scgextra.entity.fac.FACEntities;

public final class FacCommissarAi {
    private static final ImmutableList<? extends SensorType<? extends Sensor<? super PathfinderMob>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            ModBrainSensors.LONG_RANGE_PLAYER.get(),
            SensorType.HURT_BY,
            ModBrainSensors.HELD_GUN.get()
    );

    private static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            ModBrainMemories.AIM_TICKS.get(),
            ModBrainMemories.SIMULATED_GUN.get(),
            ModBrainMemories.WEAPON_IDEAL_RANGE.get(),
            ModBrainMemories.WEAPON_MAX_RANGE.get(),
            ModBrainMemories.ABILITY_STATE.get(),
            ModBrainMemories.TO_ALERT.get()
    );

    public static <T extends PathfinderMob> Brain<?> makeBrain(T mob, Brain<T> brain) {
        BrainCommons.initCoreActivity(brain);
        BrainCommons.initIdleActivity(brain);
        initFightActivity(mob, brain);
        BrainCommons.initAlertActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    public static <T extends PathfinderMob> Brain.Provider<T> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    public static <T extends PathfinderMob> void initFightActivity(T mob, Brain<T> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(target -> !BrainUtils.isTargetStillValidNonFriendlies(mob, target, false)),
                AttackLastHurtIfNear.create((self, target) -> !Faction.isFriendlies(self, target), false),
                MeleeAttack.create(20),
                new ApproachTargetIfCannotAim(1.0F),
                new AimWhenNotWalking(),
                new ShootTarget(20, (simGun, entity) -> 1.6F, entity -> true,
                        new IntervalTriggerSampler(20, 40, 2, 10))
        ), MemoryModuleType.ATTACK_TARGET);

        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.FIGHT, BrainUtils.createPriorityPairs(10, ImmutableList.of(
                        StopAttackingIfTargetInvalid.create(target -> !BrainUtils.isTargetStillValidNonFriendlies(mob, target, false)),
                        AttackLastHurtIfNear.create((self, target) -> !Faction.isFriendlies(self, target), false),
                        MeleeAttack.create(20),
                        new RunOneOrdered<>(ImmutableList.of(
                                new CheckShouldAlert(FacCommissarEntity.ALERT_ANIM_TICKS),
                                new FlareSummon(FacCommissarEntity.FLARE_DURATION, 80, FlareSummon.DEFAULT_COOLDOWN,
                                        FACEntities.FAC_BLUECOAT.get(),
                                        FACEntities.FAC_TRENCHER.get(),
                                        FACEntities.TRENCH_GOBLIN.get()
                                )
                        )),
                        new ApproachTargetIfCannotAim(1.0F),
                        new AimWhenTargetVisible(),
                        new ShootTarget(20, (simGun, entity) -> 1.6F, entity -> true,
                                new IntervalTriggerSampler(20, 40, 2, 7))
                )), ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                ), ImmutableSet.of(
                        ModBrainMemories.AIM_TICKS.get()
                )
        );
    }

    public static void updateActivity(FacCommissarEntity mob) {
        Brain<FacCommissarEntity> brain = mob.getBrain();
        Activity oldActivity = brain.getActiveNonCoreActivity().orElse(null);
        brain.setActiveActivityToFirstValid(ImmutableList.of(
                ModBrainActivities.ALERT.get(),
                Activity.FIGHT,
                Activity.IDLE
        ));

        Activity newActivity = brain.getActiveNonCoreActivity().orElse(null);
        if (oldActivity != newActivity && newActivity == ModBrainActivities.ALERT.get()) {
            brain.stopAll((ServerLevel) mob.level(), mob);
        }
    }
}
