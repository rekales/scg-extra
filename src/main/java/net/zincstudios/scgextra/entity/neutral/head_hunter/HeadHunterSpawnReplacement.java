package net.zincstudios.scgextra.entity.neutral.head_hunter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.neutral.NeutralCombatUtil;
import net.zincstudios.scgextra.entity.neutral.NeutralEntities;

public final class HeadHunterSpawnReplacement {
    private HeadHunterSpawnReplacement() {
    }

    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!(event.getEntity() instanceof WitherSkeleton witherSkeleton)) {
            return;
        }

        MobSpawnType spawnType = event.getSpawnType();
        if (NeutralCombatUtil.isManualSpawn(spawnType)) {
            return;
        }

        ServerLevelAccessor level = event.getLevel();
        if (!level.getLevel().dimension().equals(Level.NETHER)) {
            return;
        }

        BlockPos pos = witherSkeleton.blockPosition();
        boolean inFortress = level.getLevel().structureManager()
                .getStructureWithPieceAt(pos, BuiltinStructures.FORTRESS)
                .isValid();
        if (!inFortress) {
            return;}



        if (!NeutralCombatUtil.passesSpawnChance(witherSkeleton.getRandom(), CommonConfig.spawnChanceHeadHunterFortressReplace)) {
            return;
        }

        HeadHunterEntity replacement = NeutralEntities.HEAD_HUNTER.get().create(level.getLevel());
        if (replacement == null) {
            return;}

        replacement.moveTo(
                witherSkeleton.getX(),
                witherSkeleton.getY(),
                witherSkeleton.getZ(),
                witherSkeleton.getYRot(),
                witherSkeleton.getXRot()
        );
        replacement.setYBodyRot(witherSkeleton.yBodyRot);
        replacement.setYHeadRot(witherSkeleton.getYHeadRot());
        replacement.finalizeSpawn(level, event.getDifficulty(), spawnType, null, null);
        level.getLevel().addFreshEntity(replacement);
        event.setSpawnCancelled(true);
    }
}

