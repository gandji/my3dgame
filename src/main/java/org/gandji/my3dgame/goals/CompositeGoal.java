package org.gandji.my3dgame.goals;

import java.util.Deque;
import java.util.LinkedList;

/**
 * LIFO behavior
 */
public class CompositeGoal extends AbstractGoal implements IsCompositeGoal {

    Deque<IsGoal> goals = new LinkedList<>();

    IsGoal current = null;

    @Override
    public void addGoal(IsGoal goal) {
        goals.push(goal);
    }

    @Override
    public void initialize() {
        super.initialize();
        advance();
    }

    @Override
    public Status process() {
        if (current==null) {
            return Status.FAILED;
        }

        if (status==Status.FAILED || status==Status.COMPLETED || status==Status.INACTIVE) {
            return status;
        }

        status = current.process();

        if (status==Status.FAILED) {
            current.terminate();
            goals.clear();
            return status;
        }
        else if (status==Status.COMPLETED) {
            current.terminate();
            advance();
        }


        return status;
    }

    private boolean advance() {
        if (goals.isEmpty()) {
            status = Status.COMPLETED;
            return false;
        }
        current = goals.pop();
        status = Status.ACTIVE;
        return true;
    }

    @Override
    public void terminate() {
        super.terminate();
        goals.clear();
        status = Status.INACTIVE;
    }

}
