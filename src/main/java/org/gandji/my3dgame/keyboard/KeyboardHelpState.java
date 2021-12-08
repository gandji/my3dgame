package org.gandji.my3dgame.keyboard;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.style.BaseStyles;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.My3DGame;
import org.gandji.my3dgame.states.ActionDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KeyboardHelpState extends BaseAppState {

    float fontSize = 24.f;

    private Container keyboardHelpWindow;

    private List<Node> children = new ArrayList<>();

    @Autowired
    private My3DGame my3DGame;

    @PostConstruct
    public void postConstruct() {
    }

    @Override
    protected void initialize(Application app) {
        log.debug("Initializing keyboard help state");
        BaseStyles.loadGlassStyle();
        keyboardHelpWindow = new Container();
        keyboardHelpWindow.setLocalTranslation(100,600,0);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        for (Node node : children) {
            keyboardHelpWindow.addChild(node);
        }
        log.debug(String.format("Attaching help window with %d children", keyboardHelpWindow.getChildren().size()));
        my3DGame.getGuiNode().attachChild(keyboardHelpWindow);
    }

    @Override
    protected void onDisable() {
        log.debug(String.format("Detaching help window with %d children", keyboardHelpWindow.getChildren().size()));
        keyboardHelpWindow.clearChildren();
        children.clear();
        my3DGame.getGuiNode().detachChild(keyboardHelpWindow);
    }

    public void buildMappingsHelp(List<Mapping> mappings) {

        if (mappings!=null && !mappings.isEmpty()) {
            for (Mapping mapping : mappings) {
                String converted = convertSomeNamesToHumanReadable(mapping.getName());
                Label textField = new Label(String.format(" %s  ->  %s", converted, mapping.getDescription()));
                textField.setFontSize(fontSize);
                children.add(textField);
            }
        } else {
            TextField textField = new TextField(String.format(" No keyboard help"));
            children.add(textField);
        }
    }

    private String convertSomeNamesToHumanReadable(String name) {

        // Grr I cannot change these and thus these conversions are mapping dependent
        if (name.equals("FLYCAM_Up")) {
            return "<Down>";
        }
        if (name.equals("FLYCAM_Down")) {
            return "<Up>";
        }
        if (name.equals("FLYCAM_Lower")) {
            return "<W>";
        }
        if (name.equals("FLYCAM_Rise")) {
            return "<A>";
        }
        if (name.equals("FLYCAM_StrafeLeft")) {
            return "<Q>";
        }
        if (name.equals("FLYCAM_Forward")) {
            return "<Z>";
        }
        if (name.equals("SIMPLEAPP_Exit")) {
            return "<ESC>";
        }
        if (name.equals(ActionDescriptor.INPUT_CAMERA_TYPE.name)) {
            return ActionDescriptor.INPUT_CAMERA_TYPE.defaultKeyDescription;
        }
        if (name.equals(ActionDescriptor.INPUT_CAMERA_TYPE_FLY.name)) {
            return ActionDescriptor.INPUT_CAMERA_TYPE_FLY.defaultKeyDescription;
        }

        return name;
    }

    public void clearMappingsHelp() {
        children.clear();
    }
}
