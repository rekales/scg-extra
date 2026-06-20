package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SetToSprint extends Behavior<LivingEntity> {

    public SetToSprint() {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT
        ), 10);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return true;
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.setSprinting(true);
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.setSprinting(false);
    }
}
