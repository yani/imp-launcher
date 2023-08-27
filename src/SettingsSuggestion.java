package src;

import java.awt.*;
import java.io.File;
import java.util.*;

import javax.swing.JOptionPane;

import src.Setting.GameBuild;
import src.Setting.GameLanguages;
import src.Setting.LowMidHigh;
import src.Setting.ScreenResolutions;
import src.Setting.Screenshots;

public class SettingsSuggestion {

    public static void change() {

        System.out.println("Changing user settings to ImpLauncher suggested settings");

        // Get screen size
        Rectangle screenSize = SettingsSuggestion.getScreenSize(0);
        System.out.println("Display[0] screen size: " + screenSize.toString());

        // GAMEPLAY
        Main.keeperFxCfg.setProperty("LANGUAGE", "ENG"); // TODO: select system language, fallback to english
        Main.runOptions.toggleOption("nointro", false);
        Main.runOptions.toggleOption("alex", true); // Cheats
        Main.keeperFxCfg.setProperty("CENSORSHIP", "OFF");
        Main.keeperFxCfg.setProperty("SCREENSHOT", "PNG");

        // GRAPHICS
        Main.runOptions.toggleOption("vidsmooth", false);
        if (screenSize.getWidth() <= 2560 && screenSize.getHeight() <= 1440) {
            String resolutionString = ((int) screenSize.getWidth()) + "x" + ((int) screenSize.getHeight()) + "w32";
            Main.keeperFxCfg.setProperty("INGAME_RES", resolutionString);
            Main.keeperFxCfg.setProperty("FRONTEND_RES",
                    resolutionString + " " + resolutionString + " " + resolutionString);
        }

        // SOUND
        Main.runOptions.toggleOption("nosound", false);
        Main.runOptions.toggleOption("nocd", true);
        Main.keeperFxCfg.setProperty("ATMOSPHERIC_SOUNDS", "ON");
        Main.keeperFxCfg.setProperty("ATMOS_FREQUENCY", "MEDIUM");
        Main.keeperFxCfg.setProperty("ATMOS_VOLUME", "LOW");
        Main.keeperFxCfg.setProperty("PAUSE_MUSIC_WHEN_GAME_PAUSED", "OFF");
        Main.keeperFxCfg.setProperty("MUTE_AUDIO_ON_FOCUS_LOST", "OFF");

        // INPUT
        Main.keeperFxCfg.setProperty("POINTER_SENSITIVITY", "0");
        Main.runOptions.toggleOption("altinput", false);
        Main.keeperFxCfg.setProperty("UNLOCK_CURSOR_WHEN_GAME_PAUSED", "OFF");
        Main.keeperFxCfg.setProperty("CURSOR_EDGE_CAMERA_PANNING", "ON");

        // MP
        Main.keeperFxCfg.setProperty("MASTERSERVER_HOST", "masterserver.keeperfx.net");

        // Save cfg options and run options
        try {
            Main.keeperFxCfg.update(new File(Main.launcherRootDir + File.separator + "keeperfx.cfg"));
            Main.runOptions.saveOptionsToFile();
        } catch (Exception ex) {
        }

        // Set to ALPHA and start update process
        if (Main.kfxReleaseType != KfxReleaseType.ALPHA) {
            new Thread(() -> (new GameUpdater(Main.main)).checkForUpdates(KfxReleaseType.ALPHA)).start();
        }
    }

    public static Rectangle getScreenSize(int displayIndex) {
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // Windows scaled sizes (eg 1280x720 for my case at 150% scaling)
        Rectangle bounds = devices[displayIndex].getDefaultConfiguration().getBounds();

        // Display sizes (same as above at 100% scale, 1920x1080 for my case)
        DisplayMode dm = devices[displayIndex].getDefaultConfiguration().getDevice().getDisplayMode();
        Rectangle orig = new Rectangle((int) bounds.getX(), (int) bounds.getY(), dm.getWidth(), dm.getHeight());

        return orig;
    }

}
