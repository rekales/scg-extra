package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.gun.HeldSimulatedGun;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HeldGunSensor extends Sensor<LivingEntity> {

    // I think we can afford a quicker scan rate for something this simple

    public HeldGunSensor() {
        this(10);
    }

    public HeldGunSensor(int scanRate) {
        super(scanRate);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(
                ModBrainMemories.SIMULATED_GUN.get(),
                ModBrainMemories.APPROACH_DIST.get(),
                ModBrainMemories.WEAPON_RANGE.get()
        );
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<SimulatedGun> optional = brain.getMemory(ModBrainMemories.SIMULATED_GUN.get());

        if ((optional.isEmpty() || optional.get().hasChanged(entity))
                && entity.getMainHandItem().getItem() instanceof GunItem gunItem) {
            SimulatedGun simGun = new HeldSimulatedGun(gunItem);
            brain.setMemory(ModBrainMemories.SIMULATED_GUN.get(), simGun);
            brain.setMemory(ModBrainMemories.APPROACH_DIST.get(), simGun.getIdealRange());
            brain.setMemory(ModBrainMemories.WEAPON_RANGE.get(), simGun.getMaxRange());
        }
    }
}
