package org.gandji.my3dgame.objects.people;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
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

    AbstractMy3DGameCharacterController zombieControl;

    Node zombieNode;
    Spatial zombieSpatial;
    Health health;
    private float yOffset;

    private final float mass = 90.f;

    @Autowired
    private My3DGame my3DGame;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BulletAppState bulletAppState;

    @PostConstruct
    public void initialize() {

        zombieSpatial = my3DGame.getAssetManager().loadModel("Models/Sinbad/Sinbad.mesh.xml");

        BoundingBox bounds = (BoundingBox) zombieSpatial.getWorldBound();
        //scale spatial
        zombieSpatial.setLocalScale(1.8f / (bounds.getYExtent() * 2));
        bounds = (BoundingBox) zombieSpatial.getWorldBound();
        //spatial origin is at center so need to offset
        yOffset = bounds.getYExtent() - bounds.getCenter().y;
        zombieSpatial.setLocalTranslation(0, yOffset, 0);

        zombieNode = new Node("Zombie");
        zombieNode.attachChild(zombieSpatial);

        zombieNode.setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_RUNNING.getId());
        zombieNode.addControl(new AnimationControl());

        health = new Health(100L,100L);
        health.setPerson(this);

        zombieControl = applicationContext.getBean(PCControl.class,1,4, 90);
        zombieControl.setGravity(new Vector3f(0.f, -9.81f, 0.f));
        zombieControl.setPhysicsDamping(0.5f);
        zombieNode.addControl(zombieControl);
        bulletAppState.getPhysicsSpace().add(zombieControl);

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
        zombieSpatial.setLocalTranslation(worldPosition.add(0,yOffset,0));
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
