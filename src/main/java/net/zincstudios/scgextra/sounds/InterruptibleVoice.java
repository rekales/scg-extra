package net.zincstudios.scgextra.sounds;

import net.minecraft.sounds.SoundEvent;

import java.util.List;

public interface InterruptibleVoice {

    List<SoundEvent> voiceLinesToSilenceOnDeath();
}
