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

    // TODO: check if actually needed, behavior gating might be sufficient
    public static final Supplier<Activity> STUNNED = ACTIVITIES
            .register("stunned", () -> new Activity("stunned"));

    public static void register(IEventBus modEventBus) {
        ACTIVITIES.register(modEventBus);
    }
}
