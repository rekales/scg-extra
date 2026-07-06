package net.zincstudios.scgextra.entity.wreckers.wrecker_jumbo;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WreckerJumboDigGoal extends Goal {

    private static final int DIG_DURATION_TICKS = 8;
    private static final int DIG_BREAK_TICK = 4;
    private static final int DIG_COOLDOWN_TICKS = 12;
    private static final int MAX_BLOCKS_PER_DIG = 6;

    private final WreckerJumboEntity mob;
    private int digCooldown;
    private int digTimer;
    private final List<BlockPos> digTargets = new ArrayList<>();
    private boolean dug;

    public WreckerJumboDigGoal(WreckerJumboEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.digCooldown > 0) {
            this.digCooldown--;
            return false;
        }

        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (!this.mob.horizontalCollision) {
            return false;
        }

        this.collectDigTargets();
        return !this.digTargets.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && !this.digTargets.isEmpty() && this.digTimer > 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.digTimer = DIG_DURATION_TICKS;
        this.dug = false;
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.triggerAnim("attack", "attack");
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();

        if (this.digTimer == DIG_BREAK_TICK && !this.dug) {
            this.dug = true;
            int broken = 0;
            for (BlockPos pos : this.digTargets) {
                if (broken >= MAX_BLOCKS_PER_DIG) {
                    break;
                }
                if (this.canDig(pos) && this.mob.level().destroyBlock(pos, false, this.mob)) {
                    broken++;
                }
            }
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
        this.digTimer--;
    }

    @Override
    public void stop() {
        this.digCooldown = DIG_COOLDOWN_TICKS;
        this.digTimer = 0;
        this.digTargets.clear();
        this.dug = false;
    }

    private void collectDigTargets() {
        this.digTargets.clear();

        Vec3 look = this.mob.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);
        if (forward.lengthSqr() < 1.0E-4D) {
            return;
        }
        forward = forward.normalize();
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
        Vec3 front = this.mob.position().add(forward.scale(this.mob.getBbWidth() * 0.5D + 0.9D));

        for (int dy = 0; dy <= 2; dy++) {
            for (int side = -1; side <= 1; side++) {
                Vec3 samplePos = front.add(right.scale(side * 0.9D));
                BlockPos pos = BlockPos.containing(samplePos.x, this.mob.getY() + 0.5D + dy, samplePos.z);
                if (this.canDig(pos) && !this.digTargets.contains(pos)) {
                    this.digTargets.add(pos);
                }
            }
        }
    }

    private boolean canDig(BlockPos pos) {
        BlockState state = this.mob.level().getBlockState(pos);

        if (state.isAir() || state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        }
        float destroySpeed = state.getDestroySpeed(this.mob.level(), pos);
        return destroySpeed >= 0.0F && destroySpeed <= 50.0F;
    }
}
