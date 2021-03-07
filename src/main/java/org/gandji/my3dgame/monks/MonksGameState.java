package org.gandji.my3dgame.monks;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.ModelKey;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.ferrari.FerrariGameState;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MonksGameState extends My3DGameBaseAppState implements ActionListener {

    @Override
    protected void initialize(Application app) {

        ModelKey keyArche = new ModelKey("Models/arche1.glb");
        Node arche = (Node) my3DGame.getAssetManager().loadModel(keyArche);

        my3DGame.getRootNode().attachChild(arche);

        ModelKey keyMonk = new ModelKey("Models/monk.glb");
        Node monk = (Node) my3DGame.getAssetManager().loadModel(keyMonk);

        my3DGame.getRootNode().attachChild(monk);


        // lights : put elsewhere?
        // duplicate code from Ferrari lights
        Light monkLight1 = new PointLight();
        //ferrariLight1.setColor(ColorRGBA.Yellow);
        ((PointLight) monkLight1).setPosition(new Vector3f(4,4,4));
        ((PointLight) monkLight1).setRadius(125.f);
        monkLight1.setName("Monk1");
        my3DGame.getRootNode().addLight(monkLight1);

        Light monkLight2 = new PointLight();
        //ferrariLight1.setColor(ColorRGBA.Yellow);
        ((PointLight) monkLight2).setPosition(new Vector3f(-4,4,-4));
        ((PointLight) monkLight2).setRadius(125.f);
        monkLight2.setName("Monk2");
        my3DGame.getRootNode().addLight(monkLight2);

        Light monkLight3 = new AmbientLight();
        monkLight3.setColor(ColorRGBA.White.mult(2.5f));
        monkLight3.setName("Monk3");
        my3DGame.getRootNode().addLight(monkLight3);


        Camera cam = my3DGame.getCamera();
        cam.setLocation(new Vector3f(0.022763014f, 0.58874106f, -3.8066878f));
        cam.setRotation(new Quaternion(-0.019826757f, 0.05451302f, 0.0010823654f, 0.99831563f));

        chaseCamera = new ChaseCamera(my3DGame.getCamera(), monk, my3DGame.getInputManager());
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(true);
        chaseCamera.setLookAtOffset(Vector3f.UNIT_Y.mult(3.f));

        cameraNode = new CameraNode("Camera Node", my3DGame.getCamera());
        //This mode means that camera copies the movements of the target:
        cameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        monk.attachChild(cameraNode);
        //Move camNode, e.g. behind and above the target:
        cameraNode.setLocalTranslation(new Vector3f(0, 4, +8));
        //Rotate the camNode to look at the target:
        cameraNode.lookAt(monk.getLocalTranslation().add(Vector3f.UNIT_Y.mult(3.f)), Vector3f.UNIT_Y);


    }

    private void setInputKeys() {
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE, new KeyTrigger(KeyInput.KEY_F2));
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE_FLY, new KeyTrigger(KeyInput.KEY_F3));
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE);
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE_FLY);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        super.onEnable();
        log.info("Loading Monk game");

        setInputKeys();


    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Monks game");

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name,isPressed,tpf);
        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            log.debug("Wanna stop playing with the Monks?...OK");
            backToMenu();
        }


    }
}
