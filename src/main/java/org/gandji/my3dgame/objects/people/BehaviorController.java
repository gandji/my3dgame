package org.gandji.my3dgame.objects.people;

import com.jme3.animation.AnimControl;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
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
    protected CollisionShape getShape() {
        //TODO: cleanup size mess..
        CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(getFinalRadius(), (getFinalHeight() /*- (2 * getFinalRadius())*/));
        CompoundCollisionShape compoundCollisionShape = new CompoundCollisionShape();
        Vector3f addLocation = new Vector3f(0, (getFinalHeight() / 2.0f), 0);
        compoundCollisionShape.addChildShape(capsuleCollisionShape, addLocation);
        return compoundCollisionShape;    }

    @Override
    public void update(float tpf) {

        if (target==null) {
            return;
        }

        Vector3f targetPosition = target.getWorldTranslation() ;

        float distanceToTarget = targetPosition.subtract(node.getPosition()).length();

        if (distanceToTarget < 3.f) {

            node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_SITTING.positionType());
            node.getControl().setWalkDirection(new Vector3f());

        } else {
            Vector3f desiredVelocity = BehaviorServices.steer(node.getPosition(), targetPosition);
            if (desiredVelocity.length() < FastMath.ZERO_TOLERANCE) {
                node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_SITTING.positionType());
                return;
            }

            node.getSpatial().setUserData(DataKey.POSITION_TYPE, EnumPosType.POS_RUNNING.positionType());
            desiredVelocity = desiredVelocity.mult(velocity);
            node.getControl().setViewDirection(desiredVelocity);
            node.getControl().setWalkDirection(desiredVelocity);
        }

        super.update(tpf);
    }


}
