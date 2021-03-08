package org.gandji.my3dgame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.simsilica.lemur.GuiGlobals;
import lombok.extern.log4j.Log4j;
import org.gandji.my3dgame.states.FlyCamAppStateAzerty;
import org.gandji.my3dgame.states.MenuAppState;
import org.gandji.my3dgame.states.My3DGameDebugKeysAppState;
import org.gandji.my3dgame.states.My3DGameStatsAppState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@Log4j
public class My3DGame extends SimpleApplication {

    @Autowired
    MenuAppState menuAppState;

    @Autowired
    My3DGameDebugKeysAppState debugKeysAppState;

    @Autowired
    My3DGameStatsAppState statsAppState;

    @Autowired
    FlyCamAppStateAzerty flyCamAppState;

    @Autowired
    My3DGameAudioListenerState audioListenerState;

    public ConfigurableApplicationContext configurableApplicationContext;

    public My3DGame() {
        /*
         * the null is very important, otherwise, the app gets initialized
         * with some default states, which we do not want. We want all states
         * to be our custom states.
         */
        super(null);
    }

    /**
     * We intercept the initialize of SimpleApplication
     * and rewrite it to use our custom (Spring injected) states.
     */
    @Override
    public void initialize() {

        log.debug("Starting Spring framework");

        // spring injection
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                new ApplicationContextInitializer<GenericApplicationContext>() {
                    @Override
                    public void initialize(GenericApplicationContext ac) {
                        ac.registerBean(SimpleApplication.class, () -> My3DGame.this);
                    }
                };

        this.configurableApplicationContext = new SpringApplicationBuilder()
                .sources(My3DGameConfiguration.class)
                .initializers(initializer)
                .run();

        log.debug("OK Spring Framework done....");
        log.debug("Attaching base states");
        getStateManager().attach(debugKeysAppState);
        getStateManager().attach(statsAppState);
        getStateManager().attach(flyCamAppState);
        getStateManager().attach(audioListenerState);

        log.debug("initialization of JMonkeyEngine");
        super.initialize();

        log.debug("Initialization of GUI");
        GuiGlobals.initialize(this);
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        setDisplayFps(false);

        setDisplayStatView(false);

        /*
         * These states are not needed for initialization,
         * they need to be attached post-initialization
         */
        log.debug("Attaching main menu");
        getStateManager().attach(menuAppState);

        log.debug("Initialization done...");
    }

    @Override
    public void simpleInitApp() {
        // no use, we need to intercept whole initialize() method
    }

    public BitmapFont getGuiFont() {
        return guiFont;
    }

    @Override
    public void simpleUpdate(float tpf) {
        // make the car cube rotate:
        //ferrari.rotate(0, 0.5f*tpf, 0);
    }

}