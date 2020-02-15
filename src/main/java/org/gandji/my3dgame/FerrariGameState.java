package org.gandji.my3dgame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.util.SkyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 19/01/2020.
 */
@Component
@Slf4j
public class FerrariGameState extends BaseAppState implements ActionListener {


    public enum CameraType {
        CHASE,
        NODE,
        FLY;

        public CameraType next() {
            switch (this) {
                case CHASE:
                    return NODE;
                case NODE:
                    return CHASE;
                case FLY:
                    return FLY;
                default:
                    return NODE;
            }
        }
    }
    static String INPUT_CAMERA_TYPE = "Camera_Type";
    static String INPUT_CAMERA_TYPE_FLY = "Camera_Type_Fly";

    @Autowired
    My3DGame my3DGame;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    BulletAppState bulletAppState;

    @Autowired
    My3DGameTerrain my3DGameTerrain;

    Ferrari ferrari;
    CameraType cameraChaseType = CameraType.FLY;
    CameraType oldCameraChaseType = CameraType.CHASE;
    ChaseCamera chaseCamera;
    CameraNode cameraNode;

    @Autowired
    MenuAppState menuAppState;

    @Override
    protected void initialize(Application app) {
        // no need for this anymore, spring does it for us:
        // this.my3DGame = (My3DGame) app;
    }

    @Override
    protected void cleanup(Application app) {

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

        log.info("Loading Ferrari game");
        PhysicsHelper.createPhysicsTestWorld(my3DGame.getRootNode(), my3DGame.getAssetManager(), bulletAppState.getPhysicsSpace());

        // the sky
        my3DGame.getRootNode().attachChild(SkyFactory.createSky(my3DGame.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        my3DGame.getRootNode().attachChild(my3DGameTerrain.getTerrain());
        bulletAppState.getPhysicsSpace().add(my3DGameTerrain.getTerrain());

        ferrari = applicationContext.getBean(Ferrari.class);
        ferrari.setupKeys(my3DGame.getInputManager());

        my3DGame.getRootNode().attachChild(ferrari.getNode());
        // scene:
        ferrari.setInitialPosition(new Vector3f(-55.94f, 72.72f, 51.f));
        // generated heightmap ferrari.setInitialPosition(new Vector3f(244.5f, 16.212f, 5));
        // test playground ferrari.setInitialPosition(Vector3f.ZERO);
        ferrari.resetPosition();

        Ferrari ferrari2 =  applicationContext.getBean(Ferrari.class);
        my3DGame.getRootNode().attachChild(ferrari2.getNode());
        ferrari2.setInitialPosition(new Vector3f(254.f, 16.f, 5.f));
        //ferrari2.setInitialPosition(new Vector3f(10.f, 0.f, 0.f));
        ferrari2.resetPosition();

        Camera cam = my3DGame.getCamera();
        cam.setLocation(new Vector3f(239.03987f, 25.607182f, 22.808495f));
        cam.setRotation(new Quaternion(0.006459943f, 0.98668134f, -0.15741317f, 0.040488426f));

        cam.setLocation(new Vector3f(43.514324f, 3.0221524f, -6.3544216f));
        cam.setRotation(new Quaternion(0.051979125f, -0.76097894f, 0.061444007f, 0.64376533f));

        chaseCamera = new ChaseCamera(my3DGame.getCamera(), ferrari.getSpatial(), my3DGame.getInputManager());
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(true);
        chaseCamera.setLookAtOffset(Vector3f.UNIT_Y.mult(3.f));

        cameraNode = new CameraNode("Camera Node", my3DGame.getCamera());
        //This mode means that camera copies the movements of the target:
        cameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        ferrari.getNode().attachChild(cameraNode);
        //Move camNode, e.g. behind and above the target:
        cameraNode.setLocalTranslation(new Vector3f(0, 4, +8));
        //Rotate the camNode to look at the target:
        cameraNode.lookAt(ferrari.getNode().getLocalTranslation().add(Vector3f.UNIT_Y.mult(3.f)), Vector3f.UNIT_Y);

        // chase camera
        updateCameraType();
        setInputKeys();

    }

    private void updateCameraType() {
        if (cameraChaseType==CameraType.CHASE) {
            if (my3DGame.getFlyByCamera()!=null) {
                my3DGame.getFlyByCamera().setEnabled(false);
            }
            cameraNode.setEnabled(false);
            chaseCamera.setEnabled(true);
        } else if (cameraChaseType==CameraType.NODE) {
            if (my3DGame.getFlyByCamera()!=null) {
                my3DGame.getFlyByCamera().setEnabled(false);
            }
            cameraNode.setEnabled(true);
            chaseCamera.setEnabled(false);
        } else if (cameraChaseType==CameraType.FLY) {
            if (my3DGame.getFlyByCamera()!=null) {
                my3DGame.getFlyByCamera().setEnabled(true);
                cameraNode.setEnabled(false);
                chaseCamera.setEnabled(false);
            }
        }
    }

    @Override
    protected void onDisable() {
        log.info("Exiting Ferrari game state");
        if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
            my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            log.info("Wanna stop playing with the Ferraris?");
            my3DGame.getRootNode().detachAllChildren();
            my3DGame.getStateManager().detach(this);
            my3DGame.getStateManager().attach(menuAppState);
        }
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

    private void setInputKeys() {
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE, new KeyTrigger(KeyInput.KEY_F2));
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE_FLY, new KeyTrigger(KeyInput.KEY_F3));
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE);
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE_FLY);
    }
}
