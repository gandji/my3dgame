package org.gandji.my3dgame.people;

import com.jme3.asset.ModelKey;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.My3DGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
@Slf4j
public class Zombie implements Person {

    BetterCharacterControl zombieControl = null;
    Node zombieNode;
    Health health;
    Vector3f position;
    Vector3f speed;

    @Autowired
    private My3DGame my3DGame;

    @PostConstruct
    public void initialize() {

        zombieControl = new BetterCharacterControl( 1.5f, 3.f, 90.f);
        //zombieCharacter.setJumpSpeed(50);
        //zombieCharacter.setFallSpeed(30);
        zombieControl.setGravity(new Vector3f(0.f,-9.81f,0.f));

        ModelKey keyMonk = new ModelKey("Models/monk.glb");
        zombieNode = (Node) my3DGame.getAssetManager().loadModel(keyMonk);
        //zombieCharacter.setSpatial(zombieNode);
        zombieNode.addControl(zombieControl);

        health = new Health(100L,100L);
        health.setPerson(this);

        Light pointLight = new PointLight();
        //pointLight.setColor(ColorRGBA.Yellow);
        ((PointLight) pointLight).setPosition(new Vector3f(4,4,4));
        ((PointLight) pointLight).setRadius(125.f);
        pointLight.setName("Zombida");
        zombieNode.addLight(pointLight);

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
        return zombieControl;
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
}
