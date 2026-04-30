package net.zincstudios.scgextra.entity.asgharian;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

// Mostly a string wrapper
// NOTE: for use in static context only
@SuppressWarnings("FieldCanBeLocal")
public record GoalState(String id) {

    private static final Map<String, GoalState> GOAL_STATES = new HashMap<>();

    public static @Nullable GoalState get(String identifier) {
        return GOAL_STATES.get(identifier);
    }

    public GoalState {
        if (GOAL_STATES.containsKey(id)) {
            throw new IllegalArgumentException("identifier already exists");
        }
        GOAL_STATES.put(id, this);
    }
}
