package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConditionalBehavior<E extends LivingEntity> implements BehaviorControl<E> {

    private final Map<MemoryModuleType<?>, MemoryStatus> runCondition;
    private final Predicate<E> runPredicate;
    private final List<BehaviorControl<? super E>> behaviors;
    private Behavior.Status status = Behavior.Status.STOPPED;

    private long checkedGameTime = 0;

    public ConditionalBehavior(Map<MemoryModuleType<?>, MemoryStatus> runCondition, Predicate<E> runPredicate, List<BehaviorControl<? super E>> behaviors) {
        this.runCondition = runCondition;
        this.runPredicate = runPredicate;
        this.behaviors = behaviors;
    }

    public ConditionalBehavior(Map<MemoryModuleType<?>, MemoryStatus> runCondition, List<BehaviorControl<? super E>> behaviors) {
        this(runCondition, entity -> true, behaviors);
    }

    public ConditionalBehavior(Predicate<E> runPredicate, List<BehaviorControl<? super E>> behaviors) {
        this(ImmutableMap.of(), runPredicate, behaviors);
    }

    @Override
    public Behavior.Status getStatus() {
        return this.status;
    }

    @Override
    public boolean tryStart(ServerLevel level, E entity, long gameTime) {
        if (this.hasRequiredMemories(entity) && runPredicate.test(entity)) {
            this.tryStartAll(level, entity, gameTime);
            if (this.behaviors.stream().anyMatch(behavior -> behavior.getStatus() == Behavior.Status.RUNNING)) {
                this.status = Behavior.Status.RUNNING;
                this.checkedGameTime = gameTime;
                return true;
            }
        }
        return false;
    }

    // TODO: profile, might be a massive performance overhead
    @Override
    public void tickOrStop(ServerLevel level, E entity, long gameTime) {
        boolean conditionsMet = true;

        if (this.checkedGameTime != gameTime) {  // to avoid running tryStart logic twice
            conditionsMet = this.hasRequiredMemories(entity) && runPredicate.test(entity);
            if (conditionsMet) {
                this.tryStartAll(level, entity, gameTime);
            }
        }

        if (conditionsMet) {
            this.behaviors.stream()
                    .filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
                    .forEach((behavior) -> behavior.tickOrStop(level, entity, gameTime));
            if (this.behaviors.stream().noneMatch(behavior -> behavior.getStatus() == Behavior.Status.RUNNING)) {
                this.doStop(level, entity, gameTime);
            }
        } else {
            this.behaviors.stream()
                    .filter((behavior) -> behavior.getStatus() == Behavior.Status.RUNNING)
                    .forEach((behavior) -> behavior.doStop(level, entity, gameTime));
        }
    }

    @Override
    public void doStop(ServerLevel level, E entity, long gameTime) {
        this.status = Behavior.Status.STOPPED;
    }

    @Override
    public String debugString() {
        return this.getClass().getSimpleName();
    }

    private boolean hasRequiredMemories(E entity) {
        for(Map.Entry<MemoryModuleType<?>, MemoryStatus> entry : this.runCondition.entrySet()) {
            if (!entity.getBrain().checkMemory(entry.getKey(), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    private void tryStartAll(ServerLevel level, E entity, long gameTime) {
        this.behaviors.stream()
                .filter(behavior -> behavior.getStatus() == Behavior.Status.STOPPED)
                .forEach(behavior -> behavior.tryStart(level, entity, gameTime));
    }
}
