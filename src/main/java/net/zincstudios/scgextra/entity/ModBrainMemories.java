package net.zincstudios.scgextra.entity;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;

import java.util.Optional;
import java.util.function.Supplier;

public class ModBrainMemories {

    private static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister
            .create(ForgeRegistries.MEMORY_MODULE_TYPES, SCGExtra.MOD_ID);

    public static final Supplier<MemoryModuleType<Integer>> AIM_TICKS = MEMORY_MODULE_TYPES
            .register("aim_ticks", () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    public static final Supplier<MemoryModuleType<Float>> WEAPON_IDEAL_RANGE = MEMORY_MODULE_TYPES
            .register("approach_dist", () -> new MemoryModuleType<>(Optional.of(Codec.FLOAT)));
    public static final Supplier<MemoryModuleType<Float>> WEAPON_MAX_RANGE = MEMORY_MODULE_TYPES
            .register("weapon_range", () -> new MemoryModuleType<>(Optional.of(Codec.FLOAT)));
    public static final Supplier<MemoryModuleType<SimulatedGun>> SIMULATED_GUN = MEMORY_MODULE_TYPES
            .register("simulated_gun", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<Integer>> STUN_END = MEMORY_MODULE_TYPES  // tickCount timestamp
            .register("stun_end", () -> new MemoryModuleType<>(Optional.of(Codec.INT)));
    public static final Supplier<MemoryModuleType<Boolean>> ABILITY_COOLING_DOWN = MEMORY_MODULE_TYPES
            .register("ability_cooling_down", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));
    public static final Supplier<MemoryModuleType<AbilityState>> ABILITY_STATE = MEMORY_MODULE_TYPES
            .register("ability_state", () -> new MemoryModuleType<>(Optional.of(AbilityState.CODEC)));

    public static final int MAX_AIM_TICKS = 60;
    public static final int MIN_AIM_TICKS = -20;

    public static void register(IEventBus modEventBus) {
        MEMORY_MODULE_TYPES.register(modEventBus);
    }
}
