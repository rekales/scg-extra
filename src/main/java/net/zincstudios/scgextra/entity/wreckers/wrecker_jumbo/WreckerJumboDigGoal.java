package net.zincstudios.scgextra.entity.wreckers.wrecker_jumbo;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WreckerJumboDigGoal extends Goal {

    private static final int POS_CHECK_INTERVAL = 10;
    private static final double LAST_POS_DISTANCE_THRESHOLD = 0.5D;
    private static final double TARGET_DISTANCE_THRESHOLD = 16.0D;
    private static final double HEIGHT_DIFF_THRESHOLD = 2.0D;
    private static final int DIG_INTERVAL_TICKS = 10;
    private static final int MAX_BLOCKS_PER_DIG = 12;

    private final WreckerJumboEntity mob;
    private Vec3 lastPos = Vec3.ZERO;
    private int lastPosCheck;
    private int frustration;

    public WreckerJumboDigGoal(WreckerJumboEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (this.mob.tickCount - this.lastPosCheck > POS_CHECK_INTERVAL) {
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.position().closerThan(this.mob.position(), TARGET_DISTANCE_THRESHOLD)) {
                if (this.lastPos.closerThan(this.mob.position(), LAST_POS_DISTANCE_THRESHOLD)) {
                    this.frustration += 5;
                }
                if (this.mob.horizontalCollision) {
                    this.frustration += 5;
                }
                if (!this.lastPos.closerThan(this.mob.position(), LAST_POS_DISTANCE_THRESHOLD) && !this.mob.horizontalCollision) {
                    this.frustration -= 1;
                }
            } else {
                this.frustration -= 1;
            }
            this.frustration = Mth.clamp(this.frustration, 0, 60);

            this.lastPos = this.mob.position();
            this.lastPosCheck = this.mob.tickCount;
        }

        return this.frustration > 40;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (this.mob.tickCount % DIG_INTERVAL_TICKS == 0 && target != null) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.triggerAnim("attack", "attack");
            this.breakBlocks(target);
        }
    }

    private void breakBlocks(LivingEntity target) {
        Level level = this.mob.level();
        AABB aabb = this.getBreakBB(target);
        BlockPos min = new BlockPos(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ));
        BlockPos max = new BlockPos(Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ));

        int broken = 0;
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (this.canDig(pos) && level.destroyBlock(pos, false, this.mob) && ++broken >= MAX_BLOCKS_PER_DIG) {
                return;
            }
        }
    }

    private AABB getBreakBB(LivingEntity target) {
        Vec3 direction = target.position().subtract(this.mob.position()).normalize();
        Vec3 xzOffset = new Vec3(direction.x, 0.0D, direction.z).normalize();

        AABB breakBB = this.mob.getBoundingBox().move(xzOffset);

        double pitchDegrees = Math.toDegrees(Math.asin(direction.y));
        if (target.position().y - this.mob.position().y >= HEIGHT_DIFF_THRESHOLD) {
            breakBB = breakBB.move(0.0D, 1.1D, 0.0D);
        } else if (pitchDegrees < -45.0D) {
            breakBB = breakBB.move(0.0D, -0.9D, 0.0D);
        }

        return breakBB;
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
