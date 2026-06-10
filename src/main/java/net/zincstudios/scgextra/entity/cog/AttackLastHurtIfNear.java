package net.zincstudios.scgextra.entity.cog;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

import java.util.function.BiPredicate;

public class AttackLastHurtIfNear {

    public static <E extends Mob> BehaviorControl<E> create(boolean prioritizePlayers) {
        return create((mob, target) -> true, prioritizePlayers);
    }

    // Maybe just extend the BehaviorControl tbh
    public static <E extends Mob> BehaviorControl<E> create(BiPredicate<E, LivingEntity> shouldAttack, boolean prioritizePlayers) {
        return BehaviorBuilder.create(builder ->
                builder.group(
                        builder.present(MemoryModuleType.ATTACK_TARGET),
                        builder.present(MemoryModuleType.HURT_BY_ENTITY),
                        builder.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
                ).apply(builder, (targetAcc, hurterAcc, lastReachAcc) ->
                        ((level, mob, gameTime) -> {
                            LivingEntity hurter = builder.get(hurterAcc);
                            LivingEntity target = builder.get(targetAcc);
                            if (hurter == target) return false;
                            if (!mob.canAttack(hurter)) return false;
                            if (!shouldAttack.test(mob, hurter)) return false;
                            if (prioritizePlayers) {
                                if (target instanceof Player && !(hurter instanceof Player)) return false;

                                if (!(target instanceof Player) && hurter instanceof Player) {
                                    LivingChangeTargetEvent changeTargetEvent = ForgeHooks.onLivingChangeTarget(mob, hurter, LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
                                    if (changeTargetEvent.isCanceled()) return false;
                                    targetAcc.set(hurter);
                                    lastReachAcc.erase();
                                    return true;
                                }
                            }

                            if (mob.distanceToSqr(hurter) < mob.distanceToSqr(target)) {
                                LivingChangeTargetEvent changeTargetEvent = ForgeHooks.onLivingChangeTarget(mob, hurter, LivingChangeTargetEvent.LivingTargetType.BEHAVIOR_TARGET);
                                if (changeTargetEvent.isCanceled()) return false;
                                targetAcc.set(hurter);
                                lastReachAcc.erase();
                                return true;
                            }

                            return false;
                        }
                ))
        );
    }

}
