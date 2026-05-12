package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

// NOTE: Windup and Recovery is mostly used for matching timings with animations.
public abstract class AbilityGoal<T extends Mob> extends Goal {

    public static final GoalState IDLE_STATE = new GoalState("ability_idle_state");
    public static final GoalState WINDUP_STATE = new GoalState("ability_windup_state");
    public static final GoalState ACTIVE_STATE = new GoalState("ability_active_state");
    public static final GoalState RECOVERY_STATE = new GoalState("ability_recovery_state");
    public static final GoalState COOLDOWN_STATE = new GoalState("ability_cooldown_state");

    protected final T mob;
    protected int cooldownDuration = 200;
    protected int windupDuration = 0;
    protected int recoveryDuration = 0;
    protected int cooldownEnd = 0;  // tickCount timestamp
    protected int windupTimer = 0;
    protected int recoveryTimer = 0;
    protected GoalState state = IDLE_STATE;
    protected boolean activated = false;

    public AbilityGoal(T mob) {
        this.mob = mob;
    }

    public GoalState getState() {
        return this.state;
    }

    protected void setGoalState(GoalState state) {
        GoalState oldState = this.state;
        this.state = state;
        if (oldState != state && this.mob instanceof GoalStateHandler goalStateHandler) {
            goalStateHandler.onGoalStateChanged(this, state);
        }
    }

    protected void resetCooldown() {
        this.cooldownEnd = this.mob.tickCount + this.cooldownDuration;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            if (this.mob.tickCount > this.cooldownEnd) {
                if (this.state != IDLE_STATE) {
                    this.setGoalState(IDLE_STATE);
                }
            }
            return false;
        }

        return this.mob.tickCount > this.cooldownEnd && this.mob.isAlive();
    }

    @Override
    public void start() {
        this.windupTimer = this.windupDuration;
        this.recoveryTimer = 0;
        this.activated = false;
        this.setGoalState(WINDUP_STATE);
    }

    @Override
    public void tick() {
        if (this.windupTimer > 0) {
            this.windupTimer--;
        } else if (!this.activated) {
            this.setGoalState(ACTIVE_STATE);
            if (!this.activate()) {
                this.activated = true;
                this.recoveryTimer = this.recoveryDuration;
            }
        } else if (this.recoveryTimer > 0) {
            this.setGoalState(RECOVERY_STATE);
            this.recoveryTimer--;
        } else {
            this.setGoalState(COOLDOWN_STATE);
            this.resetCooldown();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * @return if it should continue to keep active;
     */
    public abstract boolean activate();


    public AbilityGoal<T> cooldown(int cooldownTicks) {
        this.cooldownDuration = cooldownTicks;
        return this;
    }

    public AbilityGoal<T> windup(int windupTicks) {
        this.windupDuration = windupTicks;
        return this;
    }

    public AbilityGoal<T> recovery(int recoveryTicks) {
        this.recoveryDuration = recoveryTicks;
        return this;
    }
}
