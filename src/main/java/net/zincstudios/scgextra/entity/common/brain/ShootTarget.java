package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.gun.MarkovTriggerSampler;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import net.zincstudios.scgextra.entity.common.gun.TriggerStateSampler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShootTarget extends Behavior<Mob> {

    private final int aimThreshold;
    private final Function<LivingEntity, Float> accuracyFunc;
    private final TriggerStateSampler triggerSampler;

    public ShootTarget(int aimThreshold) {
        this(aimThreshold, 3.2f);
    }

    public ShootTarget(int aimThreshold, float accuracy) {
        this(
                aimThreshold,
                entity -> accuracy,
                new MarkovTriggerSampler(0.93f, 0.94f, 15, 80)
        );
    }

    public ShootTarget(int aimThreshold, Function<LivingEntity, Float> accuracyFunc, TriggerStateSampler triggerSampler) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.AIM_TICKS.get(), MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.SIMULATED_GUN.get(), MemoryStatus.VALUE_PRESENT
        ), 80);
        this.aimThreshold = aimThreshold;
        this.accuracyFunc = accuracyFunc;
        this.triggerSampler = triggerSampler;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && brain.hasMemoryValue(ModBrainMemories.AIM_TICKS.get())
                && brain.hasMemoryValue(ModBrainMemories.SIMULATED_GUN.get());
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "RedundantIfStatement"}) // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();
        int aimTicks = brain.getMemory(ModBrainMemories.AIM_TICKS.get()).get();
        SimulatedGun simGun = brain.getMemory(ModBrainMemories.SIMULATED_GUN.get()).get();

        if (aimTicks >= this.aimThreshold && this.triggerSampler.next(mob.getRandom())) {
            simGun.tickFire(mob, target, this.accuracyFunc.apply(mob), true);
        } else {
            simGun.tickFire(mob, target, this.accuracyFunc.apply(mob), false);
        }
    }
}
