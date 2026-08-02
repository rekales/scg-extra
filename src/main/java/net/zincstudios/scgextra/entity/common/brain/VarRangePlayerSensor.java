package net.zincstudios.scgextra.entity.common.brain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VarRangePlayerSensor extends Sensor<LivingEntity> {

    private final float radius;

    public VarRangePlayerSensor(int scanInterval, float radius) {
        super(scanInterval);
        this.radius = radius;
    }

    @Override
    protected void doTick(ServerLevel level, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        List<Player> players = level.players().stream()
                .filter(EntitySelector.NO_SPECTATORS)
                .filter(player -> entity.closerThan(player, this.radius))
                .sorted(Comparator.comparingDouble(entity::distanceToSqr))
                .collect(Collectors.toList());
        brain.setMemory(MemoryModuleType.NEAREST_PLAYERS, players);

        List<Player> visiblePlayers = players.stream()
                .filter(player -> isPlayerVisible(entity, player, this.radius))
                .toList();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, visiblePlayers.isEmpty() ? null : visiblePlayers.get(0));

        Optional<Player> optional = visiblePlayers.stream()
                .filter(player -> isPlayerAttackable(entity, player))
                .findFirst();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, optional);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of();
    }

    private static boolean isPlayerVisible(LivingEntity entity, Player player, float range) {
        double vis = player.getVisibilityPercent(entity);
        double adjRange = Math.max(range * vis, 2.0D);
        if (!entity.closerThan(player, adjRange)) return false;

        if (entity instanceof Mob mob) {
            return mob.getSensing().hasLineOfSight(player);
        } else {
            return entity.hasLineOfSight(player);
        }
    }

    private static boolean isPlayerAttackable(LivingEntity entity, Player player) {
        return entity.canAttack(player) && entity.canAttackType(player.getType()) && !entity.isAlliedTo(player);
    }
}
