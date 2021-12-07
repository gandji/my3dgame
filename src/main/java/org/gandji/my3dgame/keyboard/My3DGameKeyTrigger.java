package org.gandji.my3dgame.keyboard;

import com.jme3.input.controls.KeyTrigger;

public class My3DGameKeyTrigger extends KeyTrigger {

    String description;

    public My3DGameKeyTrigger(int keyCode, String description) {
        super(keyCode);
        this.description = description;
    }

    @Override
    public String getName() {
        return description;
    }
}
