package org.gandji.my3dgame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 25/01/2020.
 */
@Component
@Log4j
public class AppCloseListener implements ActionListener {

    @Autowired
    My3DGame my3DGame;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            return;
        }

        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            my3DGame.stop();
        }
    }
}
