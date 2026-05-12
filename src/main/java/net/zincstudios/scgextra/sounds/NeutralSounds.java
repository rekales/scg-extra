package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public final class NeutralSounds {
    private static boolean initialized = false;

    private NeutralSounds() {}

    private static RegistryObject<SoundEvent> reg(String id) {
        return ModSounds.SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ModSounds.asResource(id)));
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        reg("neutral.ammo_goblin.idle_01");
        reg("neutral.ammo_goblin.idle_02");
        reg("neutral.ammo_goblin.hurt_01");
        reg("neutral.ammo_goblin.hurt_02");
        reg("neutral.ammo_goblin.hurt_03");
        reg("neutral.ammo_goblin.dead");

        reg("neutral.big_lump.idle_01");
        reg("neutral.big_lump.idle_02");
        reg("neutral.big_lump.idle_03");
        reg("neutral.big_lump.hurt_01");
        reg("neutral.big_lump.hurt_02");
        reg("neutral.big_lump.death");

        reg("neutral.end_crab.attack_01");
        reg("neutral.end_crab.idle_01");
        reg("neutral.end_crab.idle_02");
        reg("neutral.end_crab.hurt_01");
        reg("neutral.end_crab.hurt_02");
        reg("neutral.end_crab.death");

        reg("neutral.end_dweller.charging");
        reg("neutral.end_dweller.idle_01");
        reg("neutral.end_dweller.idle_02");
        reg("neutral.end_dweller.idle_03");
        reg("neutral.end_dweller.hurt_01");
        reg("neutral.end_dweller.hurt_02");

        reg("neutral.end_pod.breed");
        reg("neutral.end_pod.idle_01");
        reg("neutral.end_pod.idle_02");
        reg("neutral.end_pod.idle_03");
        reg("neutral.end_pod.idle_04");
        reg("neutral.end_pod.idle_05");
        reg("neutral.end_pod.death_01");
        reg("neutral.end_pod.death_02");

        reg("neutral.end_scorpion.idle_01");
        reg("neutral.end_scorpion.idle_02");
        reg("neutral.end_scorpion.idle_03");
        reg("neutral.end_scorpion.hurt_01");
        reg("neutral.end_scorpion.hurt_02");
        reg("neutral.end_scorpion.sting");
        reg("neutral.end_scorpion.death");

        reg("neutral.mutant_bat.idle_01");
        reg("neutral.mutant_bat.idle_02");
        reg("neutral.mutant_bat.idle_03");
        reg("neutral.mutant_bat.hurt");
        reg("neutral.mutant_bat.scream");
        reg("neutral.mutant_bat.death");

        reg("neutral.netherite_eater.breath");
        reg("neutral.netherite_eater.idle_01");
        reg("neutral.netherite_eater.idle_02");
        reg("neutral.netherite_eater.idle_03");
        reg("neutral.netherite_eater.idle_04");
        reg("neutral.netherite_eater.hurt_01");
        reg("neutral.netherite_eater.hurt_02");
        reg("neutral.netherite_eater.death");
    }
}
