package org.gandji.my3dgame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.CameraNode;
import org.gandji.my3dgame.My3DGame;
import org.gandji.my3dgame.ferrari.FerrariGameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
    protected static String INPUT_CAMERA_TYPE = "Camera_Type";
    protected static String INPUT_CAMERA_TYPE_FLY = "Camera_Type_Fly";
    CameraType cameraChaseType = CameraType.CHASE;
    CameraType oldCameraChaseType = CameraType.FLY;
    protected ChaseCamera chaseCamera;
    protected CameraNode cameraNode;


    @Override
    protected void initialize(Application app) {
        // no need for this anymore, spring does it for us:
        // this.my3DGame = (My3DGame) app;
    }

    @Override
    protected void onEnable() {
        my3DGame.getStateManager().attach(bulletAppState);

        if (my3DGame.getInputManager() != null) {
            if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
                my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

            my3DGame.getInputManager().addMapping(SimpleApplication.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            my3DGame.getInputManager().addListener(this,SimpleApplication.INPUT_MAPPING_EXIT);
        }



        updateCameraType();
    }

    @Override
    protected void onDisable() {
        if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
            my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().removeListener(this);

        my3DGame.getRootNode().detachAllChildren();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        // the camera controls
        if (isPressed && name.equals(INPUT_CAMERA_TYPE)) {
            if (cameraChaseType != CameraType.FLY) {
                cameraChaseType = cameraChaseType.next();
            } else {
                cameraChaseType = oldCameraChaseType;
            }
            updateCameraType();
        }
        if (isPressed && name.equals(INPUT_CAMERA_TYPE_FLY)) {
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
