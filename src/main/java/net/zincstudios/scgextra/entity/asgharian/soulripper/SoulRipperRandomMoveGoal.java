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

        for(int i = 0; i < 3; ++i) {
            blockpos = blockpos.offset(this.mob.getRandom().nextInt(11) - 5, this.mob.getRandom().nextInt(10) - 2, this.mob.getRandom().nextInt(11) - 5);
            if (this.mob.level().isEmptyBlock(blockpos)) {
                this.mob.getMoveControl().setWantedPosition(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, 0.25D);
                if (this.mob.getTarget() == null) {
                    this.mob.getLookControl().setLookAt(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, 180.0F, 20.0F);
                }
                break;
            }
        }

    }
}
