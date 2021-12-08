package org.gandji.my3dgame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.CameraNode;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.My3DGame;
import org.gandji.my3dgame.ferrari.FerrariGameState;
import org.gandji.my3dgame.keyboard.Mapping;
import org.gandji.my3dgame.keyboard.KeyboardHelpState;
import org.gandji.my3dgame.menu.MenuAppState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class My3DGameBaseAppState extends BaseAppState implements ActionListener {

    @Autowired
    protected
    My3DGame my3DGame;

    @Autowired
    protected
    ApplicationContext applicationContext;

    @Autowired
    protected
    BulletAppState bulletAppState;

    @Autowired
    protected MenuAppState menuAppState;

    @Autowired
    protected KeyboardHelpState keyboardHelpState;

    public enum CameraType {
        CHASE,
        NODE,
        FLY;

        public CameraType next() {
            switch (this) {
                case CHASE:
                default:
                    return NODE;
                case NODE:
                    return CHASE;
                case FLY:
                    return FLY;
            }
        }
    }
    CameraType cameraChaseType = CameraType.CHASE;
    CameraType oldCameraChaseType = CameraType.FLY;
    protected ChaseCamera chaseCamera;
    protected CameraNode cameraNode;

    protected List<Mapping> mappings = new ArrayList<>();

    @Override
    protected void initialize(Application app) {
        // no need for this anymore, spring does it for us:
        // this.my3DGame = (My3DGame) app;
    }

    @Override
    protected void onEnable() {
        my3DGame.getStateManager().attach(bulletAppState);

        mappings.add(new Mapping(SimpleApplication.INPUT_MAPPING_EXIT, "Back to main menu", KeyInput.KEY_ESCAPE,
                (ActionListener) (name, isPressed, tpf) -> backToMenu())
                .updateMapping(my3DGame.getInputManager()));
        new Mapping("<F1>", "Display Help", KeyInput.KEY_F1,
                (ActionListener) (name, isPressed, tpf) -> {
                    if (!isPressed) {
                        if (my3DGame.getStateManager().hasState(keyboardHelpState)) {
                            log.debug("Base state detaching keyboard help");
                            my3DGame.getStateManager().detach(keyboardHelpState);
                        } else {
                            log.debug("Base state attaching keyboard help");
                            keyboardHelpState.clearMappingsHelp();
                            keyboardHelpState.buildMappingsHelp(mappings);
                            if (cameraChaseType==CameraType.FLY) {
                                FlyCamAppStateAzerty flyCamState = my3DGame.getStateManager().getState(FlyCamAppStateAzerty.class);
                                if (flyCamState!=null) {
                                    keyboardHelpState.buildMappingsHelp(flyCamState.getMappings());
                                }
                            }
                            my3DGame.getStateManager().attach(keyboardHelpState);
                        }
                    }
                })
                .updateMapping(my3DGame.getInputManager());

        updateCameraType();
    }

    @Override
    protected void onDisable() {

        for (Mapping mapping : mappings) {
            mapping.remove(my3DGame.getInputManager());
        }
        mappings.clear();

        my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().deleteMapping("<F1>");

        my3DGame.getRootNode().detachAllChildren();
        my3DGame.getStateManager().detach(bulletAppState);
        my3DGame.getStateManager().detach(keyboardHelpState);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // the camera controls
        if (isPressed && name.equals(ActionDescriptor.INPUT_CAMERA_TYPE.name)) {
            if (cameraChaseType != CameraType.FLY) {
                cameraChaseType = cameraChaseType.next();
            } else {
                cameraChaseType = oldCameraChaseType;
            }
            updateCameraType();
        }
        if (isPressed && name.equals(ActionDescriptor.INPUT_CAMERA_TYPE_FLY.name)) {
            if (cameraChaseType != CameraType.FLY) {
                oldCameraChaseType = cameraChaseType;
                cameraChaseType = cameraChaseType.FLY;
                updateCameraType();
            }
        }
    }

    protected void backToMenu() {
        my3DGame.getRootNode().detachAllChildren();
        my3DGame.getStateManager().detach(this);
        my3DGame.getStateManager().attach(menuAppState);
    }
    private void updateCameraType() {
        /* FIXME common part of cameras?? */
        if (cameraNode !=null && chaseCamera !=null) {
            if (cameraChaseType == FerrariGameState.CameraType.CHASE) {
                if (my3DGame.getFlyByCamera() != null) {
                    my3DGame.getFlyByCamera().setEnabled(false);
                }
                cameraNode.setEnabled(false);
                chaseCamera.setEnabled(true);
            } else if (cameraChaseType == FerrariGameState.CameraType.NODE) {
                if (my3DGame.getFlyByCamera() != null) {
                    my3DGame.getFlyByCamera().setEnabled(false);
                }
                cameraNode.setEnabled(true);
                chaseCamera.setEnabled(false);
            } else if (cameraChaseType == FerrariGameState.CameraType.FLY) {
                if (my3DGame.getFlyByCamera() != null) {
                    my3DGame.getFlyByCamera().setEnabled(true);
                    cameraNode.setEnabled(false);
                    chaseCamera.setEnabled(false);
                }
            }
        }
    }


}
