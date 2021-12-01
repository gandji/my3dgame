package org.gandji.my3dgame.people;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;

public interface Person extends Drawable {
    Role getRole();
    String getName();
    Health getHealth();

    BetterCharacterControl getControl();

    void setPosition(Vector3f worldPosition);

    Vector3f getPosition();
}
