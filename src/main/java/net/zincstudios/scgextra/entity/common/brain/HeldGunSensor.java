package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.gun.HeldScorchedSimGun;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import top.ribs.scguns.item.GunItem;

import java.util.Optional;
import java.util.Set;

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
                ModBrainMemories.WEAPON_IDEAL_RANGE.get(),
                ModBrainMemories.WEAPON_MAX_RANGE.get()
        );
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<SimulatedGun> optional = brain.getMemory(ModBrainMemories.SIMULATED_GUN.get());

        if ((optional.isEmpty() || optional.get().hasChanged(entity))
                && entity.getMainHandItem().getItem() instanceof GunItem) {
            SimulatedGun simGun = new HeldScorchedSimGun(entity.getMainHandItem());
            brain.setMemory(ModBrainMemories.SIMULATED_GUN.get(), simGun);
            brain.setMemory(ModBrainMemories.WEAPON_IDEAL_RANGE.get(), simGun.getIdealRange());
            brain.setMemory(ModBrainMemories.WEAPON_MAX_RANGE.get(), simGun.getMaxRange());
        }
    }
}
