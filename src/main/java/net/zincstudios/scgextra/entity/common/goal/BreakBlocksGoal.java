package net.zincstudios.scgextra.entity.common.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BreakBlocksGoal extends Goal{
    private final PathfinderMob mob;
    private long lastPosCheck = 0;
    public static final int POS_CHECK_INTERVAL = 10;
    public static final double LAST_POS_DISTANCE_THRESHOLD = 0.5;
    public static final double TARGET_DISTANCE_THRESHOLD = 12;
    public static final double HEIGHT_DIFF_THRESHOLD = 2;
    private Vec3 lastPos = Vec3.ZERO;
    private int frustration = 0;
    private final int FRUSTRATION_THRESHOLD;

    public BreakBlocksGoal(PathfinderMob entity, int maxFrustration){
        this.mob = entity;
        this.FRUSTRATION_THRESHOLD = maxFrustration;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getTarget()==null)return false;
        if(this.mob.level().getGameTime()-this.lastPosCheck > POS_CHECK_INTERVAL) {
            if (
                this.lastPos.closerThan(this.mob.position(), LAST_POS_DISTANCE_THRESHOLD) 
            ){
                this.frustration += 5;
            } else {
                this.frustration -= 1;
            }

            this.lastPos = this.mob.position();
            this.lastPosCheck = this.mob.tickCount;
        }
        return this.frustration > FRUSTRATION_THRESHOLD;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mob.level().getGameTime() % 10 == 0 && !this.mob.level().isClientSide()) {
            AABB breakBB = this.getBreakBB(this.mob, this.mob.getTarget());
            this.breakBlocks((ServerLevel)this.mob.level(), this.mob, breakBB);
            this.stop();
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.frustration = 0;
        this.lastPos = Vec3.ZERO;
        this.lastPosCheck = 0;
    }
    public void breakBlocks(ServerLevel level, LivingEntity entity, AABB aabb) {
        BlockPos min = new BlockPos(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ));
        BlockPos max = new BlockPos(Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ));

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {            
            if (canBreak(level, pos)) {
                level.destroyBlock(pos, true, entity);
            }
        }
    }

    protected AABB getBreakBB(LivingEntity entity, LivingEntity target) {
        Vec3 direction = target.position().subtract(entity.position()).normalize();
        Vec3 xzOffset = new Vec3(direction.x, 0, direction.z).normalize();

        AABB breakBB = entity.getBoundingBox();
        breakBB = breakBB.move(xzOffset);

        double pitchDegrees = Math.toDegrees(Math.asin(direction.y));
        if (target.position().y-entity.position().y >= HEIGHT_DIFF_THRESHOLD) {
            breakBB = breakBB.move(0,1.1,0);
        } else if (pitchDegrees < -45.0) {
            breakBB = breakBB.move(0,-0.9,0);
        }

        return breakBB;
    }
    private static boolean canBreak(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return false;
        if (hardness >= 10) return false;

        

        return (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_AXE))
                && !state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }
}
