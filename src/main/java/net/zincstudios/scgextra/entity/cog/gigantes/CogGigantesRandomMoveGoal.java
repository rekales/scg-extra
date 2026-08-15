package net.zincstudios.scgextra.entity.cog.gigantes;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CogGigantesRandomMoveGoal extends Goal {
    private final CogGigantesEntity mob;
    private int tickDelay;

    public CogGigantesRandomMoveGoal(CogGigantesEntity mob, int initialDelay) {
        this.mob = mob;
        this.tickDelay = initialDelay;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        return --this.tickDelay <= 0;
    }

    public void start() {
        this.setNewWanderTarget();
        this.tickDelay = 100;
    }

    private void setNewWanderTarget() {
        double x = this.mob.getX() + (this.mob.getRandom().nextDouble() * (double)20.0F - (double)10.0F);
        double y = this.mob.getY() + (this.mob.getRandom().nextDouble() * (double)20.0F - (double)10.0F);
        double z = this.mob.getZ() + (this.mob.getRandom().nextDouble() * (double)20.0F - (double)10.0F);
        this.mob.getMoveControl().setWantedPosition(x, y, z, 1.0F);
    }
}