package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.zincstudios.scgextra.entity.ModBrainMemories;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.function.Function;

import static net.zincstudios.scgextra.entity.ModBrainMemories.MAX_AIM_TICKS;
import static net.zincstudios.scgextra.entity.ModBrainMemories.MIN_AIM_TICKS;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ApproachTargetAndAim extends Behavior<Mob> {

    private final Function<LivingEntity, Float> speedModifier;

    public ApproachTargetAndAim(Function<LivingEntity, Float> speedModifier) {
        super(ImmutableMap.of(
                MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.AIM_TICKS.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.APPROACH_DIST.get(), MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.WEAPON_RANGE.get(), MemoryStatus.VALUE_PRESENT
        ));
        this.speedModifier = speedModifier;
    }

    public ApproachTargetAndAim(float speedModifier) {
        this(entity -> speedModifier);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && brain.hasMemoryValue(ModBrainMemories.APPROACH_DIST.get())
                && brain.hasMemoryValue(ModBrainMemories.WEAPON_RANGE.get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // because already handled on init
    @Override
    protected void start(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();
        float approachDist = brain.getMemory(ModBrainMemories.APPROACH_DIST.get()).get();
        float weaponRange = brain.getMemory(ModBrainMemories.WEAPON_RANGE.get()).get();
        int aimTicks = brain.getMemory(ModBrainMemories.AIM_TICKS.get()).orElse(0);
        boolean lineOfSight = mob.getSensing().hasLineOfSight(target);

        if (!brain.hasMemoryValue(MemoryModuleType.LOOK_TARGET)) {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new PatchedEntityTracker(target, true));
        }

        if (brain.hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
            aimTicks = 0;
        } else {
            aimTicks += lineOfSight ? 1 : -1;
            aimTicks = Mth.clamp(aimTicks, MIN_AIM_TICKS, MAX_AIM_TICKS);
        }
        brain.setMemory(ModBrainMemories.AIM_TICKS.get(), aimTicks);

        if (!mob.closerThan(target, weaponRange) || aimTicks <= MIN_AIM_TICKS) {
            if (!brain.hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(
                        new EntityTracker(target, false), this.speedModifier.apply(mob), 0
                ));
            }
            return;
        }

        if (mob.closerThan(target, approachDist) && lineOfSight) {
            if (brain.hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            }
        }
    }

}
