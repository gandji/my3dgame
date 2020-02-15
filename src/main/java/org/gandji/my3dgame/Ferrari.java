package org.gandji.my3dgame;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by gandji on 18/01/2020.
 */
@Component
@Scope("prototype")
public class Ferrari implements ActionListener {

    @Autowired
    BulletAppState bulletAppState;

    @Autowired
    My3DGame my3DGame;

    Spatial spatial;
    Node node;


    Vector3f initialPosition;

    float stiffness = 20.f; //120.0f;//200=f1 car
    float compValue = 0.4f; //(lower than damp!)
    float dampValue = 0.5f;
    final float mass = 300;

    float wheelRadius;

    VehicleControl driver;
    private float steeringValue = 0;
    private float accelerationValue = 0;

    public Ferrari() {
    }

    @PostConstruct
    public void construct() {


        // y is up, x is left, z is backward for ferrari
        spatial = my3DGame.getAssetManager().loadModel("Models/Ferrari/Car.scene");
        node = new Node("Ferrari Node");
        node.attachChild(spatial);

        spatial.setShadowMode(RenderQueue.ShadowMode.Cast);
        Geometry chasis = Utils.findGeom(spatial, "Car");

        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        driver = new VehicleControl(carHull, mass);
        node.addControl(driver);

        //Setting default values for wheels
        driver.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        driver.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        driver.setSuspensionStiffness(stiffness);
        driver.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Geometry wheel_fr = Utils.findGeom(spatial, "WheelFrontRight");
        wheel_fr.center();
        BoundingBox box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        driver.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_fl = Utils.findGeom(spatial, "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        driver.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = Utils.findGeom(spatial, "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        driver.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = Utils.findGeom(spatial, "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        driver.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        driver.getWheel(2).setFrictionSlip(4);
        driver.getWheel(3).setFrictionSlip(4);

        bulletAppState.getPhysicsSpace().add(driver);

        // lights : put elsewhere?
        Light ferrariLight1 = new PointLight();
        //ferrariLight1.setColor(ColorRGBA.Yellow);
        ((PointLight) ferrariLight1).setPosition(new Vector3f(4,4,4));
        ((PointLight) ferrariLight1).setRadius(125.f);
        ferrariLight1.setName("Ferrari1");
        getSpatial().addLight(ferrariLight1);

        Light ferrariLight2 = new PointLight();
        //ferrariLight1.setColor(ColorRGBA.Yellow);
        ((PointLight) ferrariLight2).setPosition(new Vector3f(-4,4,-4));
        ((PointLight) ferrariLight2).setRadius(125.f);
        ferrariLight2.setName("Ferrari2");
        getSpatial().addLight(ferrariLight2);

        Light ferrariLight3 = new AmbientLight();
        ferrariLight3.setColor(ColorRGBA.White.mult(2.5f));
        ferrariLight3.setName("Ferrari3");
        getSpatial().addLight(ferrariLight3);

    }

    public void setupKeys(InputManager inputManager) {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Backs", new KeyTrigger(KeyInput.KEY_COMMA));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Backs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
    }



    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            driver.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            driver.steer(steeringValue);
        } //note that our fancy car actually goes backwards..
        else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue = 0;
            }
            driver.accelerate(accelerationValue);
            driver.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(Utils.findGeom(spatial, "Car")));
        }
        else if (binding.equals("Backs")) {
                if (value) {
                    accelerationValue += 500;
                } else {
                    accelerationValue = 0;
                }
                driver.accelerate(accelerationValue);
                driver.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(Utils.findGeom(spatial, "Car")));
        } else if (binding.equals("Downs")) {
            if (value) {
                driver.brake(40f);
            } else {
                driver.brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                resetPosition();
            } else {
            }
        }
    }

    public void resetPosition() {
        driver.setPhysicsLocation(initialPosition);
        driver.setPhysicsRotation(new Matrix3f());
        driver.setLinearVelocity(Vector3f.ZERO);
        driver.setAngularVelocity(Vector3f.ZERO);
        driver.resetSuspension();

    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public Node getNode() {
        return node;
    }

    public void setDriver(VehicleControl driver) {
        this.driver = driver;
    }

    public VehicleControl getDriver() {
        return driver;
    }

    public Vector3f getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Vector3f initialPosition) {
        this.initialPosition = initialPosition;
    }
}
