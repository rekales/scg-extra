package net.zincstudios.scgextra.raid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class WaveRaidUtil {

    public static final int FIND_SPAWN_LOCATION_ATTEMPTS = 40;
    public static final double MIN_SPAWN_PLAYER_DISTANCE = 32;

    public static Component getBossBarLabel(WaveRaidData raidData, int currentWave) {
//        if (raidData.isFinalWave(currentWave) && this.raiderUUIDs.size() == 1) {
//            UUID bossId = this.raiderUUIDs.iterator().next();
//            Entity entity = this.level.getEntity(bossId);
//            if (entity instanceof LivingEntity bossEntity) {
//                return bossEntity.getDisplayName();
//            }
//        }

        String wave = switch (currentWave) {
            case 0 -> "wave_1";
            case 1 -> "wave_2";
            case 2 -> "wave_3";
            case 3 -> "wave_4";
            case 4 -> "wave_5";
            case 5 -> "wave_6";
            case 6 -> "wave_7";
            case 7 -> "wave_8";
            case 8 -> "wave_9";
            default -> "";
        };
        if (raidData.isFinalWave(currentWave)) wave = "last_wave";

        return Component.translatable(SCGExtra.MOD_ID+".raid.label."+raidData.id())
                .append(" ")
                .append(Component.translatable(SCGExtra.MOD_ID+".raid.label."+wave));
    }

    public static void announceToNearbyPlayers(ServerLevel level, Component message, Vec3 pos, double radius) {
        for(ServerPlayer player : level.getPlayers((player) -> player.position().distanceTo(pos) <= radius)) {
            player.sendSystemMessage(message);
        }
    }

    public static @Nullable ServerPlayer findNearestPlayer(ServerLevel level, Vec3 pos, float radius) {
        ServerPlayer nearest = null;
        double nearestDist = radius;

        for(ServerPlayer player : level.players()) {
            if (!player.isSpectator() && !player.isCreative()) {
                double dist = player.position().distanceTo(pos);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = player;
                }
            }
        }

        return nearest;
    }

    // Copied from RaidManager#findRaidSpawnLocation
    @SuppressWarnings("deprecation")
    static @Nullable Vec3 findWaveSpawnLocation(ServerLevel level, Vec3 center, @Nullable Vec3 playerPos) {
        RandomSource random = level.getRandom();
        int playerY = (int)center.y;
        boolean isUnderground = playerY < 50 && !level.canSeeSky(BlockPos.containing(center));

        for(int attempt = 0; attempt < FIND_SPAWN_LOCATION_ATTEMPTS; ++attempt) {
            double angle = random.nextDouble() * Math.PI * 2.0F;
            double distance = 30.0F + random.nextDouble() * 14.0F;
            double x = center.x + Math.cos(angle) * distance;
            double z = center.z + Math.sin(angle) * distance;
            BlockPos pos = new BlockPos((int)x, playerY, (int)z);
            BlockPos groundPos;
            if (isUnderground) {
                groundPos = findNearestValidCaveSpawn(level, pos, playerY);
                if (groundPos == null) {
                    continue;
                }
            } else {
                groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
                if (playerPos != null) {
                    if (playerPos.closerThan(Vec3.atCenterOf(groundPos), MIN_SPAWN_PLAYER_DISTANCE)) {
                        SCGExtra.LOGGER.warn("Too close: " + groundPos);
                        continue;
                    } else if (!playerPos.closerThan(Vec3.atCenterOf(groundPos), MIN_SPAWN_PLAYER_DISTANCE*1.5)) {
                        SCGExtra.LOGGER.warn("Too far: " + groundPos);
                        continue;
                    }
                }
            }
            if (level.getBlockState(groundPos.below()).isSolid()
                    && level.getBlockState(groundPos).isAir()
                    && level.getBlockState(groundPos.above()).isAir()
                    && level.getBlockState(groundPos.above(2)).isAir()) {
                return new Vec3((double)groundPos.getX() + (double)0.5F, groundPos.getY(), (double)groundPos.getZ() + (double)0.5F);
            }
        }

        return null;
    }

    // Copied from RaidManager#findNearestValidCaveSpawn
    @SuppressWarnings("deprecation")
    static @Nullable BlockPos findNearestValidCaveSpawn(ServerLevel level, BlockPos center, int playerY) {
        for(int yOffset = -5; yOffset <= 5; ++yOffset) {
            BlockPos checkPos = new BlockPos(center.getX(), playerY + yOffset, center.getZ());
            if (level.getBlockState(checkPos.below()).isSolid()
                    && level.getBlockState(checkPos).isAir()
                    && level.getBlockState(checkPos.above()).isAir()
                    && level.getBlockState(checkPos.above(2)).isAir()) {
                int airCount = 0;

                for(int i = 0; i < 4; ++i) {
                    if (level.getBlockState(checkPos.above(i)).isAir()) {
                        ++airCount;
                    }
                }

                if (airCount >= 3) {
                    return checkPos;
                }
            }
        }

        return null;
    }

    // Copied and altered from RaidManager#findHenchmanSpawnPos
    @SuppressWarnings({"deprecation", "SameParameterValue"})
    static Vec3 findMobSpawnPos(ServerLevel level, Vec3 center, double radius) {
        RandomSource random = level.getRandom();

        for(int attempt = 0; attempt < 10; ++attempt) {
            double angle = random.nextDouble() * Math.PI * (double)2.0F;
            double distance = random.nextDouble() * radius;
            double x = center.x + Math.cos(angle) * distance;
            double z = center.z + Math.sin(angle) * distance;
            BlockPos pos = new BlockPos((int)x, (int)center.y, (int)z);
            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if (level.getBlockState(groundPos.below()).isSolid()
                    && level.getBlockState(groundPos).isAir()
                    && level.getBlockState(groundPos.above()).isAir()) {
                return new Vec3((double)groundPos.getX() + (double)0.5F, groundPos.getY(), (double)groundPos.getZ() + (double)0.5F);
            }
        }

        return center;  // TODO: better fallback
    }
}
