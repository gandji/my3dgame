package org.gandji.my3dgame.testq3;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MaterialList;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.plugins.ogre.OgreMeshKey;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class TestQ3GameState extends My3DGameBaseAppState {

    private Node gameLevel;
    private PhysicsCharacter player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false,right=false,up=false,down=false;

    float directionalBrightness = 2.0f;
    DirectionalLight directionalLight;
    float ambientBrightness = 2.0f;
    AmbientLight ambientLight;

    @Override
    protected void initialize(Application app) {
        super.initialize(app);

        directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White.clone().multLocal(directionalBrightness));
        directionalLight.setDirection(new Vector3f(-1, -1, -1).normalize());

        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(ambientBrightness));
    }

    @Override
    protected void onEnable() {

        super.onEnable();

        log.info("Loading test Q3");

        boolean useHttp = false;

        Resource quakeLevelResource = applicationContext.getResource("quake3level.zip");

        File file = null;
        try {
            file = quakeLevelResource.getFile();
        } catch (IOException e) {
            useHttp = true;
        }

        my3DGame.getFlyByCamera().setMoveSpeed(100);

        my3DGame.getCamera().setFrustumFar(2000);

        my3DGame.getCamera().setAxes(new Vector3f(1.f,0.f,0.f),new Vector3f(0.f, 1.f, 0.f),new Vector3f(0.f,0.f,1.f));

        my3DGame.getRootNode().addLight(directionalLight);

        my3DGame.getRootNode().addLight(ambientLight);

        // load the level from zip or http zip
        if (useHttp) {
            log.debug("Loading Quake level from net");
            my3DGame.getAssetManager().registerLocator(
                    "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/quake3level.zip",
                    HttpZipLocator.class);
        } else {
            log.debug("Loading Quake level from local resources");
            my3DGame.getAssetManager().registerLocator("quake3level.zip", ZipLocator.class);
        }

        // create the geometry and attach it
        log.debug("Loading the scene, please wait...");
        MaterialList matList = (MaterialList) my3DGame.getAssetManager().loadAsset("Scene.material");
        log.debug("Loading the mesh, please wait....");
        com.jme3.scene.plugins.ogre.OgreMeshKey key = new OgreMeshKey("main.meshxml", matList);
        gameLevel = (Node) my3DGame.getAssetManager().loadAsset(key);
        gameLevel.setLocalScale(0.1f);
        log.debug("OK ... loaded");

        // add a physics control, it will generate a MeshCollisionShape based on the gameLevel
        gameLevel.addControl(new RigidBodyControl(0));

        player = new PhysicsCharacter(new SphereCollisionShape(5), .1f);
        player.setJumpSpeed(70);
        player.setFallSpeed(55);
        player.setGravity(new Vector3f(0f,-1f,0f));

        player.setPhysicsLocation(new Vector3f(60, 10, -60));

        my3DGame.getRootNode().attachChild(gameLevel);

        bulletAppState.getPhysicsSpace().addAll(gameLevel);
        bulletAppState.getPhysicsSpace().add(player);

        setInputKeys();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Test Q3");
        my3DGame.getRootNode().getLocalLightList().clear();
        my3DGame.getRootNode().getWorldLightList().clear();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        Vector3f camDir = my3DGame.getCamera().getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = my3DGame.getCamera().getLeft().clone().multLocal(0.4f);
        walkDirection.set(0,0,0);
        if(left)
            walkDirection.addLocal(camLeft);
        if(right)
            walkDirection.addLocal(camLeft.negate());
        if(up)
            walkDirection.addLocal(camDir);
        if(down)
            walkDirection.addLocal(camDir.negate());
        player.setWalkDirection(walkDirection);
        my3DGame.getCamera().setLocation(player.getPhysicsLocation());
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        super.onAction(name,isPressed,tpf);
        if (name.equals(SimpleApplication.INPUT_MAPPING_EXIT)) {
            log.debug("Wanna stop playing with test Q3?...OK");
            backToMenu();
        }
        if (name.equals("Lefts")) {
            if(isPressed)
                left=true;
            else
                left=false;
        } else if (name.equals("Rights")) {
            if(isPressed)
                right=true;
            else
                right=false;
        } else if (name.equals("Ups")) {
            if(isPressed)
                up=true;
            else
                up=false;
        } else if (name.equals("Downs")) {
            if(isPressed)
                down=true;
            else
                down=false;
        } else if (name.equals("Space")) {
            player.jump(Vector3f.UNIT_Y);
        }
    }

    public void setInputKeys() {
        my3DGame.getInputManager().addMapping("Lefts", new KeyTrigger(KeyInput.KEY_Q));
        my3DGame.getInputManager().addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        my3DGame.getInputManager().addMapping("Ups", new KeyTrigger(KeyInput.KEY_Z));
        my3DGame.getInputManager().addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        my3DGame.getInputManager().addMapping("Backs", new KeyTrigger(KeyInput.KEY_COMMA));
        my3DGame.getInputManager().addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        my3DGame.getInputManager().addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        my3DGame.getInputManager().addListener(this, "Lefts");
        my3DGame.getInputManager().addListener(this, "Rights");
        my3DGame.getInputManager().addListener(this, "Ups");
        my3DGame.getInputManager().addListener(this, "Downs");
        my3DGame.getInputManager().addListener(this, "Backs");
        my3DGame.getInputManager().addListener(this, "Space");
        my3DGame.getInputManager().addListener(this, "Reset");

        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE, new KeyTrigger(KeyInput.KEY_F2));
        my3DGame.getInputManager().addMapping(INPUT_CAMERA_TYPE_FLY, new KeyTrigger(KeyInput.KEY_F3));
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE);
        my3DGame.getInputManager().addListener(this, INPUT_CAMERA_TYPE_FLY);

    }
    @Override
    protected void cleanup(Application app) {

    }

    private String updateMapping(String input, int key) {
        getApplication().getInputManager().deleteMapping(input);
        getApplication().getInputManager().addMapping(input, new KeyTrigger(key));
        return input;
    }

}
