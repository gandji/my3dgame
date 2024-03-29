package org.gandji.my3dgame.ai;

import com.jme3.math.Vector3f;

/**
 * Sets the target for the NavigationControl.
 *
 * @author mitm
 */
public interface Pickable {
    void setTarget(Vector3f target);
}