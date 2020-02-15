package org.gandji.my3dgame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.lemur.*;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gandji on 25/01/2020.
 */
@Component
@Log4j
public class MenuAppState extends BaseAppState {

    float fontSize = 36.f;

    @Autowired
    My3DGame my3DGame;

    @Autowired
    AppCloseListener appCloseListener;

    @Autowired
    FerrariGameState ferrariGameState;

    Container menuWindow;

    @Override
    protected void initialize(Application app) {
        BaseStyles.loadGlassStyle();
        menuWindow = new Container();

// Put it somewhere that we will see it.
// Note: Lemur GUI elements grow down from the upper left corner.
        menuWindow.setLocalTranslation(100, 500, 0);

// Add some elements
        Label title = new Label("My3DGame");
        title.setFontSize(fontSize);
        menuWindow.addChild(title);
        Button ferrari = new Button("Play with the Ferraris");
        ferrari.setFontSize(fontSize);
        ferrari = menuWindow.addChild(ferrari);
        ferrari.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
                log.info("Wanna play with the Ferrari?");
                my3DGame.getStateManager().detach(MenuAppState.this);
                my3DGame.getStateManager().attach(ferrariGameState);
            }
        });
        Button close = new Button("Exit");
        close.setFontSize(fontSize);
        close = menuWindow.addChild(close);
        close.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                log.info("Bye...");
               my3DGame.stop();
            }
        });
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        log.info("Entering menu mode");
// Create a simple container for our elements
        my3DGame.getGuiNode().attachChild(menuWindow);
// put it back:
        //if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
          //  my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().addMapping(SimpleApplication.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
        my3DGame.getInputManager().addListener(appCloseListener,SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().setCursorVisible(true);
    }

    @Override
    protected void onDisable() {
        log.info("Exiting menu mode");
        my3DGame.getGuiNode().detachChild(menuWindow);
        my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

    }
}
