package net.zincstudios.scgextra.entity.cog.bombardier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
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

public class CogBombardierAi {

    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super CogBombardierEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            ModBrainSensors.CUSTOM_GUN.get()
    );

    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.AVOID_TARGET,
            ModBrainMemories.AIM_TICKS.get(),
            ModBrainMemories.WEAPON_IDEAL_RANGE.get(),
            ModBrainMemories.WEAPON_MAX_RANGE.get()
    );

    protected static Brain<?> makeBrain(CogBombardierEntity mob, Brain<CogBombardierEntity> brain) {
        BrainUtils.Standard.initCoreActivity(brain);
        BrainUtils.Standard.initIdleActivity(brain);
        initFightActivity(mob, brain);
        initAvoidActivity(brain);
        initStunnedActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initFightActivity(CogBombardierEntity mob, Brain<CogBombardierEntity> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.FIGHT, BrainUtils.createPriorityPairs(10, ImmutableList.of(
                        StopAttackingIfTargetInvalid.create(target -> !BrainUtils.isTargetStillValid(mob, target, false)),
                        AttackLastHurtIfNear.create((self, target) -> !Faction.isFriendlies(self, target), true),
                        AvoidTargetIfClose.create(10, UniformInt.of(60, 80)),
                        new WalkUpToIdealRange(1.0F),
                        new AimWhenNotWalking(),
                        new ShootTarget(30)
                )), ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                ), ImmutableSet.of(
                        ModBrainMemories.AIM_TICKS.get()
                )
        );

    }

    private static void initAvoidActivity(Brain<CogBombardierEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
                        new SetToSprint(),
                        SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1F, 15, true)
                ),
                MemoryModuleType.AVOID_TARGET
        );
    }

    private static void initStunnedActivity(Brain<CogBombardierEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(ModBrainActivities.STUNNED.get(), 10, ImmutableList.of(
                new PutStunnedMobEffect()
                ), ModBrainMemories.STUNNED.get()
        );
    }

    public static void updateActivity(CogBombardierEntity mob) {
        Brain<CogBombardierEntity> brain = mob.getBrain();
        Activity oldActivity = brain.getActiveNonCoreActivity().orElse(null);
        brain.setActiveActivityToFirstValid(ImmutableList.of(
                ModBrainActivities.STUNNED.get(),
                Activity.AVOID,
                Activity.FIGHT,
                Activity.IDLE
        ));
        Activity newActivity = brain.getActiveNonCoreActivity().orElse(null);

        if (newActivity == ModBrainActivities.STUNNED.get() && oldActivity != newActivity) {
            brain.stopAll((ServerLevel) mob.level(), mob);
            mob.getNavigation().stop();
        }
    }
}
