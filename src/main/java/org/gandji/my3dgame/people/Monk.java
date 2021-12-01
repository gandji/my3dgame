package org.gandji.my3dgame.people;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class Monk implements Person, Drawable {

    private static final Role ROLE = Role.MONK;

    String name;

    Health health;

    @Autowired
    MonkNode monkNode;

    @PostConstruct
    public void init() {
        health = new Health(100L,100L);
    }

    public void setName(String name) {
        this.name = name;
        this.monkNode.setName(name);
    }

    // Person
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Role getRole() {
        return ROLE;
    }

    @Override
    public Health getHealth() {
        return health;
    }

    @Override
    public BetterCharacterControl getControl() {
        return null;
    }

    @Override
    public void setPosition(Vector3f worldPosition) {

    }

    @Override
    public Vector3f getPosition() {
        return null;
    }

    // Drawable
    @Override
    public Spatial getSpatial() {
        return monkNode;
    }
}
