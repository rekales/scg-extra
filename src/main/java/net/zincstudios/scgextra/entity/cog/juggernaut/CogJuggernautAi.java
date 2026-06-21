package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.zincstudios.scgextra.entity.*;
import net.zincstudios.scgextra.entity.common.brain.*;

public class CogJuggernautAi {

    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super CogJuggernautEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            ModBrainSensors.HELD_GUN.get()
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
            ModBrainMemories.AIM_TICKS.get(),
            ModBrainMemories.WEAPON_IDEAL_RANGE.get(),
            ModBrainMemories.WEAPON_MAX_RANGE.get(),
            ModBrainMemories.ABILITY_STATE.get(),
            ModBrainMemories.RELOCATE_TARGET.get(),
            ModBrainMemories.JET_BOOTS_COOLING_DOWN.get()
    );

    protected static Brain<?> makeBrain(CogJuggernautEntity mob, Brain<CogJuggernautEntity> brain) {
        BrainCommons.initCoreActivity(brain);
        BrainCommons.initIdleActivity(brain);
        initFightActivity(mob, brain);
        initRelocateActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initFightActivity(CogJuggernautEntity mob, Brain<CogJuggernautEntity> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.FIGHT, BrainUtils.createPriorityPairs(10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(target -> !BrainUtils.isTargetStillValid(mob, target, false)),
                AttackLastHurtIfNear.create((self, target) -> !Faction.isFriendlies(self, target), true),
                GetCloseToTarget.create(5, 1.0F),
                new AimWhenTargetVisible(),
                JetBootsRelocate.create(),
                new RocketBarrageAbility(),
                new ConditionalBehavior<>(
                        ImmutableMap.of(ModBrainMemories.ABILITY_STATE.get(), MemoryStatus.VALUE_ABSENT),
                        ImmutableList.of(new ShootTarget(20))
                ))), ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                ), ImmutableSet.of(
                        ModBrainMemories.AIM_TICKS.get()
                )
        );
    }

    private static void initRelocateActivity(Brain<CogJuggernautEntity> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(ModBrainActivities.RELOCATE.get(), BrainUtils.createPriorityPairs(10, ImmutableList.of(
                new JetBootsAbility(),
                new BurnNearby(CogJuggernautAi::jetBootsShouldBurn)
                )), ImmutableSet.of(
                        Pair.of(ModBrainMemories.RELOCATE_TARGET.get(), MemoryStatus.VALUE_PRESENT),
                        Pair.of(ModBrainMemories.JET_BOOTS_COOLING_DOWN.get(), MemoryStatus.VALUE_ABSENT)
                ), ImmutableSet.of(ModBrainMemories.RELOCATE_TARGET.get())
        );
    }

    public static void updateActivity(Mob mob) {
        mob.getBrain().setActiveActivityToFirstValid(ImmutableList.of(
                ModBrainActivities.RELOCATE.get(),
                Activity.FIGHT,
                Activity.IDLE
        ));
    }

    private static boolean jetBootsShouldBurn(LivingEntity entity, LivingEntity target) {
        double dx = target.getX() - entity.getX();
        double dz = target.getZ() - entity.getZ();
        double dy = entity.getY() - target.getY(); // positive when target is below
        return (dx * dx + dz * dz) <= 4 * 4 && dy >= 1.2 && dy <= 6;
    }
}
