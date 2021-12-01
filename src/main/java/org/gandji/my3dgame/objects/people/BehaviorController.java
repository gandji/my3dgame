package org.gandji.my3dgame.objects.people;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.services.BehaviorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class BehaviorController extends BetterCharacterControl {

    private final Person node;
    private Node target;
    private float velocity;

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    enum CurrentBehavior {
        STEER;
    }

    @Autowired
    BehaviorServices behaviorServices;

    public BehaviorController(Person person, float velocity) {
        super(1,4,90 );// TODO person dimensions here
        this.node = person;
        this.velocity = velocity;
        setSpatial(node.getSpatial());
    }

    @Override
    public void update(float tpf) {

        if (target==null) {
            return;
        }

        Vector3f targetPosition = target.getWorldTranslation() ;

        Vector3f desiredVelocity = BehaviorServices.steer(node.getPosition(), targetPosition);

        if (desiredVelocity.length()< FastMath.ZERO_TOLERANCE) {
            return;
        }

        desiredVelocity = desiredVelocity.mult(velocity);

        node.getControl().setWalkDirection(desiredVelocity);

        super.update(tpf);
    }


}
