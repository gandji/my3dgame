package org.gandji.my3dgame.objects.people;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.services.BehaviorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
@Deprecated
public class BehaviorController extends AbstractMy3DGameCharacterController {

    private final Person node;
    Vector3f targetPosition;

    @Deprecated
    public void setTarget(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    @Autowired
    BehaviorServices behaviorServices;

    public BehaviorController(Person person) {
        super(1,4,90 );
        this.node = person;
        setSpatial(node.getSpatial());
    }

    @Override
    protected CollisionShape getShape() {
        //TODO: cleanup size mess..
        CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(getFinalRadius(), (getFinalHeight() /*- (2 * getFinalRadius())*/));
        CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
        Vector3f addLocation = new Vector3f(0, (getFinalHeight() / 2.0f), 0);
        compoundCollisionShape.addChildShape(capsuleCollisionShape, addLocation);
        return compoundCollisionShape;    }

    @Override
    public void update(float tpf) {

        if (targetPosition==null) {
            return;
        }

        float distanceToTarget = targetPosition.subtract(node.getPosition()).length();

        if (distanceToTarget < 3.f || distanceToTarget > 50.f) {

            node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_SITTING.getId());
            node.getControl().setWalkDirection(new Vector3f());

        } else {
            Vector3f desiredVelocity = BehaviorServices.steer(node.getPosition(), targetPosition);
            if (desiredVelocity.length() < FastMath.ZERO_TOLERANCE) {
                node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_SITTING.getId());
                return;
            }

            node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_RUNNING.getId());
            desiredVelocity = desiredVelocity.mult(getVelocity());
            node.getControl().setViewDirection(desiredVelocity);
            node.getControl().setWalkDirection(desiredVelocity);
        }

        super.update(tpf);
    }

    @Override
    public void setAction(String name, boolean isPressed, float tpf) {

    }
}
