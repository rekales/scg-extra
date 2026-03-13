package net.zincstudios.scgextra.entity.rrc.drone;

import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal;
import top.ribs.scguns.init.ModEffects;

public class StunnedGoal extends Goal{

    private final DroneEntity mob;
    protected int tick = 0;
    public StunnedGoal(DroneEntity en){
        this.mob = en;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.isStunned();
    }

    @Override
    public void start() {
        this.tick = 0;
        this.mob.getNavigation().stop();
        this.mob.triggerAnim("behaviour", "stun");
    }

    @Override
    public void stop() {
        this.tick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        this.mob.triggerAnim("behaviour", "stun");
        if(this.tick > 60){
            this.mob.removeEffect(ModEffects.DEAFENED.get());
            this.mob.removeEffect(ModEffects.BLINDED.get());
        }
        this.tick++;
    }
    @Override
    public boolean canContinueToUse() {
        return this.mob.isStunned();
    }
}
