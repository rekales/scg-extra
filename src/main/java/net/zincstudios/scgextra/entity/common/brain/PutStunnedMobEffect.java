package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.effects.ModEffects;
import net.zincstudios.scgextra.entity.ModBrainMemories;

// TODO: Placeholder for later
public class PutStunnedMobEffect extends Behavior<LivingEntity> {

    public PutStunnedMobEffect() {
        super(ImmutableMap.of(
                ModBrainMemories.STUNNED.get(), MemoryStatus.VALUE_PRESENT
        ));
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModBrainMemories.STUNNED.get());
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.addEffect(new MobEffectInstance(
                ModEffects.STUNNED_EFFECT.get(),
                (int) entity.getBrain().getTimeUntilExpiry(ModBrainMemories.STUNNED.get())
        ));
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.removeEffect(ModEffects.STUNNED_EFFECT.get());
    }

    @Override
    protected void tick(ServerLevel level, LivingEntity entity, long gameTime) {
        if (!entity.hasEffect(ModEffects.STUNNED_EFFECT.get())) {
            entity.addEffect(new MobEffectInstance(
                    ModEffects.STUNNED_EFFECT.get(),
                    (int) entity.getBrain().getTimeUntilExpiry(ModBrainMemories.STUNNED.get())
            ));
        }
    }
}
