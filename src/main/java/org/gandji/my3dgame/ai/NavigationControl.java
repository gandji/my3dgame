package org.gandji.my3dgame.ai;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.cinematic.MotionPath;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Spline;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.My3DGame;
import org.gandji.my3dgame.objects.people.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements NavigationControl but actually extends Pathfinder which
 * uses the NavMesh, you can replace it with any Pathfinding system.
 *
 * @author adapted by mitm from the MonkeyZone project written by normenhansen.
 * then adapted by gandji for my3dgame
 */
@Component
@Scope("prototype")
@Slf4j
public class NavigationControl extends NavMeshPathfinder implements Control,
        JmeCloneable, Pickable {

    @Autowired
    private My3DGame my3DGame;

    private final ScheduledExecutorService executor;
    private AbstractMy3DGameCharacterController pcControl;
    private Spatial spatial;
    private boolean pathFinding;
    private Vector3f wayPosition;
    private MotionPath motionPath;
    private boolean showPath;
    private Vector3f target;

    public NavigationControl(NavMesh navMesh) {
        super(navMesh); //sets the NavMesh for this control
        motionPath = new MotionPath();
        motionPath.setPathSplineType(Spline.SplineType.Linear);
        executor = Executors.newScheduledThreadPool(1);
        startPathFinder();
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        try {
            NavigationControl c = (NavigationControl) clone();
            c.spatial = null; // to keep setSpatial() from throwing an exception
            c.setSpatial(spatial);
            return c;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Can't clone control for spatial", e);
        }
    }

    @Override
    public Object jmeClone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Can't clone control for spatial", e);
        }
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {
        this.spatial = cloner.clone(spatial);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException(
                    "This control has already been added to a Spatial");
        }
        this.spatial = spatial;
        if (spatial == null) {
            shutdownAndAwaitTermination(executor);
            pcControl = null;
        } else {
            pcControl = spatial.getControl(PCControl.class);
            if (pcControl == null) {
                throw new IllegalStateException(
                        "Cannot add NavigationControl to spatial without PCControl!");
            }
        }
    }

    //standard shutdown process for executor
    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(6, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(6, TimeUnit.SECONDS)) {
                    log.error(String.format("Pool did not terminate %s", pool.toString()));
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void update(float tpf) {
        if (getWayPosition() != null) {
            Vector3f spatialPosition = spatial.getWorldTranslation();
            Vector2f aiPosition = new Vector2f(spatialPosition.x,
                    spatialPosition.z);
            Vector2f waypoint2D = new Vector2f(getWayPosition().x,
                    getWayPosition().z);
            float distance = aiPosition.distance(waypoint2D);
            //move char between waypoints until waypoint reached then set null
            if (distance > .25f) {
                Vector2f direction = waypoint2D.subtract(aiPosition);
                direction.mult(tpf);
                pcControl.setViewDirection(new Vector3f(direction.x, 0,
                        direction.y).normalize());
                pcControl.setAction(ListenerKey.MOVE_FORWARD, true, 1);
            } else {
                setWayPosition(null);
            }
        } else if (!isPathFinding() && getNextWaypoint() != null
                && !isAtGoalWaypoint()) {
            //advance to next waypoint
            goToNextWaypoint();
            setWayPosition(new Vector3f(getWaypointPosition()));

            //set spatial physical position
            if (getPosType() == EnumPosType.POS_STANDING) {
                setPositionType(EnumPosType.POS_RUNNING);
                stopFeetPlaying();
                stopTorsoPlaying();
            }
        } else {
            //waypoint null so stop moving and set spatials physical position
            if (getPosType() == EnumPosType.POS_RUNNING) {
                setPositionType(EnumPosType.POS_STANDING);
                stopFeetPlaying();
                stopTorsoPlaying();
            }
            pcControl.setAction(ListenerKey.MOVE_FORWARD, false, 1);
        }

        if (!pathFinding && showPath) {
            if (motionPath.getNbWayPoints() > 0) {
                motionPath.enableDebugShape(my3DGame.getAssetManager(), my3DGame.getRootNode());
            }
        } else {
            try {
                motionPath.disableDebugShape();
            } catch (NullPointerException e) {

            }
        }

    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Computes a path using the A* algorithm. Every 1/2 second checks target
    //for processing. Path will remain untill a new path is generated.
    private void startPathFinder() {
        executor.scheduleWithFixedDelay(() -> {
            if (target != null) {
                clearPath();
                setWayPosition(null);
                pathFinding = true;

                //setPosition must be set before computePath is called.
                setPosition(spatial.getWorldTranslation());
                //*The first waypoint on any path is the one you set with
                //`setPosition()`.
                //*The last waypoint on any path is always the `target` Vector3f.
                //computePath() adds one waypoint to the cell *nearest* to the
                //target only if you are not in the goalCell (the cell target is in),
                //and if there is a cell between first and last waypoint,
                //and if there is no direct line of sight.
                //*If inside the goalCell when a new target is selected,
                //computePath() will do a direct line of sight placement of
                //target. This means there will only be 2 waypoints set,
                //`setPosition()` and `target`.
                //*If the `target` is outside the `NavMesh`, your endpoint will
                //be also.
                //warpInside(target) moves endpoint within the navMesh always.
                warpInside(target);
                log.debug("Target " + target);
                boolean success;
                //compute the path
                success = computePath(target);
                log.debug("SUCCESS = " + success);
                if (success) {
                    //clear target if successful
                    target = null;
                    //compute debug display motion path
                    buildDebugPath();
                }
                pathFinding = false;
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * @return the pathfinding
     */
    public boolean isPathFinding() {
        return pathFinding;
    }

    /**
     * @return the wayPosition
     */
    public Vector3f getWayPosition() {
        return wayPosition;
    }

    /**
     * @param wayPosition the wayPosition to set
     */
    public void setWayPosition(Vector3f wayPosition) {
        this.wayPosition = wayPosition;
    }

    //looks at UserData for the physical position of a spatial.
    private EnumPosType getPosType() {
        return EnumPosType.fromId(spatial.getUserData(DataKey.POSITION_TYPE));
    }

    //Sets the physical posType of a spatial.
    private void setPositionType(EnumPosType posType) {
        spatial.setUserData(DataKey.POSITION_TYPE, posType.getId());
    }

    //Stops the torso channel if playing an animation.
    private void stopTorsoPlaying() {
        spatial.getControl(AnimationControl.class).getTorsoChannel().setTime(
                spatial.getControl(AnimationControl.class).getTorsoChannel().
                        getAnimMaxTime());
    }

    //Stops the feet channel if playing an animation.
    private void stopFeetPlaying() {
        spatial.getControl(AnimationControl.class).getFeetChannel().setTime(
                spatial.getControl(AnimationControl.class).getFeetChannel().
                        getAnimMaxTime());
    }

    //Displays a motion path showing each waypoint. Stays in scene until another
    //path is set.
    private void buildDebugPath() {
        if (motionPath.getNbWayPoints() > 0) {
            motionPath.clearWayPoints();
        }

        for (Path.Waypoint wp : getPath().getWaypoints()) {
            motionPath.addWayPoint(wp.getPosition());
        }
    }

    public void toggleDisplayMotionPath() {
        showPath = !showPath;
    }

    /**
     * @param target the target to set
     */
    @Override
    public void setTarget(Vector3f target) {
        motionPath.clearWayPoints();
        try {
            motionPath.disableDebugShape();
        } catch (NullPointerException e) {
            // it's ok, means motion path was not displayed
        }
        this.target = target;

    }

}
