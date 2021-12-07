package org.gandji.my3dgame.objects.people;

import com.jme3.bullet.control.BetterCharacterControl;

/**
 * This class just to insert the setAction interface....
 * which is used only to set/unset "forward" boolean
 * TODO implement cleaner "separate" action listener
 */
public abstract class AbstractMy3DGameCharacterController extends BetterCharacterControl {

    public AbstractMy3DGameCharacterController(float radius, float height, float mass) {
        super(radius, height, mass);
    }

    public abstract void setAction(String name, boolean isPressed, float tpf);
}
