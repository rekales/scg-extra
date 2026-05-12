package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);
    static ResourceLocation asResource(String id) {
        return SCGExtra.asResource(id);
    }

    public static void register(IEventBus eventbus){
        // to force java to load static variables in case it didn't
        WhalerSounds.init();
        RRCSounds.init();
        FACSounds.init();
        AsgharianSounds.init();
        NeutralSounds.init();

        SOUND_EVENTS.register(eventbus);
    }
}
