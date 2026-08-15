package net.zincstudios.scgextra.entity;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.zincstudios.scgextra.SCGExtra;

import java.util.function.Supplier;

public class ModBrainActivities {

    private static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister
            .create(ForgeRegistries.ACTIVITIES, SCGExtra.MOD_ID);

    public static final Supplier<Activity> ALERT = ACTIVITIES
            .register("alert", () -> new Activity("alert"));
    public static final Supplier<Activity> RELOCATE = ACTIVITIES
            .register("relocate", () -> new Activity("relocate"));
    public static final Supplier<Activity> STUNNED = ACTIVITIES
            .register("stunned", () -> new Activity("stunned"));


    public static void register(IEventBus modEventBus) {
        ACTIVITIES.register(modEventBus);
    }
}
