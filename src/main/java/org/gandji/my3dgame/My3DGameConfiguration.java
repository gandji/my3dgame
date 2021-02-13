package org.gandji.my3dgame;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.system.AppSettings;
import lombok.extern.slf4j.Slf4j;
import org.gandji.my3dgame.states.FlyCamAppStateAzerty;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by gandji on 25/01/2020.
 */
@SpringBootApplication
@Slf4j
public class My3DGameConfiguration {

    static My3DGame app;

    @Bean
    public My3DGame my3DGame() {
        return app;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        app = new My3DGame(new StatsAppState(), new FlyCamAppStateAzerty(), new AudioListenerState(), new DebugKeysAppState());
        app.setShowSettings(false);

        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "My3DGame");
        settings.put("VSync", true);
        settings.put("Fullscreen", true);
        //Anti-Aliasing
        settings.put("Samples", 4);
        app.setSettings(settings);

        app.start();

    }
}
