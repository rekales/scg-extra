package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

import static net.zincstudios.scgextra.sounds.ModSounds.SOUND_EVENTS;

public class AsgharianSounds {

    // Asghar Surgeon
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_ATTACK_1 = SOUND_EVENTS.register("asghar_surgeon.attack1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.attack1")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_ATTACK_2 = SOUND_EVENTS.register("asghar_surgeon.attack2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.attack2")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_DEATH = SOUND_EVENTS.register("asghar_surgeon.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.death")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_HURT_1 = SOUND_EVENTS.register("asghar_surgeon.hurt1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.hurt1")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_HURT_2 = SOUND_EVENTS.register("asghar_surgeon.hurt2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.hurt2")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_HURT_3 = SOUND_EVENTS.register("asghar_surgeon.hurt3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.hurt3")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_HURT_4 = SOUND_EVENTS.register("asghar_surgeon.hurt4",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.hurt4")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_IDLE_1 = SOUND_EVENTS.register("asghar_surgeon.idle1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.idle1")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_IDLE_2 = SOUND_EVENTS.register("asghar_surgeon.idle2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.idle2")));
    public static final RegistryObject<SoundEvent> ASGHAR_SURGEON_IDLE_3 = SOUND_EVENTS.register("asghar_surgeon.idle3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_surgeon.idle3")));

    // Asghar Worker
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_CLAW = SOUND_EVENTS.register("asghar_worker.claw",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.claw")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_DEATH = SOUND_EVENTS.register("asghar_worker.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.death")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_HURT_1 = SOUND_EVENTS.register("asghar_worker.hurt1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.hurt1")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_HURT_2 = SOUND_EVENTS.register("asghar_worker.hurt2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.hurt2")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_HURT_3 = SOUND_EVENTS.register("asghar_worker.hurt3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.hurt3")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_SAW = SOUND_EVENTS.register("asghar_worker.saw",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.saw")));
    public static final RegistryObject<SoundEvent> ASGHAR_WORKER_WALK = SOUND_EVENTS.register("asghar_worker.walk",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_worker.walk")));

    // Asghar Flamer
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_ATTACK_1 = SOUND_EVENTS.register("asghar_flamer.attack1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.attack1")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_ATTACK_2 = SOUND_EVENTS.register("asghar_flamer.attack2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.attack2")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_DEATH = SOUND_EVENTS.register("asghar_flamer.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.death")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_HURT_1 = SOUND_EVENTS.register("asghar_flamer.hurt1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.hurt1")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_HURT_2 = SOUND_EVENTS.register("asghar_flamer.hurt2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.hurt2")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_IDLE_1 = SOUND_EVENTS.register("asghar_flamer.idle1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.idle1")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_IDLE_2 = SOUND_EVENTS.register("asghar_flamer.idle2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.idle2")));
    public static final RegistryObject<SoundEvent> ASGHAR_FLAMER_IDLE_3 = SOUND_EVENTS.register("asghar_flamer.idle3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("asghar_flamer.idle3")));

    // Candle Fiend
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_DEATH = SOUND_EVENTS.register("candle_fiend.death",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.death")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_HURT_1 = SOUND_EVENTS.register("candle_fiend.hurt1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.hurt1")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_HURT_2 = SOUND_EVENTS.register("candle_fiend.hurt2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.hurt2")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_HURT_3 = SOUND_EVENTS.register("candle_fiend.hurt3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.hurt3")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_HURT_4 = SOUND_EVENTS.register("candle_fiend.hurt4",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.hurt4")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_IDLE_1 = SOUND_EVENTS.register("candle_fiend.idle1",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.idle1")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_IDLE_2 = SOUND_EVENTS.register("candle_fiend.idle2",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.idle2")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_IDLE_3 = SOUND_EVENTS.register("candle_fiend.idle3",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.idle3")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_REVIVE = SOUND_EVENTS.register("candle_fiend.revive",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.revive")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_RUN = SOUND_EVENTS.register("candle_fiend.run",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.run")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_SCREAM = SOUND_EVENTS.register("candle_fiend.scream",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.scream")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_SLASH = SOUND_EVENTS.register("candle_fiend.slash",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.slash")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_SMASH = SOUND_EVENTS.register("candle_fiend.smash",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.smash")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_WALK = SOUND_EVENTS.register("candle_fiend.walk",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.walk")));
    public static final RegistryObject<SoundEvent> CANDLE_FIEND_WARNING = SOUND_EVENTS.register("candle_fiend.warning",
            () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource("candle_fiend.warning")));

    public static void init() {}
}
