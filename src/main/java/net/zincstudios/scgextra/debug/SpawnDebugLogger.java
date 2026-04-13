
package net.zincstudios.scgextra.debug;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.zincstudios.scgextra.SCGExtra;

public final class SpawnDebugLogger {
    private static final boolean ENABLED = true;
    private static final double MAX_PLAYER_TRACK_DISTANCE = 256.0;

    private SpawnDebugLogger() {
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!ENABLED) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof Mob mob)) return;

        Vec3 mobPos = mob.position();
        Player nearestPlayer = level.getNearestPlayer(mobPos.x, mobPos.y, mobPos.z, MAX_PLAYER_TRACK_DISTANCE, false);
        if (!(nearestPlayer instanceof ServerPlayer serverPlayer)) {
            SCGExtra.LOGGER.info(
                "[SPAWN TRACE] entity={} mobPos={} nearestPlayer=none",
                mob.getType().toShortString(),
                fmtVec(mobPos)
            );
            return;
        }

        Vec3 playerPos = serverPlayer.position();
        double dx = mobPos.x - playerPos.x;
        double dz = mobPos.z - playerPos.z;
        double dist2D = Math.sqrt(dx * dx + dz * dz);
        double dist3D = mobPos.distanceTo(playerPos);

        SCGExtra.LOGGER.info(
            "[SPAWN TRACE] entity={} mobPos={} player={} playerPos={} dist2D={}m dist3D={}m playerCreative={} playerSpectator={}",
            mob.getType().toShortString(),
            fmtVec(mobPos),
            serverPlayer.getGameProfile().getName(),
            fmtVec(playerPos),
            fmtDistance(dist2D),
            fmtDistance(dist3D),
            serverPlayer.isCreative(),
            serverPlayer.isSpectator()
        );
    }

    private static String fmtDistance(double value) {
        return String.format("%.2f", value);
    }

    private static String fmtVec(Vec3 vec) {
        return String.format("(%.2f, %.2f, %.2f)", vec.x, vec.y, vec.z);
    }
}
