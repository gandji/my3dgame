package org.gandji.my3dgame.ferrari;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;
import com.jme3.util.SkyFactory;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.*;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by gandji on 19/01/2020.
 */
@Component
@Slf4j
public class FerrariGameState extends My3DGameBaseAppState {

    @Autowired
    My3DGameTerrain my3DGameTerrain;

    @Autowired
    Ferrari ferrari;

    Ferrari ferrari2;

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        log.info("Initializing Ferrari game");
        // this is the test world with a floor and some boxes
        //PhysicsHelper.createPhysicsTestWorld(my3DGame.getRootNode(), my3DGame.getAssetManager(), bulletAppState.getPhysicsSpace());
        my3DGameTerrain.loadAssets();

        ferrari.loadAssets();

        // the second ferrari is not autowired!
        ferrari2 =  applicationContext.getBean(Ferrari.class);
        ferrari2.loadAssets();

        chaseCamera = new ChaseCamera(my3DGame.getCamera(), ferrari.getSpatial(), my3DGame.getInputManager());
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(true);
        chaseCamera.setLookAtOffset(Vector3f.UNIT_Y.mult(3.f));
    }

    @Override
    protected void onEnable() {

        super.onEnable();

        log.info("Loading Ferrari game");
        // the sky
        my3DGame.getRootNode().attachChild(SkyFactory.createSky(my3DGame.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        my3DGame.getRootNode().attachChild(my3DGameTerrain.getTerrain());
        bulletAppState.getPhysicsSpace().add(my3DGameTerrain.getTerrain());

        ferrari.setupKeys(my3DGame.getInputManager());
        ferrari.enterState();
        ferrari.setInitialPosition(my3DGameTerrain.getInitialPosition());
        // scene:ferrari.setInitialPosition(new Vector3f(-55.94f, 72.72f, 51.f));
        // generated heightmap ferrari.setInitialPosition(new Vector3f(244.5f, 16.212f, 5));
        // test playground ferrari.setInitialPosition(Vector3f.ZERO);
        ferrari.resetPosition();

        ferrari2.enterState();
        ferrari2.setInitialPosition(ferrari.initialPosition.add(10.f, 0.f, 0.f));
        ferrari2.resetPosition();

        my3DGame.getRootNode().attachChild(ferrari.getNode());

        my3DGame.getRootNode().attachChild(ferrari2.getNode());

        cameraNode = new CameraNode("Camera Node", my3DGame.getCamera());
        //This mode means that camera copies the movements of the target:
        cameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        ferrari.getNode().attachChild(cameraNode);
        //Move camNode, e.g. behind and above the target:
        cameraNode.setLocalTranslation(new Vector3f(0, 4, +8));
        //Rotate the camNode to look at the target:
        cameraNode.lookAt(ferrari.getNode().getLocalTranslation().add(Vector3f.UNIT_Y.mult(3.f)), Vector3f.UNIT_Y);

        setInputKeys();

        my3DGame.getFlyByCamera().setEnabled(true);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Ferrari game");
        removeInputKeys();
        my3DGame.getFlyByCamera().setEnabled(false);
        ferrari.disableKeys(my3DGame.getInputManager());
        ferrari.exitState();
        ferrari2.exitState();
    }

    private void setInputKeys() {
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE, new KeyTrigger(KeyInput.KEY_F2));
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE_FLY, new KeyTrigger(KeyInput.KEY_F3));
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE);
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE_FLY);
    }
    private void removeInputKeys() {
        my3DGame.getInputManager().removeListener(this);
    }
}
