package org.gandji.my3dgame;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.font.BitmapFont;
import com.simsilica.lemur.GuiGlobals;
import lombok.extern.log4j.Log4j;
import org.gandji.my3dgame.states.FlyCamAppStateAzerty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class My3DGame extends SimpleApplication {

    @Autowired
    FerrariGameState ferrariGameState;

    @Autowired
    MenuAppState menuAppState;

    public ConfigurableApplicationContext context;

    public My3DGame(StatsAppState statsAppState,
                    FlyCamAppStateAzerty flyCamAppState,
                    AudioListenerState audioListenerState,
                    DebugKeysAppState debugKeysAppState) {
        super(statsAppState,flyCamAppState,audioListenerState,debugKeysAppState);
    }

    @Override
    public void simpleInitApp() {

        log.info("Starting Spring framework");

        // spring injection
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                new ApplicationContextInitializer<GenericApplicationContext>() {
                    @Override
                    public void initialize(GenericApplicationContext ac) {
                        ac.registerBean(SimpleApplication.class, () -> My3DGame.this);
                    }
                };

        this.context = new SpringApplicationBuilder()
                .sources(My3DGameConfiguration.class)
                .initializers(initializer)
                .run();

        GuiGlobals.initialize(this);
// Set 'glass' as the default style when not specified
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        getStateManager().attach(menuAppState);

        setDisplayFps(false);

        setDisplayStatView(false);


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