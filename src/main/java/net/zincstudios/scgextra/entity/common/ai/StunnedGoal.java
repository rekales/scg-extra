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
    private boolean stunned = false;

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
        if (stunned) {
            return this.stunTimer > 0;
        } else {
            return this.mob.shouldStun() > 0 && this.mob.level().getGameTime() > this.cooldownEnd;
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
        this.stunned = true;
    }

    @Override
    public void stop() {
        this.mob.setStunned(false);
        this.stunned = false;
        this.cooldownEnd = this.mob.level().getGameTime() + this.cooldownLength;
    }

    @Override
    public void tick() {
        this.stunTimer--;
        if (this.mob.updateStunned(this.stunTimer)) {
            this.stunTimer = 0;
        }
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.stunTimer > 0;
    }
}
