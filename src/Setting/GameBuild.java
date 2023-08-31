package src.Setting;

import javax.swing.JComboBox;

import src.KfxReleaseType;
import src.Main;

public class GameBuild extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put(KfxReleaseType.STABLE.toString(), "Stable");
        settingsComboBox.put(KfxReleaseType.ALPHA.toString(), "Alpha");

        // Add Prototype to text box if current game build is a prototype.
        // This makes it so it's only visible if we are on a prototype.
        if (Main.kfxReleaseType == KfxReleaseType.PROTOTYPE) {
            settingsComboBox.put(KfxReleaseType.PROTOTYPE.toString(), "Prototype");
        }

        return settingsComboBox.createComponent(currentKey);
    }
}
