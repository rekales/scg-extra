package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.AbilityState;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.brain.PatchedEntityTracker;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RocketBarrageAbility extends Behavior<CogJuggernautEntity> {

    public static final int DEFAULT_COOLDOWN_DURATION_TICKS = 160;
    public static final String ABILITY_ID = "cog_juggernaut_rocket_barrage";

    private final int cooldownDuration;
    private final SimulatedGun gun;

    private int rocketsLeft = 0;
    private int recoveryTimer = 0;

    public RocketBarrageAbility() {
        this(DEFAULT_COOLDOWN_DURATION_TICKS);
    }

    public RocketBarrageAbility(int cooldownDuration) {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.ABILITY_COOLING_DOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModBrainMemories.ABILITY_STATE.get(), MemoryStatus.REGISTERED
        ), 40);
        this.cooldownDuration = cooldownDuration;
        this.gun = new RocketBarrageSimGun();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        return brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && !brain.hasMemoryValue(ModBrainMemories.ABILITY_COOLING_DOWN.get())
                && brain.hasMemoryValue(ModBrainMemories.ABILITY_STATE.get())
                && this.recoveryTimer > 0;
    }

    @Override
    protected void start(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        this.rocketsLeft = 5;
        this.recoveryTimer = 20;
        mob.getBrain().setMemoryWithExpiry(
                ModBrainMemories.ABILITY_STATE.get(),
                new AbilityState(ABILITY_ID, mob.tickCount + Behavior.DEFAULT_DURATION),
                Behavior.DEFAULT_DURATION
        );
    }

    @Override
    protected void stop(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        brain.setMemoryWithExpiry(ModBrainMemories.ABILITY_COOLING_DOWN.get(), true, this.cooldownDuration);
        brain.eraseMemory(ModBrainMemories.ABILITY_STATE.get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, CogJuggernautEntity mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).get();

        if (!brain.hasMemoryValue(MemoryModuleType.LOOK_TARGET)) {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new PatchedEntityTracker(target, true));
        }

        if (this.rocketsLeft > 0) {
            if (gun.tickFire(mob, target, 2F, true)) {
                this.rocketsLeft--;
                SCGExtra.LOGGER.debug(this.rocketsLeft + " tick: " + mob.tickCount);
            }
        } else {
            this.recoveryTimer--;
        }
    }
}
