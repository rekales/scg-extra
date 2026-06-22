package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.entity.ModBrainMemories;

public class DelayedMeleeAttack extends Behavior<LivingEntity> {

    private final int damageDelay;
    private final int meleeDuration;
    protected final float range;
    private final int meleeCooldown;

    public DelayedMeleeAttack(int damageDelay, int meleeDuration, float range) {
        this(damageDelay, meleeDuration, range, 0);
    }

    public DelayedMeleeAttack(int damageDelay, int meleeDuration, float range, int meleeCooldown) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT,
                ModBrainMemories.DELAYED_MELEE.get(), MemoryStatus.REGISTERED
        ));
        this.damageDelay = damageDelay;
        this.meleeDuration = meleeDuration;
        this.range = range;
        this.meleeCooldown = meleeCooldown;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // because already handled on hasRequiredMemories
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity entity) {
        LivingEntity target = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        return entity.position().closerThan(target.position(), entity.getBbWidth()/2 + this.range);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                && entity.getBrain().hasMemoryValue(ModBrainMemories.DELAYED_MELEE.get());
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.getBrain().setMemoryWithExpiry(
                ModBrainMemories.DELAYED_MELEE.get(),
                this.damageDelay + gameTime,
                this.meleeDuration
        );
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.meleeCooldown);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, LivingEntity entity, long gameTime) {
        LivingEntity target = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (entity.getBrain().getMemory(ModBrainMemories.DELAYED_MELEE.get()).get() == gameTime
                && entity.position().closerThan(target.position(), (entity.getBbWidth()/2 + this.range) * 1.2)) {
            entity.doHurtTarget(target);
        }
    }
}
