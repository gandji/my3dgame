package org.gandji.my3dgame.objects.people;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Controls the spatials movement. Speed is derived from EnumPosType.
 *
 * @author mitm
 */
@Component
@Scope("prototype")
@Slf4j
public class PCControl extends AbstractMy3DGameCharacterController {

    private boolean forward;
    private float moveSpeed;
    private EnumPosType position;

    public PCControl(float radius, float height, float mass) {
        super(radius, height, mass);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        this.moveSpeed = 0;
        walkDirection.set(0, 0, 0);
        if (forward) {
            Vector3f modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
            position = getPositionType();
            moveSpeed = position.speed();
            /* REMOVE switch (position) {
                case POS_RUNNING:
                    moveSpeed = EnumPosType.POS_RUNNING.speed();
                    break;
                default:
                    moveSpeed = 0f;
                    break;
            }*/
            walkDirection.addLocal(modelForwardDir.mult(moveSpeed));
        }
        setWalkDirection(walkDirection);
    }

    @Override
    public void setAction(String name, boolean isPressed, float tpf) {
        if (name.equals(ListenerKey.MOVE_FORWARD)) {
            forward = isPressed;
        }
    }

    //need to overide because we extended BetterCharacterControl
    @Override
    public PCControl cloneForSpatial(Spatial spatial) {
        try {
            PCControl control = (PCControl) super.clone();
            control.setSpatial(spatial);
            return control;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Clone Not Supported", ex);
        }
    }

    //need to override because we extended BetterCharacterControl
    @Override
    public PCControl jmeClone() {
        try {
            return (PCControl) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Clone Not Supported", ex);
        }
    }

    //gets the physical position of spatial
    private EnumPosType getPositionType() {
        return EnumPosType.fromId(spatial.getUserData(DataKey.POSITION_TYPE));
    }

}
