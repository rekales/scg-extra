package net.zincstudios.scgextra.entity.whaler;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

public class WhalerSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);
    
    public static final RegistryObject<SoundEvent> WHALE_SLAM_1 = SOUND_EVENTS.register("armored_whale_slam_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_slam_1")));

    public static final RegistryObject<SoundEvent> WHALE_SLAM_2 = SOUND_EVENTS.register("armored_whale_slam_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_slam_2")));

    public static final RegistryObject<SoundEvent> WHALE_SPLASH_1 = SOUND_EVENTS.register("armored_whale_splash_1", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_splash_1")));

    public static final RegistryObject<SoundEvent> WHALE_SPLASH_2 = SOUND_EVENTS.register("armored_whale_splash_2", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("armored_whale_splash_2")));

    public static final RegistryObject<SoundEvent> GUARDIAN_STATUE_CHARGE = SOUND_EVENTS.register("guardian_statue_charge", 
        () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("guardian_statue_charge")));
    
    public static void register(IEventBus eventbus){
        SOUND_EVENTS.register(eventbus);
    }
}