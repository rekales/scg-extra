package net.zincstudios.scgextra.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.neutral.head_hunter.HeadHunterEntity;
import top.ribs.scguns.entity.monster.FinforcerEntity;

public class EntityAdjustments {

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof WitherSkeleton witherSkeleton
                && event.getLevel() instanceof ServerLevel serverLevel
                && serverLevel.dimension().equals(Level.NETHER)
                && serverLevel.structureManager()
                .getStructureWithPieceAt(witherSkeleton.blockPosition(), BuiltinStructures.FORTRESS).isValid()
                && witherSkeleton.getRandom().nextFloat() * 100.0F < CommonConfig.spawnChanceHeadHunterFortressReplace) {
            HeadHunterEntity headHunter = ModEntities.HEAD_HUNTER.get().create(serverLevel);
            if (headHunter != null) {
                headHunter.moveTo(witherSkeleton.getX(), witherSkeleton.getY(), witherSkeleton.getZ(),
                        witherSkeleton.getYRot(), witherSkeleton.getXRot());
                headHunter.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(witherSkeleton.blockPosition()),
                        MobSpawnType.NATURAL, null, null);
                event.setCanceled(true);
                serverLevel.addFreshEntity(headHunter);
                return;
            }
        }

        if (event.getEntity() instanceof FinforcerEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }
    }

}
