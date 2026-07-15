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
    
    public static final RegistryObject<SoundEvent> MUTANT_BAT_IDLE = SOUND_EVENTS.register("mutant_bat.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("mutant_bat.idle")));

    public static final RegistryObject<SoundEvent> MUTANT_BAT_HURT = SOUND_EVENTS.register("mutant_bat.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("mutant_bat.hurt")));

    public static final RegistryObject<SoundEvent> MUTANT_BAT_DEAD = SOUND_EVENTS.register("mutant_bat.dead",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("mutant_bat.dead")));

    public static final RegistryObject<SoundEvent> MUTANT_BAT_SCREAM = SOUND_EVENTS.register("mutant_bat.scream",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("mutant_bat.scream")));
    
    public static final RegistryObject<SoundEvent> NETHERITE_EATER_IDLE = SOUND_EVENTS.register("netherite_eater.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("netherite_eater.idle")));

    public static final RegistryObject<SoundEvent> NETHERITE_EATER_HURT = SOUND_EVENTS.register("netherite_eater.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("netherite_eater.hurt")));

    public static final RegistryObject<SoundEvent> NETHERITE_EATER_DEAD = SOUND_EVENTS.register("netherite_eater.dead",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("netherite_eater.dead")));

    public static final RegistryObject<SoundEvent> NETHERITE_EATER_BREATH = SOUND_EVENTS.register("netherite_eater.breath",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("netherite_eater.breath")));

    public static final RegistryObject<SoundEvent> END_POD_IDLE = SOUND_EVENTS.register("end_pod.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_pod.idle")));

    public static final RegistryObject<SoundEvent> END_POD_DEAD = SOUND_EVENTS.register("end_pod.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_pod.death")));

    public static final RegistryObject<SoundEvent> END_POD_BREED = SOUND_EVENTS.register("end_pod.breed",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_pod.breed")));

    public static final RegistryObject<SoundEvent> END_DWELLER_IDLE = SOUND_EVENTS.register("end_dweller.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_dweller.idle")));

    public static final RegistryObject<SoundEvent> END_DWELLER_CHARGING = SOUND_EVENTS.register("end_dweller.charging",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_dweller.charging")));

    public static final RegistryObject<SoundEvent> END_DWELLER_HURT = SOUND_EVENTS.register("end_dweller.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_dweller.hurt")));

    public static final RegistryObject<SoundEvent> END_STONE_CRAB_IDLE= SOUND_EVENTS.register("end_stone_crab.idle",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_stone_crab.idle")));

    public static final RegistryObject<SoundEvent> END_STONE_CRAB_HURT = SOUND_EVENTS.register("end_stone_crab.hurt",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_stone_crab.hurt")));

    public static final RegistryObject<SoundEvent> END_STONE_CRAB_DEATH = SOUND_EVENTS.register("end_stone_crab.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_stone_crab.death")));

    public static final RegistryObject<SoundEvent> END_STONE_CRAB_ATTACK = SOUND_EVENTS.register("end_stone_crab.attack",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("end_stone_crab.attack")));

    public static void init() {}

}