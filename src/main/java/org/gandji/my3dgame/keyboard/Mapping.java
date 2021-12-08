package org.gandji.my3dgame.keyboard;

import com.jme3.input.InputManager;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import org.gandji.my3dgame.states.ActionDescriptor;

public class Mapping {

    String name;

    String description;

    Integer keyCode;

    InputListener listener;

    public Mapping(ActionDescriptor descriptor, String description, InputListener listener) {
        this.name = descriptor.name;
        this.description = description;
        this.keyCode = descriptor.defaultKeyCode;
        this.listener = listener;
    }

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

    public Mapping updateMapping(InputManager inputManager) {
        inputManager.deleteMapping(name);
        inputManager.addMapping(name, new KeyTrigger(keyCode));
        inputManager.addListener(listener,name);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void remove(InputManager inputManager) {
        inputManager.deleteMapping(name);
        inputManager.removeListener(listener);
    }
}
