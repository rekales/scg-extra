package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

import static net.zincstudios.scgextra.sounds.ModSounds.SOUND_EVENTS;

public class NeutralSounds {

    public static final RegistryObject<SoundEvent> AMMO_GOBLIN_IDLE = SOUND_EVENTS.register("ammo_goblin.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("ammo_goblin.idle")));

    public static final RegistryObject<SoundEvent> AMMO_GOBLIN_HURT = SOUND_EVENTS.register("ammo_goblin.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("ammo_goblin.hurt")));

    public static final RegistryObject<SoundEvent> AMMO_GOBLIN_DEAD = SOUND_EVENTS.register("ammo_goblin.dead",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("ammo_goblin.dead")));

    public static final RegistryObject<SoundEvent> BIG_LUMP_IDLE = SOUND_EVENTS.register("big_lump.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("big_lump.idle")));

    public static final RegistryObject<SoundEvent> BIG_LUMP_HURT = SOUND_EVENTS.register("big_lump.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("big_lump.hurt")));

    public static final RegistryObject<SoundEvent> BIG_LUMP_DEAD = SOUND_EVENTS.register("big_lump.dead",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("big_lump.dead")));

    public static void init() {}

}