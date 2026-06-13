package net.zincstudios.scgextra.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

/**
 * @param abilityId name of the current ability to identify it apart from other abilities
 * @param endTick entity.tickCount timestamp to when the ability will end
 */
public record AbilityState(String abilityId, int endTick) {

    public static final Codec<AbilityState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("AbilityId").forGetter(AbilityState::abilityId),
                    Codec.INT.fieldOf("EndTick").forGetter(AbilityState::endTick)
            ).apply(instance, AbilityState::new)
    );

    public int getTicksLeft(LivingEntity entity) {
        return this.endTick - entity.tickCount;
    }

    public boolean isSame(String abilityId) {
        return this.abilityId.equals(abilityId);
    }
}
