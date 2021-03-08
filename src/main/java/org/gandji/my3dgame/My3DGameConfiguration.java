package org.gandji.my3dgame;

import com.jme3.system.AppSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by gandji on 25/01/2020.
 */
@SpringBootApplication
@Slf4j
public class My3DGameConfiguration {

    static My3DGame app;

    /**
     * The app we build at init will become a bean only once the Spring framework is initialized,
     * in My3DGame.simpleInitApp
     * @return
     */
    @Bean
    public My3DGame my3DGame() {
        return app;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        app = new My3DGame();
        app.setShowSettings(false);

        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "My3DGame");
        settings.put("VSync", true);
        settings.put("Fullscreen", false);
        //Anti-Aliasing
        settings.put("Samples", 4);
        app.setSettings(settings);

        /**
         * This will initialize Spring framework,
         * and add the states as bean (debugKeys, audio state, camera state, stats state, main menu state)
         */
        app.start();

    }
}
