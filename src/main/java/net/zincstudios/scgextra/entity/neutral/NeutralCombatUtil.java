package net.zincstudios.scgextra.entity.neutral;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import top.ribs.scguns.init.ModEffects;

import java.util.List;

public final class NeutralCombatUtil {
    private NeutralCombatUtil() {
    }

    public static void applyLacerate(LivingEntity target, int durationTicks) {
        target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), durationTicks));
    }

    public static List<Player> nearbyPlayers(Mob mob, double radius) {
        AABB box = mob.getBoundingBox().inflate(radius);
        return mob.level().getEntitiesOfClass(Player.class, box, p -> !p.isCreative() && !p.isSpectator());
    }

    public static boolean canSpawnEndSurface(LevelAccessor level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos.below());
        if (!ground.is(Blocks.END_STONE)) {
            return false;
        }

        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }

    public static boolean canSpawnEndSurfaceMob(LevelAccessor level, BlockPos pos, Entity entity) {
        if (!canSpawnEndSurface(level, pos)) {
            return false;
        }
        if (level instanceof Level vanillaLevel && vanillaLevel.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return level.noCollision(entity);
    }

    public static <T extends Monster> boolean canSpawnEndMonster(EntityType<T> type, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (spawnReason == MobSpawnType.SPAWN_EGG || spawnReason == MobSpawnType.COMMAND) {
            return true;
        }
        if (!Monster.checkMonsterSpawnRules(type, level, spawnReason, pos, random)) {
            return false;
        }
        return canSpawnEndSurface(level, pos);
    }
}
