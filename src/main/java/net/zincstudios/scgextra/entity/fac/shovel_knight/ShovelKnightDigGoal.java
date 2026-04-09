package net.zincstudios.scgextra.entity.fac.shovel_knight;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ShovelKnightDigGoal extends Goal {

    private static final int DIG_DURATION_TICKS = 4;
    private static final int DIG_BREAK_TICK = 2;
    private static final int DIG_COOLDOWN_TICKS = 10;

    private final PathfinderMob mob;
    private int digCooldown;
    private int digTimer;
    private BlockPos digTargetPos;
    private boolean dug;

    public ShovelKnightDigGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.digCooldown > 0) {
            this.digCooldown--;
            this.digTargetPos = null;
            return false;
        }

        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (!this.mob.horizontalCollision) {
            return false;
        }

        BlockPos frontPos = BlockPos.containing(
                this.mob.getX() + this.mob.getLookAngle().x * 0.9,
                this.mob.getY() + 0.6,
                this.mob.getZ() + this.mob.getLookAngle().z * 0.9
        );
        BlockPos abovePos = frontPos.above();

        if (this.canDig(frontPos)) {
            this.digTargetPos = frontPos;
            return true;
        }
        if (this.canDig(abovePos)) {
            this.digTargetPos = abovePos;
            return true;
        }
        this.digTargetPos = null;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.digTargetPos != null && this.digTimer > 0;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.digTimer = DIG_DURATION_TICKS;
        this.dug = false;
        this.mob.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();

        if (this.digTimer == DIG_BREAK_TICK && !this.dug && this.digTargetPos != null) {
            this.dug = this.tryDig(this.digTargetPos);
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
        this.digTimer--;
    }

    @Override
    public void stop() {
        this.digCooldown = DIG_COOLDOWN_TICKS;
        this.digTimer = 0;
        this.digTargetPos = null;
        this.dug = false;
    }

    private boolean canDig(BlockPos pos) {
        BlockState state = this.mob.level().getBlockState(pos);

        if (state.isAir() || !state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return false;
        }
        return state.getDestroySpeed(this.mob.level(), pos) >= 0.0F;
    }

    private boolean tryDig(BlockPos pos) {
        if (!this.canDig(pos)) {
            return false;
        }
        return this.mob.level().destroyBlock(pos, false, this.mob);
    }
}
