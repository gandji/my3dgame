package org.gandji.my3dgame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.keyboard.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandji on 18/01/2020.
 */
@Component
@Slf4j
public class My3DGameTerrain {

    @Autowired
    My3DGame my3DGame;

    AssetManager assetManager;
    Node rootNode;
    Camera cam;

    TerrainGenerationType terrainGenerationType = TerrainGenerationType.HEIGHT_MAP;
    TerrainQuad terrain;
    Light light;

    boolean wireframe = false;
    boolean triPlanar = false;
    Material matWire, matRock;
    float grassScale = 64;
    float dirtScale = 16;
    float rockScale = 128;

    private List<Mapping> mappings = new ArrayList<>();

    enum TerrainGenerationType {
        HEIGHT_MAP,
        SCENE;
    }

    public My3DGameTerrain() {
    }

    /**
     * "Ideal" initial position of an object
     * @return
     */
    public Vector3f getInitialPosition() {
        if (terrainGenerationType==TerrainGenerationType.HEIGHT_MAP) {
            return new Vector3f(244.5f, 16.212f, 5);
        } else if (terrainGenerationType==TerrainGenerationType.SCENE) {
            return new Vector3f(-55.94f, 72.72f, 51.f);
        }
        return Vector3f.ZERO;
    }

    public void loadAssets() {
        this.assetManager = my3DGame.getAssetManager();
        this.rootNode = my3DGame.getRootNode();
        this.cam = my3DGame.getCamera();
        loadMaterials();
        loadScene();
    }

    private void loadMaterials() {
        // TERRAIN TEXTURE material
        matRock = new Material(this.assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);

        // ALPHA map (for splat textures)
        matRock.setTexture("Alpha", this.assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

        // GRASS texture
        Texture grass = this.assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", grassScale);

        // DIRT texture
        Texture dirt = this.assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex2", dirt);
        matRock.setFloat("Tex2Scale", dirtScale);

        // ROCK texture
        Texture rock = this.assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("Tex3", rock);
        matRock.setFloat("Tex3Scale", rockScale);

        // WIREFRAME material
        matWire = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWire.getAdditionalRenderState().setWireframe(true);
        matWire.setColor("Color", ColorRGBA.Green);
    }

    private void loadScene() {
        Node firstScene = null;
        try {

            firstScene = (Node)assetManager.loadModel("MyAssetPack/assets/firstScene.j3o");

            log.debug(String.format("Loaded scene type %s",firstScene.getClass().getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // using height map:
        if (terrainGenerationType == TerrainGenerationType.HEIGHT_MAP) {
            // CREATE HEIGHTMAP
            AbstractHeightMap heightmap = null;

            try {
                // three ways to compute the height map
                // 1) heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

                // 2) heightmap = new FluidSimHeightMap(1025, 1000);

                // 3) with a height map image
                Texture heightMapImage = this.assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
                heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);

                heightmap.load();

            } catch (Exception e) {
                e.printStackTrace();
            }

            heightmap.smooth(0.9f,2);
            /*
             * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
             * terrain will be 513x513. It uses the height map we created to generate the height values.
             */
            /**
             * Optimal terrain patch size is 65 (64x64).
             * The total size is up to you. At 1025 it ran fine for me (200+FPS), however at
             * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
             */

            terrain = new TerrainQuad("terrain", 65,  1025, heightmap.getHeightMap());
            TerrainLodControl control = new TerrainLodControl(terrain, this.cam);
            control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
            terrain.addControl(control);
            terrain.setMaterial(matRock);
            terrain.setLocalTranslation(0, 00, 0);
            terrain.setLocalScale(2f, 0.5f, 2f);
        }
        else if (terrainGenerationType == TerrainGenerationType.SCENE) {

            // using scene
            terrain = (TerrainQuad) firstScene.getChild("terrain-firstScene");
        }

        TerrainLodControl lodControl = terrain.getControl(TerrainLodControl.class);
        if (lodControl != null)
            lodControl.setCamera(this.cam);

        // collision shape for terrain
        /* RigidBodyControl with mass zero.*/
        terrain.addControl(new RigidBodyControl(0));

        light = new DirectionalLight();
        ((DirectionalLight)light).setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        terrain.addLight(light);
    }

    public List<Mapping> setupKeys() {

        mappings.add(new Mapping("<T>", "Switch to wireframe", KeyInput.KEY_T,
                (ActionListener) (name, isPressed, tpf) -> {
                    if (!isPressed) {
                    wireframe = !wireframe;
                    if (wireframe) {
                        terrain.setMaterial(matWire);
                    } else {
                        terrain.setMaterial(matRock);
                    }                }})
                .updateMapping(my3DGame.getInputManager()));

        mappings.add(new Mapping("<P>", "Switch to tri-planar texturing", KeyInput.KEY_P,
                (ActionListener) (name, isPressed, tpf) -> {
                    if (!isPressed) {
                        triPlanar = !triPlanar;
                        if (triPlanar) {
                            matRock.setBoolean("useTriPlanarMapping", true);
                            // planar textures don't use the mesh's texture coordinates but real world coordinates,
                            // so we need to convert these texture coordinate scales into real world scales so it looks
                            // the same when we switch to/from tri-planar mode
                            matRock.setFloat("Tex1Scale", 1f / (float) (512f / grassScale));
                            matRock.setFloat("Tex2Scale", 1f / (float) (512f / dirtScale));
                            matRock.setFloat("Tex3Scale", 1f / (float) (512f / rockScale));
                        } else {
                            matRock.setBoolean("useTriPlanarMapping", false);
                            matRock.setFloat("Tex1Scale", grassScale);
                            matRock.setFloat("Tex2Scale", dirtScale);
                            matRock.setFloat("Tex3Scale", rockScale);
                        }
                    }})
                .updateMapping(my3DGame.getInputManager()));

        return mappings;
    }

    public void disableKeys() {
        for (Mapping mapping : mappings) {
            mapping.remove(my3DGame.getInputManager());
        }
        mappings.clear();
    }

    public TerrainQuad getTerrain() {
        return terrain;
    }
}
