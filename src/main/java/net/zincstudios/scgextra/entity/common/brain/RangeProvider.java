package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.entity.ModBrainMemories;

import javax.annotation.ParametersAreNonnullByDefault;

// TODO: debug behavior, delete later
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RangeProvider extends Behavior<Mob> {

    public RangeProvider() {
        super(ImmutableMap.of(
                ModBrainMemories.APPROACH_DIST.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.WEAPON_RANGE.get(), MemoryStatus.REGISTERED
        ));
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob entity, long gameTime) {
        return true;
    }

    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();

        if (!brain.hasMemoryValue(ModBrainMemories.APPROACH_DIST.get())) {
            brain.setMemory(ModBrainMemories.APPROACH_DIST.get(), 10F);
        }

        if (!brain.hasMemoryValue(ModBrainMemories.WEAPON_RANGE.get())) {
            brain.setMemory(ModBrainMemories.WEAPON_RANGE.get(), 15F);
        }
    }

}
