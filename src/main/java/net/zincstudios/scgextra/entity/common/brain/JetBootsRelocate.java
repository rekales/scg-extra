package net.zincstudios.scgextra.entity.common.brain;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.ModBrainMemories;

public class JetBootsRelocate {

    public static final float TOO_CLOSE_DIST = 4;
    public static final float TOO_FAR_DIST = 25;

    public static <E extends PathfinderMob> BehaviorControl<E> create() {
        return BehaviorBuilder.create(builder ->
                builder.group(
                        builder.present(MemoryModuleType.ATTACK_TARGET),
                        builder.present(ModBrainMemories.WEAPON_IDEAL_RANGE.get()),
                        builder.registered(ModBrainMemories.RELOCATE_TARGET.get()),
                        builder.absent(ModBrainMemories.JET_BOOTS_COOLING_DOWN.get())
                ).apply(builder, (targetAcc, rangeAcc, relocateAcc, cdAcc) ->
                        ((level, mob, gameTime) -> {
                            LivingEntity target = builder.get(targetAcc);
                            float idealRange = builder.get(rangeAcc);
                            float dist = mob.distanceTo(target);

                            if (dist < TOO_CLOSE_DIST) {
                                Vec3 targetPos = mob.getEyePosition().subtract(target.getEyePosition()).normalize().scale(12);
                                targetPos = targetPos
                                        .add(
                                        (level.getRandom().nextDouble() - 0.5) * 2,
                                        0,
                                        (level.getRandom().nextDouble() - 0.5) * 2
                                        ).add(mob.position());
                                BlockHitResult result = level.clip(new ClipContext(
                                        targetPos, targetPos.add(0, -4, 0),
                                        ClipContext.Block.COLLIDER,
                                        ClipContext.Fluid.ANY,
                                        null
                                ));

                                if (result.getType() == HitResult.Type.BLOCK) {
                                    relocateAcc.set(new BlockPosTracker(result.getBlockPos().above()));
                                    return true;
                                }
                            } else if (dist > idealRange + 10 || dist > TOO_FAR_DIST) {
                                Vec3 targetPos = target.getEyePosition()
                                        .subtract(mob.getEyePosition())
                                        .normalize()
                                        .scale(12)
                                        .add(mob.position());

                                BlockHitResult result = level.clip(new ClipContext(
                                        targetPos, targetPos.add(0, -4, 0),
                                        ClipContext.Block.COLLIDER,
                                        ClipContext.Fluid.ANY,
                                        null
                                ));

                                if (result.getType() == HitResult.Type.BLOCK) {
                                    relocateAcc.set(new BlockPosTracker(result.getBlockPos()));
                                    return true;
                                }
                            }

                            return false;
                        }))
        );
    }
}
