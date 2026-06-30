package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.SCGExtra;

import static net.zincstudios.scgextra.sounds.ModSounds.SOUND_EVENTS;

public class WreckersSounds {

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(SCGExtra.asResource(name)));
    }

    // GANG (shared by red / green / turret which the doc doesn't indicate the voice set)
    public static final RegistryObject<SoundEvent> GANG_IDLE_1 = register("wreckers.idle1");
    public static final RegistryObject<SoundEvent> GANG_IDLE_2 = register("wreckers.idle2");
    public static final RegistryObject<SoundEvent> GANG_IDLE_3 = register("wreckers.idle3");
    public static final RegistryObject<SoundEvent> GANG_IDLE_4 = register("wreckers.idle4");
    public static final RegistryObject<SoundEvent> GANG_HURT_1 = register("wreckers.hurt1");
    public static final RegistryObject<SoundEvent> GANG_HURT_2 = register("wreckers.hurt2");
    public static final RegistryObject<SoundEvent> GANG_HURT_3 = register("wreckers.hurt3");
    public static final RegistryObject<SoundEvent> GANG_ATTACK_1 = register("wreckers.attack1");
    public static final RegistryObject<SoundEvent> GANG_ATTACK_2 = register("wreckers.attack2");
    public static final RegistryObject<SoundEvent> GANG_ATTACK_3 = register("wreckers.attack3");
    public static final RegistryObject<SoundEvent> GANG_DEATH_1 = register("wreckers.death1");
    public static final RegistryObject<SoundEvent> GANG_DEATH_2 = register("wreckers.death2");
    public static final RegistryObject<SoundEvent> GANG_DEATH_3 = register("wreckers.death3");
    public static final RegistryObject<SoundEvent> GANG_DEATH_4 = register("wreckers.death4");
    public static final RegistryObject<SoundEvent> GANG_DEATH_5 = register("wreckers.death5");

    // BLUE
    public static final RegistryObject<SoundEvent> BLUE_IDLE_1 = register("wrecker_blue.idle1");
    public static final RegistryObject<SoundEvent> BLUE_IDLE_2 = register("wrecker_blue.idle2");
    public static final RegistryObject<SoundEvent> BLUE_IDLE_3 = register("wrecker_blue.idle3");
    public static final RegistryObject<SoundEvent> BLUE_HURT_1 = register("wrecker_blue.hurt1");
    public static final RegistryObject<SoundEvent> BLUE_HURT_2 = register("wrecker_blue.hurt2");
    public static final RegistryObject<SoundEvent> BLUE_HURT_3 = register("wrecker_blue.hurt3");
    public static final RegistryObject<SoundEvent> BLUE_ATTACK_1 = register("wrecker_blue.attack1");
    public static final RegistryObject<SoundEvent> BLUE_ATTACK_2 = register("wrecker_blue.attack2");
    public static final RegistryObject<SoundEvent> BLUE_ATTACK_3 = register("wrecker_blue.attack3");
    public static final RegistryObject<SoundEvent> BLUE_ATTACK_4 = register("wrecker_blue.attack4");
    public static final RegistryObject<SoundEvent> BLUE_DEATH_1 = register("wrecker_blue.death1");
    public static final RegistryObject<SoundEvent> BLUE_DEATH_2 = register("wrecker_blue.death2");

    // JUMBO
    public static final RegistryObject<SoundEvent> JUMBO_IDLE = register("wrecker_jumbo.idle");
    public static final RegistryObject<SoundEvent> JUMBO_HURT = register("wrecker_jumbo.hurt");
    public static final RegistryObject<SoundEvent> JUMBO_ATTACK = register("wrecker_jumbo.attack");
    public static final RegistryObject<SoundEvent> JUMBO_DEATH = register("wrecker_jumbo.death");
    public static final RegistryObject<SoundEvent> JUMBO_LINE = register("wrecker_jumbo.line");

    // HELICUBE
    public static final RegistryObject<SoundEvent> HELICUBE_IDLE = register("wrecker_helicube.idle");
    public static final RegistryObject<SoundEvent> HELICUBE_HURT_1 = register("wrecker_helicube.hurt1");
    public static final RegistryObject<SoundEvent> HELICUBE_HURT_2 = register("wrecker_helicube.hurt2");

    // DOZER
    public static final RegistryObject<SoundEvent> DOZER_IDLE_1 = register("wrecker_dozer.idle1");
    public static final RegistryObject<SoundEvent> DOZER_IDLE_2 = register("wrecker_dozer.idle2");
    public static final RegistryObject<SoundEvent> DOZER_IDLE_3 = register("wrecker_dozer.idle3");
    public static final RegistryObject<SoundEvent> DOZER_IDLE_4 = register("wrecker_dozer.idle4");
    public static final RegistryObject<SoundEvent> DOZER_HURT_1 = register("wrecker_dozer.hurt1");
    public static final RegistryObject<SoundEvent> DOZER_HURT_2 = register("wrecker_dozer.hurt2");
    public static final RegistryObject<SoundEvent> DOZER_HURT_3 = register("wrecker_dozer.hurt3");
    public static final RegistryObject<SoundEvent> DOZER_HURT_4 = register("wrecker_dozer.hurt4");
    public static final RegistryObject<SoundEvent> DOZER_HURT_5 = register("wrecker_dozer.hurt5");
    public static final RegistryObject<SoundEvent> DOZER_CHARGE = register("wrecker_dozer.charge");
    public static final RegistryObject<SoundEvent> DOZER_MOVE = register("wrecker_dozer.move");
    public static final RegistryObject<SoundEvent> DOZER_STUN_DEATH = register("wrecker_dozer.stun_death");

    public static void init() {}
}
