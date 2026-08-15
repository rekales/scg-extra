package net.zincstudios.scgextra.entity.common.brain;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class GetCloseToTarget {

    public static <E extends Mob> BehaviorControl<E> create(float approachDist, float speedModifier) {
        return BehaviorBuilder.create(builder ->
                builder.group(
                        builder.present(MemoryModuleType.ATTACK_TARGET),
                        builder.absent(MemoryModuleType.WALK_TARGET)
                ).apply(builder, (targetAcc, walkAcc) ->
                        ((level, mob, gameTime) -> {
                            LivingEntity target = builder.get(targetAcc);
                            if (mob.closerThan(target, approachDist)) return false;
                            walkAcc.setWithExpiry(new WalkTarget(
                                    new EntityTracker(target, false), speedModifier, (int) approachDist
                            ), 100);
                            return true;
                        }))
        );
    }
}
