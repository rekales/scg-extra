package net.zincstudios.scgextra.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * @param abilityId name of the current ability to identify it apart from other abilities
 * @param endTick level timestamp to when the ability will end
 */
public record AbilityState(String abilityId, long startTick, long endTick) {

    public static final Codec<AbilityState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("AbilityId").forGetter(AbilityState::abilityId),
                    Codec.LONG.fieldOf("StartTick").forGetter(AbilityState::endTick),
                    Codec.LONG.fieldOf("EndTick").forGetter(AbilityState::endTick)
            ).apply(instance, AbilityState::new)
    );

    public long getTicksLeft(Level level) {
        return this.endTick - level.getGameTime();
    }

    public long getDuration(Level level) {
        return level.getGameTime() - this.startTick;
    }

    public boolean isSame(String abilityId) {
        return this.abilityId.equals(abilityId);
    }
}
