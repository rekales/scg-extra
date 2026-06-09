package net.zincstudios.scgextra.entity.cog;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.zincstudios.scgextra.entity.Faction;

import java.util.List;
import java.util.Optional;

public class BrainUtils {

    private static final TargetingConditions BOSS_PLAYER_TARGET_CONDITIONS = TargetingConditions
            .forCombat().range(16.0D).ignoreInvisibilityTesting().ignoreLineOfSight();

    public static Optional<? extends LivingEntity> findNearestAttackableFactionEnemy(LivingEntity mob) {
        Brain<? extends LivingEntity> brain = mob.getBrain();
        Optional<NearestVisibleLivingEntities> optional = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return optional.flatMap(entities -> entities.findClosest(
                entity -> Faction.isEnemies(mob, entity) && mob.canAttack(entity)));
    }

    public static Optional<? extends LivingEntity> findNearestVisibleAttackablePlayer(LivingEntity mob) {
        return mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }

    // No line of sight needed, for bosses
    public static Optional<? extends LivingEntity> findNearestAttackablePlayer(LivingEntity mob) {
        Optional<List<Player>> optional = mob.getBrain().getMemory(MemoryModuleType.NEAREST_PLAYERS);
        if (optional.isEmpty()) return Optional.empty();
        List<Player> players = optional.get();
        return players.stream().filter(target -> BOSS_PLAYER_TARGET_CONDITIONS.test(mob, target)).findFirst();
    }

    // Not for flying and swimming mobs
    public static void initGenericIdleActivity(Brain<PathfinderMob> brain) {
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
}
