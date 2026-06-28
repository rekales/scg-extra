package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.AABB;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.ModBrainMemories;

import java.util.ArrayList;
import java.util.List;

// Doesn't rely on sensors because of performance issues when checking entities in a larger radius
public class CheckShouldAlert extends Behavior<LivingEntity> {

    private final int alertDuration;
    private final int alertCooldown;
    private final float radius;

    private long cooldownEnd = 0;  // I highly doubt other behaviors needs this, turn to a memory if so.
    private boolean firstAlert = true;

    public CheckShouldAlert() {
        this(10);
    }

    public CheckShouldAlert(int alertDuration) {
        this(alertDuration, 100, 64);
    }

    public CheckShouldAlert(int alertDuration, int alertCooldown, float radius) {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                ModBrainMemories.TO_ALERT.get(), MemoryStatus.VALUE_ABSENT
        ), 600);
        this.alertDuration = alertDuration;
        this.alertCooldown = alertCooldown;
        this.radius = radius;
    }

    public CheckShouldAlert noAlertOnAggro() {
        this.firstAlert = false;
        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity entity) {
        if (this.cooldownEnd > level.getGameTime()) return false;
        if (!this.firstAlert && level.getGameTime() % 40 != 1) return false;

        SCGExtra.LOGGER.debug("checking alert: ");

        Brain<?> brain = entity.getBrain();
        AABB aabb = entity.getBoundingBox().inflate(this.radius, this.radius/2, this.radius);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                other -> other != entity && other.isAlive() && Faction.isFriendlies(entity, other)
        );

        SCGExtra.LOGGER.debug("to alert: " + entities);

        List<LivingEntity> toAlert = new ArrayList<>();
        for (LivingEntity other : entities) {
            if (brain.checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)) {
                toAlert.add(other);
            } else if (other instanceof Mob mob && mob.getTarget() == null) {
                toAlert.add(other);
            }
        }

        if (this.firstAlert) {
            this.firstAlert = false;
            toAlert.add(entity); // can't put empty list on memory for some reason, so put self
        }

        if (!toAlert.isEmpty()) {
            brain.setMemoryWithExpiry(ModBrainMemories.TO_ALERT.get(), toAlert, this.alertDuration);
            this.cooldownEnd = level.getGameTime() + this.alertDuration + this.alertCooldown;
            return true;
        }

        return false;
    }
}
