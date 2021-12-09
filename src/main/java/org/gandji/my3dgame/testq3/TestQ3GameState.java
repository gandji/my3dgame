package org.gandji.my3dgame.testq3;

import com.jme3.app.Application;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MaterialList;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.plugins.ogre.OgreMeshKey;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.ai.NavMeshState;
import org.gandji.my3dgame.ai.NavigationControl;
import org.gandji.my3dgame.keyboard.Mapping;
import org.gandji.my3dgame.objects.people.Zombie;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * TODO flicker when not searching path
 * TODO configure + force recomputation of navmesh?
 * TODO redesign level: nav mesh is discontinuous
 * TODO shoot at zombie
 * TODO use MotionPath.update() function (to be able to use spline paths)
 */
@Component
@Slf4j
public class TestQ3GameState extends My3DGameBaseAppState {

    private Node gameLevel;
    private BetterCharacterControl playerControl;
    private boolean left=false,right=false,up=false,down=false;

    float directionalBrightness = 2.0f;
    DirectionalLight directionalLight;
    float directionalBrightness2 = 2.0f;
    DirectionalLight directionalLight2;
    float ambientBrightness = 1.2f;
    AmbientLight ambientLight;

    ModelKey customModelKey;
    private Node playerNode;

    @Autowired
    private NavMeshState navMeshState;

    private NavigationControl navControl;

    @Override
    protected void initialize(Application app) {
        super.initialize(app);

        directionalLight = new DirectionalLight();
        directionalLight.setColor(ColorRGBA.White.clone().multLocal(directionalBrightness));
        directionalLight.setDirection(new Vector3f(-1, -1, -1).normalize());

        directionalLight2 = new DirectionalLight();
        directionalLight2.setColor(ColorRGBA.White.clone().multLocal(directionalBrightness2));
        directionalLight2.setDirection(new Vector3f(1, 1, 0.3f).normalize());

        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(ambientBrightness));

        my3DGame.getRootNode().addLight(directionalLight);

        my3DGame.getRootNode().addLight(directionalLight2);

        my3DGame.getRootNode().addLight(ambientLight);

        customModelKey = new ModelKey("Models/vaisseau1.glb");
        gameLevel = (Node) my3DGame.getAssetManager().loadAsset(customModelKey);
        gameLevel.setLocalScale(3f);

        my3DGame.getStateManager().attach(navMeshState);

