package net.zincstudios.scgextra.entity;

import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.brain.HeldGunSensor;

import java.util.function.Supplier;

public class ModBrainSensors {

    private static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister
            .create(ForgeRegistries.SENSOR_TYPES, SCGExtra.MOD_ID);

    public static final Supplier<SensorType<HeldGunSensor>> HELD_GUN = SENSOR_TYPES
            .register("held_gun", () -> new SensorType<>(HeldGunSensor::new));
    public static final Supplier<SensorType<HeldGunSensor>> HELD_GUN_RAPID = SENSOR_TYPES
            .register("held_gun_rapid", () -> new SensorType<>(() -> new HeldGunSensor(1)));

    public static void register(IEventBus modEventBus) {
        SENSOR_TYPES.register(modEventBus);
    }
}
