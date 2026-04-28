package net.zincstudios.scgextra.raid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// because WaveRaidManager is getting too big
public class WaveRaidUtil {

    public static final int FIND_SPAWN_LOCATION_ATTEMPTS = 25;

    // Copied from RaidManager#findRaidSpawnLocation
    @SuppressWarnings("deprecation")
    @Nullable
    public static Vec3 findRaidSpawnLocation(ServerLevel level, Vec3 center) {
        RandomSource random = level.getRandom();
        int playerY = (int)center.y;
        boolean isUnderground = playerY < 50;

        for(int attempt = 0; attempt < FIND_SPAWN_LOCATION_ATTEMPTS; ++attempt) {
            double angle = random.nextDouble() * Math.PI * (double)2.0F;
            double distance = (double)25.0F + random.nextDouble() * (double)15.0F;
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
            }

            if (level.getBlockState(groundPos.below()).isSolid() && level.getBlockState(groundPos).isAir() && level.getBlockState(groundPos.above()).isAir() && level.getBlockState(groundPos.above(2)).isAir()) {
                return new Vec3((double)groundPos.getX() + (double)0.5F, groundPos.getY(), (double)groundPos.getZ() + (double)0.5F);
            }
        }

        return null;
    }

    // Copied from RaidManager#findRaidSpawnLocation
    @SuppressWarnings("deprecation")
    @Nullable
    static Vec3 findWaveSpawnLocation(ServerLevel level, Vec3 center) {
        RandomSource random = level.getRandom();
        int playerY = (int)center.y;
        boolean isUnderground = playerY < 50;

        for(int attempt = 0; attempt < FIND_SPAWN_LOCATION_ATTEMPTS; ++attempt) {
            double angle = random.nextDouble() * Math.PI * (double)2.0F;
            double distance = (double)25.0F + random.nextDouble() * (double)15.0F;
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
            }

            if (level.getBlockState(groundPos.below()).isSolid() && level.getBlockState(groundPos).isAir() && level.getBlockState(groundPos.above()).isAir() && level.getBlockState(groundPos.above(2)).isAir()) {
                return new Vec3((double)groundPos.getX() + (double)0.5F, groundPos.getY(), (double)groundPos.getZ() + (double)0.5F);
            }
        }

        return null;
    }

    // Copied from RaidManager#findNearestValidCaveSpawn
    @SuppressWarnings("deprecation")
    @Nullable
    static BlockPos findNearestValidCaveSpawn(ServerLevel level, BlockPos center, int playerY) {
        for(int yOffset = -5; yOffset <= 5; ++yOffset) {
            BlockPos checkPos = new BlockPos(center.getX(), playerY + yOffset, center.getZ());
            if (level.getBlockState(checkPos.below()).isSolid() && level.getBlockState(checkPos).isAir() && level.getBlockState(checkPos.above()).isAir() && level.getBlockState(checkPos.above(2)).isAir()) {
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
    static Vec3 findMobSpawnPos(ServerLevel level, @Nonnull Vec3 center, double radius) {
        RandomSource random = level.getRandom();

        for(int attempt = 0; attempt < 10; ++attempt) {
            double angle = random.nextDouble() * Math.PI * (double)2.0F;
            double distance = random.nextDouble() * radius;
            double x = center.x + Math.cos(angle) * distance;
            double z = center.z + Math.sin(angle) * distance;
            BlockPos pos = new BlockPos((int)x, (int)center.y, (int)z);
            BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if (level.getBlockState(groundPos.below()).isSolid() && level.getBlockState(groundPos).isAir() && level.getBlockState(groundPos.above()).isAir()) {
                return new Vec3((double)groundPos.getX() + (double)0.5F, groundPos.getY(), (double)groundPos.getZ() + (double)0.5F);
            }
        }

        return center;  // TODO: better fallback
    }

    // Copied and adjusted from Fassiss' old raid wave code
//    private static Vec3 findRaidSpawnCenter2(
//            RaidManagerInvoker invoker,
//            ActiveRaid raid,
//            ServerLevel level,
//            Vec3 raidOrigin,
//            RaidConfig.HenchmenData henchmenData,
//            RandomSource random
//    ) {
//        int raidSpawnRadius = scaledRaidSpawnRadius(RAID_SPAWN_RADIUS, RAID_SPAWN_RADIUS_MIN, RAID_SPAWN_RADIUS_MULTIPLIER);
//        int outerRadius = Math.max(GROUP_MAX_DISTANCE_FROM_ORIGIN, raidSpawnRadius);
//        for (int i = 0; i < GROUP_CENTER_FIND_ATTEMPTS; i++) {
//            double angle = random.nextDouble() * Math.PI * 2.0;
//            double distance = GROUP_MIN_DISTANCE_FROM_ORIGIN + random.nextDouble() * (GROUP_MAX_DISTANCE_FROM_ORIGIN - GROUP_MIN_DISTANCE_FROM_ORIGIN);
//            Vec3 probe = new Vec3(
//                    raidOrigin.x + Math.cos(angle) * distance,
//                    raidOrigin.y,
//                    raidOrigin.z + Math.sin(angle) * distance
//            );
//            Vec3 center = this.findSpawnPosWithFallback(invoker, raid, level, probe, GROUP_HALF_SIZE_BLOCKS, random);
//            if (this.isWithinGroupBand(center, raidOrigin) && this.isOutsidePlayerSafetyRadius(level, center)) return center;
//            if (DEBUG_RAID_SPAWN_LOGS && i < 8) {
//                SCGExtra.LOGGER.debug("[SCGEXTRA RAID] group center reject (ring attempt {}) raid={} uuid={} probe={} got={} dist={} nearestPlayerDist={}",
//                        i + 1, raid.getConfig().raidId(), raid.getRaidId(),
//                        this.scgextra$fmtVec(probe), this.scgextra$fmtVec(center),
//                        center == null ? "null" : this.scgextra$fmtDistance(this.scgextra$distance2D(center, raidOrigin)),
//                        center == null ? "null" : this.scgextra$fmtDistance(this.nearestValidPlayerDistance2D(level, center)));
//            }
//        }
//        for (int i = 0; i < 8; i++) {
//            Vec3 fallback = this.findSpawnPosWithFallback(invoker, raid, level, raidOrigin, outerRadius, random);
//            if (this.isWithinGroupBand(fallback, raidOrigin) && this.isOutsidePlayerSafetyRadius(level, fallback)) return fallback;
//            if (DEBUG_RAID_SPAWN_LOGS) {
//                SCGExtra.LOGGER.debug("[SCGEXTRA RAID] group center reject (fallback {}) raid={} uuid={} got={} dist={} nearestPlayerDist={}",
//                        i + 1, raid.getConfig().raidId(), raid.getRaidId(),
//                        this.scgextra$fmtVec(fallback),
//                        fallback == null ? "null" : this.scgextra$fmtDistance(this.scgextra$distance2D(fallback, raidOrigin)),
//                        fallback == null ? "null" : this.scgextra$fmtDistance(this.nearestValidPlayerDistance2D(level, fallback)));
//            }
//        }
//        return null;
//    }
}
