package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.zincstudios.scgextra.entity.cog.BrainUtils;

import java.util.List;
import java.util.Optional;

public class CogJuggernautAi {

    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super CogJuggernautEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS
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
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN
    );

    protected static Brain<?> makeBrain(CogJuggernautEntity mob, Brain<CogJuggernautEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(mob, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<CogJuggernautEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink())
        );
    }

    private static void initIdleActivity(Brain<CogJuggernautEntity> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(BrainUtils::findNearestAttackableFactionEnemy),
                StartAttacking.create(BrainUtils::findNearestVisibleAttackablePlayer),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1.0F), 2),
                        Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1)
                ))
        ));
    }

    private static void initFightActivity(CogJuggernautEntity mob, Brain<CogJuggernautEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(p_350106_ -> !Sensor.isEntityAttackable(mob, p_350106_)),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
                MeleeAttack.create(20)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    static void updateActivity(CogJuggernautEntity mob) {
        mob.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(CogJuggernautEntity mob) {
        Brain<CogJuggernautEntity> brain = mob.getBrain();
        Optional<List<LivingEntity>> optional = brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
        if (optional.isEmpty()) return Optional.empty();
        List<LivingEntity> entities = optional.get();
        for (LivingEntity entity : entities) {
            if (mob.canAttack(entity)) return Optional.of(entity);
        }

        return Optional.empty();
    }
}
