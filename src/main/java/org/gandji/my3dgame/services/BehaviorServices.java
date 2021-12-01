package org.gandji.my3dgame.services;

import com.jme3.math.Vector3f;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BehaviorServices {
    public static Vector3f steer(Vector3f position, Vector3f target) {
        Vector3f to = target.subtract(position).setY(0);
        if (to.length()< 0.1) {
            to.set(0,0,0);
        }
        return to.normalize();
    }
}
