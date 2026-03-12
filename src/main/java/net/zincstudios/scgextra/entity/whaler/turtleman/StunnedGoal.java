package net.zincstudios.scgextra.entity.whaler.turtleman;

import net.minecraft.world.entity.ai.goal.Goal;
import top.ribs.scguns.init.ModEffects;

import java.util.EnumSet;

public class StunnedGoal extends Goal {

    protected TurtlemanEntity mob;
    protected int stunTimer = 0;
    protected int stunDuration = 0;

    public StunnedGoal(TurtlemanEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void stun(int ticks) {
        this.stunDuration = ticks;
        this.stunTimer = ticks;
    }

    public boolean isStunned() {
        return this.stunTimer > 0;
    }

    @Override
    public boolean canUse() {
        return this.stunTimer > 0;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.triggerAnim("stunned", "start_stun");
    }

    @Override
    public void stop() {
        this.stunTimer = 0;
        this.mob.removeEffect(ModEffects.DEAFENED.get());
        this.mob.removeEffect(ModEffects.BLINDED.get());  // No panic
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.stunTimer--;
        this.mob.getNavigation().stop();

        if (this.stunTimer == stunDuration - 10) {
            this.mob.triggerAnim("stunned", "stunned");
        } else if (this.stunTimer == 10) {
            this.mob.triggerAnim("stunned", "end_stun");
        }
    }
}

