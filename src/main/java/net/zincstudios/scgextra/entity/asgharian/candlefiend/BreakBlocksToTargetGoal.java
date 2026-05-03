package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;

public class BreakBlocksToTargetGoal extends Goal {

    public static final int POS_CHECK_INTERVAL = 10;
    public static final double LAST_POS_DISTANCE_THRESHOLD = 0.5;
    public static final double TARGET_DISTANCE_THRESHOLD = 12;
    public static final double HEIGHT_DIFF_THRESHOLD = 2;

    protected final Mob mob;
    private Vec3 lastPos = Vec3.ZERO;
    private int lastPosCheck = 0;  // tickCount timestamp
    private int frustration = 0;

    public BreakBlocksToTargetGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (this.mob.tickCount-this.lastPosCheck > POS_CHECK_INTERVAL) {
            LivingEntity target = this.mob.getTarget();
            if (target != null && this.lastPos.closerThan(this.mob.position(), LAST_POS_DISTANCE_THRESHOLD)
                    && target.position().closerThan(this.mob.position(), TARGET_DISTANCE_THRESHOLD)){
                this.frustration += 5;
            } else {
                this.frustration -= 1;
                if (this.frustration > 50) {
                    this.frustration -= 2;
                }
            }
            this.frustration = Mth.clamp(this.frustration, 0, 80);

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
        if (this.mob.tickCount % 10 == 0 && this.mob.getTarget() != null) {
            this.breakBlocks(this.mob.getTarget());
        }
    }

    public void breakBlocks(LivingEntity target) {
        Level level = this.mob.level();
        AABB aabb = this.getBreakBB(target);
        BlockPos min = new BlockPos(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ));
        BlockPos max = new BlockPos(Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ));

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (canBreak(level, pos)) {
                level.destroyBlock(pos, true, this.mob);
            }
        }
    }

    protected AABB getBreakBB(LivingEntity target) {
        Vec3 direction = target.position().subtract(this.mob.position()).normalize();
        Vec3 xzOffset = new Vec3(direction.x, 0, direction.z).normalize();

        AABB breakBB = this.mob.getBoundingBox();

        SCGExtra.LOGGER.debug("original:" + breakBB.minY);

        breakBB = breakBB.move(xzOffset);

        SCGExtra.LOGGER.debug("moved:" + breakBB.minY);

        double pitchDegrees = Math.toDegrees(Math.asin(direction.y));
        if (target.position().y-this.mob.position().y >= HEIGHT_DIFF_THRESHOLD) {
            breakBB = breakBB.move(0,1.1,0);
            SCGExtra.LOGGER.debug("adjusted:" + breakBB.minY);
        } else if (pitchDegrees < -45.0) {
            breakBB = breakBB.move(0,-0.9,0);
            SCGExtra.LOGGER.debug("adjusted:" + breakBB.minY);
        }



        return breakBB;
    }

    protected boolean canBreak(Level level, BlockPos pos) {
        BlockState state = this.mob.level().getBlockState(pos);
        if (state.isAir()) return false;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return false;  // Unbreakable
        if (hardness >= 5) return false;  // Ex. Iron Block: 5, Stone: 1.5

        return !state.is(BlockTags.NEEDS_STONE_TOOL)
                && !state.is(BlockTags.NEEDS_IRON_TOOL)
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }
}
