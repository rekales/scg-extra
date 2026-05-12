package net.zincstudios.scgextra.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.zincstudios.scgextra.entity.common.ai.HurtByNonFactionGoal;
import top.ribs.scguns.entity.monster.*;

public class EntityAdjustments {

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof FinforcerEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }

        if (event.getEntity() instanceof DissidentEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }
        if (event.getEntity() instanceof AdjudicatorEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }
        if (event.getEntity() instanceof SubjugatorEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }
        if (event.getEntity() instanceof PraetorEntity mob) {
            mob.targetSelector.getAvailableGoals().removeIf(
                    goal -> goal.getGoal() instanceof HurtByTargetGoal);
            mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(mob));
            mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                    entity -> Faction.isEnemies(mob, entity)));
        }
    }

}
