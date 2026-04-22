package net.zincstudios.scgextra.entity.asgharian;

import net.minecraft.world.entity.ai.goal.Goal;

public interface GoalStateHandler {
    void onGoalStateChanged(Goal goal, String state);
}
