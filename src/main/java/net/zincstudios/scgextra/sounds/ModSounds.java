package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SCGExtra.MOD_ID);
    static ResourceLocation asResource(String id) {
        return SCGExtra.asResource(id);
    }
    private static RegistryObject<SoundEvent> reg(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(asResource(id)));
    }

    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_IDLE_01 = reg("neutral.ammo_goblin.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_IDLE_02 = reg("neutral.ammo_goblin.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_HURT_01 = reg("neutral.ammo_goblin.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_HURT_02 = reg("neutral.ammo_goblin.hurt_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_HURT_03 = reg("neutral.ammo_goblin.hurt_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_AMMO_GOBLIN_DEAD = reg("neutral.ammo_goblin.dead");

    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_IDLE_01 = reg("neutral.big_lump.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_IDLE_02 = reg("neutral.big_lump.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_IDLE_03 = reg("neutral.big_lump.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_HURT_01 = reg("neutral.big_lump.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_HURT_02 = reg("neutral.big_lump.hurt_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_BIG_LUMP_DEATH = reg("neutral.big_lump.death");

    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_ATTACK_01 = reg("neutral.end_crab.attack_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_IDLE_01 = reg("neutral.end_crab.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_IDLE_02 = reg("neutral.end_crab.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_HURT_01 = reg("neutral.end_crab.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_HURT_02 = reg("neutral.end_crab.hurt_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_CRAB_DEATH = reg("neutral.end_crab.death");

    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_CHARGING = reg("neutral.end_dweller.charging");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_IDLE_01 = reg("neutral.end_dweller.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_IDLE_02 = reg("neutral.end_dweller.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_IDLE_03 = reg("neutral.end_dweller.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_HURT_01 = reg("neutral.end_dweller.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_DWELLER_HURT_02 = reg("neutral.end_dweller.hurt_02");

    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_BREED = reg("neutral.end_pod.breed");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_IDLE_01 = reg("neutral.end_pod.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_IDLE_02 = reg("neutral.end_pod.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_IDLE_03 = reg("neutral.end_pod.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_IDLE_04 = reg("neutral.end_pod.idle_04");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_IDLE_05 = reg("neutral.end_pod.idle_05");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_DEATH_01 = reg("neutral.end_pod.death_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_POD_DEATH_02 = reg("neutral.end_pod.death_02");

    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_IDLE_01 = reg("neutral.end_scorpion.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_IDLE_02 = reg("neutral.end_scorpion.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_IDLE_03 = reg("neutral.end_scorpion.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_HURT_01 = reg("neutral.end_scorpion.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_HURT_02 = reg("neutral.end_scorpion.hurt_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_STING = reg("neutral.end_scorpion.sting");
    public static final RegistryObject<SoundEvent> NEUTRAL_END_SCORPION_DEATH = reg("neutral.end_scorpion.death");

    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_IDLE_01 = reg("neutral.mutant_bat.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_IDLE_02 = reg("neutral.mutant_bat.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_IDLE_03 = reg("neutral.mutant_bat.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_HURT = reg("neutral.mutant_bat.hurt");
    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_SCREAM = reg("neutral.mutant_bat.scream");
    public static final RegistryObject<SoundEvent> NEUTRAL_MUTANT_BAT_DEATH = reg("neutral.mutant_bat.death");

    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_BREATH = reg("neutral.netherite_eater.breath");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_IDLE_01 = reg("neutral.netherite_eater.idle_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_IDLE_02 = reg("neutral.netherite_eater.idle_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_IDLE_03 = reg("neutral.netherite_eater.idle_03");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_IDLE_04 = reg("neutral.netherite_eater.idle_04");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_HURT_01 = reg("neutral.netherite_eater.hurt_01");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_HURT_02 = reg("neutral.netherite_eater.hurt_02");
    public static final RegistryObject<SoundEvent> NEUTRAL_NETHERITE_EATER_DEATH = reg("neutral.netherite_eater.death");

    public static void register(IEventBus eventbus){
        // to force java to load static variables in case it didn't
        WhalerSounds.init();
        RRCSounds.init();
        FACSounds.init();
        AsgharianSounds.init();

        SOUND_EVENTS.register(eventbus);
    }
}
