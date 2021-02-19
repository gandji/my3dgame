package org.gandji.my3dgame.monks;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MonksGameState extends My3DGameBaseAppState implements ActionListener {
    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        super.onEnable();
        log.info("Loading Monk game");

    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Monks game");

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            log.debug("Wanna stop playing with the Monks?...OK");
            backToMenu();
        }
    }
}
