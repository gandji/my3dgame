package org.gandji.my3dgame.goals;

public interface IsCompositeGoal extends IsGoal {
    void addGoal(IsGoal goal);
}
