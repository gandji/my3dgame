package org.gandji.my3dgame.goals;

import org.gandji.my3dgame.messages.IsTelegram;

public interface IsGoal {

    enum Status {
        INACTIVE,
        ACTIVE,
        COMPLETED,
        FAILED;
    }

    void initialize();
    Status process();
    void terminate();

    boolean isActive();
    boolean isCompleted();
    boolean isFailed();

    void handleMessage(IsTelegram telegram);
}
