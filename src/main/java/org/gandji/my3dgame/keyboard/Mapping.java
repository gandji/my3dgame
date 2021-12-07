package org.gandji.my3dgame.keyboard;

import com.jme3.input.InputManager;
import com.jme3.input.controls.InputListener;

public class Mapping {

    String name;

    String description;

    Integer keyCode;

    InputListener listener;

    public Mapping(String name, String description, Integer keyCode, InputListener listener) {
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.listener = listener;
    }

    public Mapping addToInputManager(InputManager inputManager) {
        inputManager.addMapping(name, new My3DGameKeyTrigger(keyCode,description));
        inputManager.addListener(listener,name);
        return this;
    }

    public String getName() {
        return name;
    }
}
