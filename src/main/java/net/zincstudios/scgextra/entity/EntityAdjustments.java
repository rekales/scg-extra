package net.zincstudios.scgextra.entity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.goal.MobHurtByNonFactionGoal;
import top.ribs.scguns.entity.monster.*;
import top.ribs.scguns.init.ModEntities;

import java.util.Map;
import java.util.function.Consumer;

public class EntityAdjustments {

    private static final Map<EntityType<?>, Consumer<Mob>> GOAL_ADJUSTMENT_HANDLERS = ImmutableMap.of(
            ModEntities.FINFORCER.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.DISSIDENT.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.ADJUDICATOR.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.SUBJUGATOR.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.PRAETOR.get(), EntityAdjustments::praetorGoalAdjustment,
            ModEntities.COG_MINION.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.COG_KNIGHT.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.TRAUMA_UNIT.get(), EntityAdjustments::basicGoalAdjustment,
            ModEntities.SKY_CARRIER.get(), EntityAdjustments::skyCarrierGoalAdjustment,
            ModEntities.SCAMP_TANK.get(), EntityAdjustments::basicGoalAdjustment
    );

    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            Consumer<Mob> handler = GOAL_ADJUSTMENT_HANDLERS.get(mob.getType());
            if (handler != null) handler.accept(mob);
        }
    }

    private static void basicGoalAdjustment(Mob mob) {
        if (!(mob instanceof PathfinderMob pathfinderMob)) return;

        mob.targetSelector.getAvailableGoals().removeIf(
                goal -> goal.getGoal() instanceof HurtByTargetGoal);
        mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(pathfinderMob));
        mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                entity -> Faction.isEnemies(mob, entity)));
    }

    private static void praetorGoalAdjustment(Mob mob) {
        if (!(mob instanceof PraetorEntity pathfinderMob)) return;

        mob.targetSelector.getAvailableGoals().removeIf(
                goal -> goal.getGoal() instanceof HurtByTargetGoal);
        mob.targetSelector.addGoal(1, new HurtByNonFactionGoal(pathfinderMob));
        mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                entity -> Faction.isEnemies(mob, entity)));
    }

    private static void skyCarrierGoalAdjustment(Mob mob) {
        if (!(mob instanceof SkyCarrierEntity)) return;

        mob.targetSelector.getAvailableGoals().removeIf(
                goal -> goal.getGoal() instanceof HurtByTargetGoal);
        mob.targetSelector.addGoal(0, new MobHurtByNonFactionGoal(mob));
        mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true,
                entity -> Faction.isEnemies(mob, entity)));
    }
}
