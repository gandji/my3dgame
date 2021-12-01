package org.gandji.my3dgame.goals;

import org.gandji.my3dgame.messages.IsTelegram;

public abstract class AbstractGoal implements IsGoal {

    Status status = Status.INACTIVE;

    @Override
    public void initialize() {
        status = Status.ACTIVE;
    }

    @Override
    public void terminate() {
        status = Status.INACTIVE;
    }

    @Override
    public void handleMessage(IsTelegram telegram) {

    }

    @Override
    public boolean isActive() {
        return status==Status.ACTIVE;
    }

    @Override
    public boolean isCompleted() {
        return status==Status.COMPLETED;
    }

    @Override
    public boolean isFailed() {
        return status==Status.FAILED;
    }
}
