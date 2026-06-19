package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.gun.GunTarget;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HandleSimulatedGun extends Behavior<LivingEntity> {

    private final Function<LivingEntity, Float> accuracyFunc;

    public HandleSimulatedGun(float accuracy) {
        this(entity -> accuracy);
    }

    public HandleSimulatedGun(Function<LivingEntity, Float> accuracyFunc) {
        super(ImmutableMap.of(
                ModBrainMemories.SHOOT_TARGET.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.HOLD_FIRE.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.TRIGGER_PULLED.get(), MemoryStatus.REGISTERED,
                ModBrainMemories.SIMULATED_GUN.get(), MemoryStatus.VALUE_PRESENT
        ), 200);
        this.accuracyFunc = accuracyFunc;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModBrainMemories.SIMULATED_GUN.get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")  // because already handled on canStillUse
    @Override
    protected void tick(ServerLevel level, LivingEntity entity, long gameTime) {
        Brain<?> brain = entity.getBrain();

        boolean holdFire = brain.getMemory(ModBrainMemories.HOLD_FIRE.get()).orElse(false);
        boolean trigger = brain.getMemory(ModBrainMemories.TRIGGER_PULLED.get()).orElse(false);
        SimulatedGun simGun = brain.getMemory(ModBrainMemories.SIMULATED_GUN.get()).get();
        Optional<GunTarget> shootTarget = brain.getMemory(ModBrainMemories.SHOOT_TARGET.get());
        Vec3 targetPos;
        if (shootTarget.isPresent()) {
            targetPos = shootTarget.get().getPos(simGun, entity);
        } else {
            targetPos = entity.getLookAngle();
        }

        if (!holdFire) {
            simGun.tickFire(entity, targetPos, this.accuracyFunc.apply(entity), trigger);
        }

    }
}
