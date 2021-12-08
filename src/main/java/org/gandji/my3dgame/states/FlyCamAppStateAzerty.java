package org.gandji.my3dgame.states;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.CameraInput;
import com.jme3.input.KeyInput;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.keyboard.Mapping;
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
public class FlyCamAppStateAzerty extends FlyCamAppState implements HasKeyMappings {

    Application app;
    AppStateManager stateManager;

    List<Mapping> mappings = new ArrayList<>();

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

            mappings.clear();

            mappings.add(new Mapping(CameraInput.FLYCAM_FORWARD,"Fly cam forward", KeyInput.KEY_Z,getCamera()).updateMapping(app.getInputManager()));
            mappings.add(new Mapping(CameraInput.FLYCAM_LOWER,"Fly cam lower", KeyInput.KEY_W,getCamera()).updateMapping(app.getInputManager()));
            mappings.add(new Mapping(CameraInput.FLYCAM_STRAFELEFT,"Fly cam left", KeyInput.KEY_Q,getCamera()).updateMapping(app.getInputManager()));
            mappings.add(new Mapping(CameraInput.FLYCAM_RISE,"Fly cam rise", KeyInput.KEY_A,getCamera()).updateMapping(app.getInputManager()));
            mappings.add(new Mapping(CameraInput.FLYCAM_UP,"Fly cam look up", KeyInput.KEY_DOWN,getCamera()).updateMapping(app.getInputManager()));
            mappings.add(new Mapping(CameraInput.FLYCAM_DOWN,"Fly cam look down", KeyInput.KEY_UP,getCamera()).updateMapping(app.getInputManager()));
        }

        getCamera().setMoveSpeed(10.f);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        log.debug("Cleaning up");
    }

    @Override
    public List<Mapping> getMappings() {
        return mappings;
    }
}
