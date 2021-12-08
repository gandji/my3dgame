package org.gandji.my3dgame.states;

import com.jme3.input.KeyInput;

public enum ActionDescriptor {

    INPUT_CAMERA_TYPE("Camera_Type","<F2>", KeyInput.KEY_F2),
    INPUT_CAMERA_TYPE_FLY("Camera_Type_Fly","<F3>", KeyInput.KEY_F3);

    public final String name;
    public final String defaultKeyDescription;
    public final Integer defaultKeyCode;

    ActionDescriptor(String name, String defaultKeyDescription, Integer defaultKeyCode) {
        this.name = name;
        this.defaultKeyDescription = defaultKeyDescription;
        this.defaultKeyCode = defaultKeyCode;
    }
}
