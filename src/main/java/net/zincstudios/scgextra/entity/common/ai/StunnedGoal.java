package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.common.Stunnable;

import java.util.EnumSet;

// NOTE: weakness exposed state == stunned goal
public class StunnedGoal<T extends PathfinderMob & Stunnable> extends Goal {

    protected final T mob;
    protected final int cooldownLength;
    private int stunTimer = 0;
    private long cooldownEnd;  // level timestamp
    private int stunLength = 0;

    public StunnedGoal(T mob, int cooldownLength) {
        this.mob = mob;
        this.cooldownLength = cooldownLength;
        this.cooldownEnd = mob.level().getGameTime() + cooldownLength;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public StunnedGoal(T mob) {
        this(mob, CommonConfig.abilityWeaknessCooldown);
    }

    @Override
    public boolean canUse() {
        if (this.stunLength > 0) {
            return this.stunTimer > 0;
        } else {
            this.stunLength = this.mob.shouldStun();
            return this.stunLength > 0 && this.mob.level().getGameTime() > this.cooldownEnd;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setStunned(false);
        this.stunTimer = this.stunLength;
    }

    @Override
    public void stop() {
        this.mob.setStunned(false);
        this.stunLength = 0;
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownLength;
    }

    @Override
    public void tick() {
        this.stunTimer--;
        if (this.mob.tickStunned(this.stunTimer)) {
            this.stunTimer = 0;
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.stunTimer > 0;
    }

    public int getStunTimer() {
        return stunTimer;
    }
}
