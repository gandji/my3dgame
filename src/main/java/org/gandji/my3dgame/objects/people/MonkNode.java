package org.gandji.my3dgame.objects.people;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.gandji.my3dgame.My3DGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MonkNode extends Node {

    Spatial spatial;

    @Autowired
    My3DGame my3DGame;

    public MonkNode() {
        super("unnamed monk");
    }

    public MonkNode(String nodeName) {
        super(nodeName);
        spatial = my3DGame.getAssetManager().loadModel("Models/monk.blend");
        attachChild(spatial);
    }

}