        navControl = applicationContext.getBean(NavigationControl.class, navMeshState.getNavMesh());

    }

    @Override
    protected void onEnable() {

        super.onEnable();

        log.info("Loading test Q3");

        playerControl = new BetterCharacterControl( 1.f, 5.f, 50.f);
        playerControl.setGravity(new Vector3f(0f,-10.f,0f));

        playerNode = new Node("player");
        playerNode.addControl(playerControl);

        boolean useHttp = false;

        Resource quakeLevelResource = applicationContext.getResource("quake3level.zip");

        File file = null;
        try {
            file = quakeLevelResource.getFile();
        } catch (IOException e) {
            useHttp = true;
        }

        my3DGame.getFlyByCamera().setEnabled(true);
        my3DGame.getFlyByCamera().setMoveSpeed(100);

        my3DGame.getCamera().setFrustumFar(2000);

        my3DGame.getCamera().setAxes(new Vector3f(1.f,0.f,0.f),new Vector3f(0.f, 1.f, 0.f),new Vector3f(0.f,0.f,1.f));

        // spawn the zombie
        Zombie zombie = applicationContext.getBean(Zombie.class);
        my3DGame.getRootNode().attachChild(zombie.getSpatial());
        zombie.getSpatial().addControl(navControl);
        Vector3f zombieInitialPosition = new Vector3f(0.f,0.f,10.f);
        zombie.getControl().warp(zombieInitialPosition);

        Vector3f playerInitialPosition;

        // load the level from zip or http zip
        if (gameLevel==null) {
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
            playerInitialPosition = new Vector3f(60, 10, -60);
            playerControl.warp(playerInitialPosition);
        } else {
            my3DGame.getRootNode().attachChild(gameLevel);
            // outside
            //playerInitialPosition = new  Vector3f(0, 3, -40);
            // inside
            playerInitialPosition = new Vector3f(0.8f, 0.3f, 61.f);
        }
        log.debug("OK ... loaded");

        // add a physics control, it will generate a MeshCollisionShape based on the gameLevel
        gameLevel.addControl(new RigidBodyControl(0));


        my3DGame.getRootNode().attachChild(gameLevel);

        bulletAppState.getPhysicsSpace().addAll(gameLevel);

        bulletAppState.getPhysicsSpace().add(playerControl);
        bulletAppState.getPhysicsSpace().addAll(playerNode);
        my3DGame.getRootNode().attachChild(playerNode);

        playerControl.warp(playerInitialPosition);
        my3DGame.getCamera().lookAtDirection(zombieInitialPosition.subtract(playerInitialPosition),Vector3f.UNIT_Y);

        setInputKeys();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Test Q3");
        my3DGame.getFlyByCamera().setEnabled(false);
        my3DGame.getRootNode().getLocalLightList().clear();
        my3DGame.getRootNode().getWorldLightList().clear();
        clearInputKeys();
    }

    /**
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        super.update(tpf);
        float velocity = 10f;
        Vector3f camDir = my3DGame.getCamera().getDirection().clone();
        camDir.y = camDir.y -camDir.dot(Vector3f.UNIT_Y);
        camDir = camDir.normalize().multLocal(velocity);
        Vector3f camLeft = my3DGame.getCamera().getLeft().clone().multLocal(velocity);
        Vector3f newWalkDirection = new Vector3f();
        if (left)
            newWalkDirection.addLocal(camLeft);
        if (right)
            newWalkDirection.addLocal(camLeft.negate());
        if (up)
            newWalkDirection.addLocal(camDir);
        if (down)
            newWalkDirection.addLocal(camDir.negate());
        playerControl.setWalkDirection(newWalkDirection);
        my3DGame.getCamera().setLocation(new Vector3f(0.f,3.f,0.f).addLocal(playerNode.getWorldTranslation()));
    }

    public void setInputKeys() {

        // pfff add these only for help display,
        // do not pass to input manager, where do these come from??
        mappings.add(new Mapping("<Up>", "Look down", KeyInput.KEY_UP,this));
        mappings.add(new Mapping("<Down>", "Look up", KeyInput.KEY_DOWN,this));
        mappings.add(new Mapping("<Left>", "Turn left", KeyInput.KEY_LEFT,this));
        mappings.add(new Mapping("<Right>", "Turn right", KeyInput.KEY_RIGHT,this));


        mappings.add(new Mapping("<Q>", "Stride left", KeyInput.KEY_Q,
                (ActionListener) (name, isPressed, tpf) -> left = isPressed)
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<D>", "Stride right", KeyInput.KEY_D,
                (ActionListener) (name, isPressed, tpf) -> right = isPressed)
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<Z>", "Walk", KeyInput.KEY_Z,
                (ActionListener) (name, isPressed, tpf) -> up = isPressed)
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<S>", "Back up", KeyInput.KEY_S,
                (ActionListener) (name, isPressed, tpf) -> down = isPressed)
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<,>", "Fire", KeyInput.KEY_COMMA,
                (ActionListener) (name, isPressed, tpf) -> {
                }).addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<SPACE>", "Jump", KeyInput.KEY_SPACE,
                (ActionListener) (name, isPressed, tpf) -> playerControl.jump())
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<RETURN", "Reset", KeyInput.KEY_RETURN,
                (ActionListener) (name, isPressed, tpf) -> {})
                .addToInputManager(my3DGame.getInputManager()));
        mappings.add(new Mapping("<P>", "Call Sinbad", KeyInput.KEY_P,
                (ActionListener) (name, isPressed, tpf) ->
                        navControl.setTarget(playerNode.getWorldTranslation()))
                .addToInputManager(my3DGame.getInputManager()));

        mappings.add(new Mapping("<N>", "Toggle nav mesh", KeyInput.KEY_N,
                (ActionListener) (name, isPressed, tpf) ->
                {if (isPressed) {navMeshState.toggleMesh();}})
                .addToInputManager(my3DGame.getInputManager()));

        mappings.add(new Mapping("<B>", "Toggle Sinbad's path", KeyInput.KEY_B,
                (ActionListener) (name, isPressed, tpf) ->
                {if (isPressed) {navControl.toggleDisplayMotionPath();}})
                .addToInputManager(my3DGame.getInputManager()));

    }

    private void clearInputKeys() {
        for (Mapping mapping : mappings) {
            my3DGame.getInputManager().deleteMapping(mapping.getName());
        }
        mappings.clear();
    }

    @Override
    protected void cleanup(Application app) {

        getStateManager().detach(navMeshState);

    }

    private String updateMapping(String input, int key) {
        getApplication().getInputManager().deleteMapping(input);
        getApplication().getInputManager().addMapping(input, new KeyTrigger(key));
        return input;
    }

}
