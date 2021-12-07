package org.gandji.my3dgame.hellocollision;

import com.jme3.app.Application;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.keyboard.Mapping;
import org.gandji.my3dgame.states.My3DGameBaseAppState;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * This uses the old CharacterControl
 *
 * From a tuto for collision shapes
 */
@Component
@Slf4j
public class HelloCollisionAppState extends My3DGameBaseAppState {

    private Spatial sceneModel;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    //Temporary vectors used on each frame.
    //They here to avoid instantiating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    private List<Mapping> mappings = null;

    @PostConstruct
    public void buildKeyMappings() {
        mappings = new ArrayList<>();
        mappings.add(new Mapping("Lefts", "Stride left", KeyInput.KEY_Q,
                (ActionListener) (name, isPressed, tpf) -> left = isPressed));
        mappings.add(new Mapping("Rights", "Stride right", KeyInput.KEY_D,
                (ActionListener) (name, isPressed, tpf) -> right = isPressed));
        mappings.add(new Mapping("Ups", "Look up", KeyInput.KEY_Z,
                (ActionListener) (name, isPressed, tpf) -> up = isPressed));
        mappings.add(new Mapping("Downs", "Look down", KeyInput.KEY_S,
                (ActionListener) (name, isPressed, tpf) -> down = isPressed));
        mappings.add(new Mapping("Space", "Jump", KeyInput.KEY_SPACE,
                (ActionListener) (name, isPressed, tpf) -> player.jump(Vector3f.UNIT_Y)));

        mappings.add(new Mapping(INPUT_CAMERA_TYPE, "Switch camera type", KeyInput.KEY_F2,
                (ActionListener) (name, isPressed, tpf) -> HelloCollisionAppState.super.onAction(name, isPressed, tpf)));

        mappings.add(new Mapping(INPUT_CAMERA_TYPE_FLY, "Switch to fly by camera", KeyInput.KEY_F3,
                (ActionListener) (name, isPressed, tpf) -> HelloCollisionAppState.super.onAction(name, isPressed, tpf)));
    }


    @Override
    protected void initialize(Application app) {
        super.initialize(app);
    }

    @Override
    protected void onEnable() {

        super.onEnable();

        log.info("Loading Hello Collision");

        // We re-use the flyby camera for rotation, while positioning is handled by physics
        my3DGame.getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        my3DGame.getFlyByCamera().setEnabled(true);
        my3DGame.getFlyByCamera().setMoveSpeed(100);
        setUpLight();

        // We load the scene from the zip file and adjust its size.
        my3DGame.getAssetManager().registerLocator(
                "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/town.zip",
                HttpZipLocator.class);
        sceneModel = my3DGame.getAssetManager().loadModel("main.scene");
        sceneModel.setLocalScale(2f);

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, stepheight, jumping, falling, and gravity.
        // We also put the player in its starting position.
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(30);
        player.setGravity(new Vector3f(0.f,-9.81f,0.f));
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        my3DGame.getRootNode().attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);

        setInputKeys();
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        my3DGame.getRootNode().addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        my3DGame.getRootNode().addLight(dl);
    }

    /** We over-write some navigational key mappings here, so we can
     * add physics-controlled walking and jumping: */
    private void setInputKeys() {
        for (Mapping mapping : mappings) {
            mapping.addToInputManager(my3DGame.getInputManager());
        }
    }

    private void clearInputKeys() {
        for (Mapping mapping : mappings) {
            my3DGame.getInputManager().deleteMapping(mapping.getName());
        }
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        super.onAction(binding,isPressed,tpf);
    }

    /**
     * This is the main event loop--walking happens here.
     * We check in which direction the player is walking by interpreting
     * the camera direction forward (camDir) and to the side (camLeft).
     * The setWalkDirection() command is what lets a physics-controlled player walk.
     * We also make sure here that the camera moves with player.
     */
    @Override
    public void update(float tpf) {
        camDir.set(my3DGame.getCamera().getDirection()).multLocal(0.6f);
        camLeft.set(my3DGame.getCamera().getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        my3DGame.getCamera().setLocation(player.getPhysicsLocation());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        log.info("Exiting Hello Collision");
        my3DGame.getFlyByCamera().setEnabled(false);
        my3DGame.getRootNode().getLocalLightList().clear();
        my3DGame.getRootNode().getWorldLightList().clear();
        clearInputKeys();
    }

    @Override
    protected void cleanup(Application app) {

    }
}
