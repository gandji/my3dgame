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

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

        super.onEnable();

        log.info("Loading Ferrari game");
        // thjis is the test world with a floor and some boxes
        //PhysicsHelper.createPhysicsTestWorld(my3DGame.getRootNode(), my3DGame.getAssetManager(), bulletAppState.getPhysicsSpace());

        my3DGameTerrain.loadAssets();

        // the sky
        my3DGame.getRootNode().attachChild(SkyFactory.createSky(my3DGame.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));

        my3DGame.getRootNode().attachChild(my3DGameTerrain.getTerrain());
        bulletAppState.getPhysicsSpace().add(my3DGameTerrain.getTerrain());

        ferrari.loadAssets();
        ferrari.setupKeys(my3DGame.getInputManager());

        my3DGame.getRootNode().attachChild(ferrari.getNode());
        ferrari.setInitialPosition(my3DGameTerrain.getInitialPosition());
        // scene:ferrari.setInitialPosition(new Vector3f(-55.94f, 72.72f, 51.f));
        // generated heightmap ferrari.setInitialPosition(new Vector3f(244.5f, 16.212f, 5));
        // test playground ferrari.setInitialPosition(Vector3f.ZERO);
        ferrari.resetPosition();

        // the second ferrari is not autowired!
        Ferrari ferrari2 =  applicationContext.getBean(Ferrari.class);
        ferrari2.loadAssets();
        my3DGame.getRootNode().attachChild(ferrari2.getNode());
        ferrari2.setInitialPosition(ferrari.initialPosition.add(10.f, 0.f, 0.f));
        ferrari2.resetPosition();

        Camera cam = my3DGame.getCamera();
        cam.setLocation(new Vector3f(239.03987f, 25.607182f, 22.808495f));
        cam.setRotation(new Quaternion(0.006459943f, 0.98668134f, -0.15741317f, 0.040488426f));

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

        setInputKeys();

    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Ferrari game");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name,isPressed,tpf);
        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            log.debug("Wanna stop playing with the Ferraris?...OK");
            backToMenu();
        }
    }

    private void setInputKeys() {
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE, new KeyTrigger(KeyInput.KEY_F2));
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE_FLY, new KeyTrigger(KeyInput.KEY_F3));
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE);
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE_FLY);
    }
}
