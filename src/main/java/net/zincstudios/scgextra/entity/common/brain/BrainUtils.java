package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.ModBrainMemories;

import java.util.Optional;

public class BrainUtils {

    public static Optional<? extends LivingEntity> findNearestAttackableFactionEnemy(LivingEntity mob) {
        Brain<? extends LivingEntity> brain = mob.getBrain();
        Optional<NearestVisibleLivingEntities> optional = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return optional.flatMap(entities -> entities.findClosest(
                entity -> Faction.isEnemies(mob, entity) && mob.canAttack(entity)));
    }

    public static Optional<? extends LivingEntity> findNearestVisibleAttackablePlayer(LivingEntity mob) {
        return mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }

    public static Optional<LivingEntity> getHurtBy(LivingEntity mob) {
        return mob.getBrain()
                .getMemory(MemoryModuleType.HURT_BY_ENTITY)
                .filter(entity -> mob != entity)
                .filter(mob::canAttack)
                .filter(entity -> isWithinRange(mob, entity));
    }

    public static Optional<LivingEntity> getHurtByNonFriendlies(LivingEntity mob) {
        return getHurtBy(mob).filter(entity -> !Faction.isFriendlies(mob, entity));
    }

    // Sensor.isEntityAttackable is shit and doesn't account for the follow range attribute
    @SuppressWarnings({"RedundantIfStatement", "BooleanMethodIsAlwaysInverted"})  // for readability
    public static boolean isTargetStillValid(LivingEntity entity, LivingEntity target, boolean needLineOfSight) {
        if (entity == target) return false;
        if (!target.canBeSeenByAnyone()) return false;
        if (!entity.canAttack(target) || !entity.canAttackType(target.getType()) || entity.isAlliedTo(target)) return false;
        if (!isWithinRange(entity, target)) return false;
        if (needLineOfSight && entity instanceof Mob mob && !mob.getSensing().hasLineOfSight(target)) return false;  // TODO: profile, might be performance intensive
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isTargetStillValidNonFriendlies(LivingEntity entity, LivingEntity target, boolean needLineOfSight) {
        return isTargetStillValid(entity, target, needLineOfSight) && !Faction.isFriendlies(entity, target);
    }


    // public static copy of Brain#createPriorityPairs
    public static <E extends LivingEntity> ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> createPriorityPairs(
            int priorityStart, ImmutableList<? extends BehaviorControl<? super E>> tasks) {
        int i = priorityStart;
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super E>>> builder = ImmutableList.builder();

        for(BehaviorControl<? super E> behaviorcontrol : tasks) {
            builder.add(Pair.of(i++, behaviorcontrol));
        }

        return builder.build();
    }

    public static boolean isStunned(LivingEntity entity) {
        return entity.getBrain().hasMemoryValue(ModBrainMemories.STUNNED.get());
    }

    public static boolean isNotStunned(LivingEntity entity) {
        return !isStunned(entity);
    }

    private static boolean isWithinRange(LivingEntity entity, LivingEntity target) {
        double range = Math.max(entity.getAttributeValue(Attributes.FOLLOW_RANGE), 2.0D);
        return entity.closerThan(target, range);
    }
}
