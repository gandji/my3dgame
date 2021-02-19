package org.gandji.my3dgame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import org.gandji.my3dgame.My3DGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class My3DGameBaseAppState extends BaseAppState implements ActionListener {
    @Autowired
    protected
    My3DGame my3DGame;

    @Autowired
    protected
    ApplicationContext applicationContext;

    @Autowired
    protected
    BulletAppState bulletAppState;

    @Autowired
    protected MenuAppState menuAppState;

    @Override
    protected void initialize(Application app) {
        // no need for this anymore, spring does it for us:
        // this.my3DGame = (My3DGame) app;
    }

    @Override
    protected void onEnable() {
        my3DGame.getStateManager().attach(bulletAppState);

        if (my3DGame.getInputManager() != null) {
            if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
                my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);

            my3DGame.getInputManager().addMapping(SimpleApplication.INPUT_MAPPING_EXIT, new KeyTrigger(KeyInput.KEY_ESCAPE));
            my3DGame.getInputManager().addListener(this,SimpleApplication.INPUT_MAPPING_EXIT);
        }
    }

    @Override
    protected void onDisable() {
        if (my3DGame.getInputManager().hasMapping(SimpleApplication.INPUT_MAPPING_EXIT))
            my3DGame.getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        my3DGame.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

    }

    protected void backToMenu() {
        my3DGame.getRootNode().detachAllChildren();
        my3DGame.getStateManager().detach(this);
        my3DGame.getStateManager().attach(menuAppState);
    }
}
