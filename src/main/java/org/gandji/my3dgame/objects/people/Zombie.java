package org.gandji.my3dgame.objects.people;

import com.jme3.asset.ModelKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.My3DGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
@Slf4j
public class Zombie implements Person {

    BehaviorController zombiePhysics;

    Node zombieNode;
    Health health;

    private final float mass = 90.f;

    @Autowired
    private My3DGame my3DGame;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BulletAppState bulletAppState;

    @PostConstruct
    public void initialize() {

        //ModelKey keyMonk = new ModelKey("Models/monk.glb");
        ModelKey keyMonk = new ModelKey("Models/gears-of-war-3-lambent-female.glb");
        zombieNode = (Node) my3DGame.getAssetManager().loadModel(keyMonk);

        health = new Health(100L,100L);
        health.setPerson(this);

        zombiePhysics = applicationContext.getBean(BehaviorController.class,this, 4);
        zombiePhysics.setGravity(new Vector3f(0.f, -9.81f, 0.f));
        zombiePhysics.setPhysicsDamping(0.5f);
        zombieNode.addControl(zombiePhysics);
        bulletAppState.getPhysicsSpace().add(zombiePhysics);

        Light pointLight = new PointLight();
        ((PointLight) pointLight).setPosition(new Vector3f(4,4,4));
        ((PointLight) pointLight).setRadius(125.f);
        ((PointLight) pointLight).setColor(ColorRGBA.White);
        ((PointLight) pointLight).setEnabled(true);
        ((PointLight) pointLight).setFrustumCheckNeeded(true);
        pointLight.setName("Zombida");
        zombieNode.addLight(pointLight);

        Light zombieLight2 = new PointLight();
        ((PointLight) zombieLight2).setPosition(new Vector3f(-4,4,-4));
        ((PointLight) zombieLight2).setRadius(125.f);
        zombieLight2.setName("Zombido");
        getSpatial().addLight(zombieLight2);

        Light zombieLight3 = new AmbientLight();
        zombieLight3.setColor(ColorRGBA.White.mult(2.5f));
        zombieLight3.setName("Zombidu");
        getSpatial().addLight(zombieLight3);

    }

    public void setBehaviorController(BehaviorController behaviorController) {

        if (zombiePhysics!=null) {
            zombieNode.removeControl(zombiePhysics);
            bulletAppState.getPhysicsSpace().remove(zombiePhysics);
            zombiePhysics = null;
        }

        if (behaviorController!=null) {
            this.zombiePhysics = behaviorController;
            zombieNode.addControl(zombiePhysics);
            bulletAppState.getPhysicsSpace().add(zombiePhysics);
        }
    }

    @Override
    public Role getRole() {
        return Role.ZOMBIE;
    }

    @Override
    public String getName() {
        return "Zomibi";
    }

    @Override
    public Health getHealth() {
        return health;
    }

    @Override
    public BetterCharacterControl getControl() {
        return zombiePhysics;
    }

    @Override
    public void setPosition(Vector3f worldPosition) {
        zombieNode.setLocalTranslation(worldPosition);
    }

    @Override
    public Vector3f getPosition() {
        return zombieNode.getWorldTranslation();
    }

    @Override
    public Spatial getSpatial() {
        return zombieNode;
    }

    public void setTarget(Node target) {
        if (this.zombiePhysics!=null) {
            this.zombiePhysics.setTarget(target);
        }
    }

    public void setVelocity(float velocity) {
        if (this.zombiePhysics!=null) {
            this.zombiePhysics.setVelocity(velocity);
        }
    }
}
