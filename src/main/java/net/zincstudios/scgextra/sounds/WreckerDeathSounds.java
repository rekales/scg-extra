package net.zincstudios.scgextra.sounds;

import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public final class WreckerDeathSounds {

    private WreckerDeathSounds() {}

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof InterruptibleVoice voice)) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        List<SoundEvent> lines = voice.voiceLinesToSilenceOnDeath();
        if (lines.isEmpty()) {
            return;
        }
        SoundSource source = entity.getSoundSource();
        for (SoundEvent line : lines) {

            level.getChunkSource().broadcastAndSend(entity,
                    new ClientboundStopSoundPacket(line.getLocation(), source));
        }
    }
}
