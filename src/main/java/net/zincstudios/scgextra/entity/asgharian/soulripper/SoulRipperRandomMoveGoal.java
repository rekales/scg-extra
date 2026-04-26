package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SoulRipperRandomMoveGoal extends Goal {

    protected final SoulRipperEntity mob;
    // TODO: bound block here

    public SoulRipperRandomMoveGoal(SoulRipperEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return !this.mob.getMoveControl().hasWanted()
                && this.mob.getRandom().nextInt(reducedTickDelay(7)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {
        BlockPos blockpos = this.mob.getBoundOrigin();
        if (blockpos == null) {
            blockpos = this.mob.blockPosition();
        }

        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.offset(this.mob.getRandom().nextInt(11) - 5, this.mob.getRandom().nextInt(8) - 2, this.mob.getRandom().nextInt(11) - 5);
            if (this.mob.level().isEmptyBlock(blockpos1)) {
                this.mob.getMoveControl().setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                if (this.mob.getTarget() == null) {
                    this.mob.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                }
                break;
            }
        }

    }
}
