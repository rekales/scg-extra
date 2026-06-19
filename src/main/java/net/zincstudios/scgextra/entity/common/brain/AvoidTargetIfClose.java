package net.zincstudios.scgextra.entity.common.brain;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.CopyMemoryWithExpiry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AvoidTargetIfClose {

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // already handled on CopyMemoryWithExpiry
    public static BehaviorControl<LivingEntity> create(float avoidDist, UniformInt duration) {
        return CopyMemoryWithExpiry.create(
                entity -> {
                    LivingEntity target = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
                    return entity.closerThan(target, avoidDist);
                },
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.AVOID_TARGET,
                duration
        );
    }
}