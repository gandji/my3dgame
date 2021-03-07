package org.gandji.my3dgame.states;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.CameraInput;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 17/01/2020.
 *
 * I overwrite some commands to convert to AZERTY
 * and make it a spring managed bean
 */
@Component
@Slf4j
public class FlyCamAppStateAzerty extends FlyCamAppState {

    Application app;
    AppStateManager stateManager;

    public FlyCamAppStateAzerty() {
        super();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = app;
        this.stateManager = stateManager;

        // initialize everything here. when is setEnabled called for AbstractAppState's??
        log.debug(String.format("Initializing"));
        if (null != app.getInputManager()) {
            log.debug(String.format("update mappings"));

            List<String> updatedMappings = new ArrayList<>();

            updatedMappings.add(updateMapping(CameraInput.FLYCAM_FORWARD, KeyInput.KEY_Z));
            updatedMappings.add(updateMapping(CameraInput.FLYCAM_LOWER, KeyInput.KEY_W));
            updatedMappings.add(updateMapping(CameraInput.FLYCAM_STRAFELEFT, KeyInput.KEY_Q));
            updatedMappings.add(updateMapping(CameraInput.FLYCAM_RISE, KeyInput.KEY_A));

            updatedMappings.add(updateMapping(CameraInput.FLYCAM_UP,KeyInput.KEY_DOWN));
            updatedMappings.add(updateMapping(CameraInput.FLYCAM_DOWN,KeyInput.KEY_UP));

            int nMappings = updatedMappings.size();
            // the listener is the camera
            app.getInputManager().addListener(getCamera(), (String[]) updatedMappings.toArray(new String[nMappings]));
        }

        getCamera().setMoveSpeed(10.f);
    }

    private String updateMapping(String input, int key) {
        app.getInputManager().deleteMapping(input);
        app.getInputManager().addMapping(input, new KeyTrigger(key));
        return input;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        log.debug("Cleaning up");
    }
}
