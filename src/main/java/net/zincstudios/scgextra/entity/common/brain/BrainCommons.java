package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.zincstudios.scgextra.entity.ModBrainActivities;
import net.zincstudios.scgextra.entity.ModBrainMemories;

public class BrainCommons {

    public static void initCoreActivity(Brain<? extends Mob> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    public static void initCoreWithStunActivity(Brain<? extends Mob> brain, int stunDuration) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink(),
                new CheckHeadshotStun(5, stunDuration, 200)
        ));
    }

    // Not for flying and swimming mobs
    public static void initIdleActivity(Brain<? extends PathfinderMob> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(
                StartAttacking.create(BrainUtils::getHurtByNonFriendlies),
                StartAttacking.create(BrainUtils::findNearestVisibleAttackablePlayer),
                StartAttacking.create(BrainUtils::findNearestAttackableFactionEnemy),
                new RunOne<>(ImmutableList.of(
                        Pair.of(RandomStroll.stroll(1.0F), 2),
                        Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1)
                ))
        ));
    }

    public static void initStunnedActivity(Brain<? extends LivingEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(ModBrainActivities.STUNNED.get(), 10, ImmutableList.of(
                        new HandleStunnedVisuals()
                ), ModBrainMemories.STUNNED.get()
        );
    }

    public static void initAvoidActivity(Brain<? extends PathfinderMob> brain, float fleeDistance) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
                        new SetToSprint(),
                        SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1F, (int) fleeDistance, true)
                ),
                MemoryModuleType.AVOID_TARGET
        );
    }

    public static void initAlertActivity(Brain<? extends LivingEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(ModBrainActivities.ALERT.get(), 10, ImmutableList.of(
                        new AlertNearbyFactionMobs()
                ), ModBrainMemories.TO_ALERT.get()
        );
    }

    public static void updateActivity(Mob mob) {
        mob.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }
}
