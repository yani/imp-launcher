package src.Setting;

import javax.swing.JComboBox;

import src.KfxReleaseType;

public class GameBuild extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put(KfxReleaseType.STABLE.toString(), "Stable");
        settingsComboBox.put(KfxReleaseType.ALPHA.toString(), "Alpha");
        // settingsComboBox.put(KfxReleaseType.PROTOTYPE.toString(), "Prototype");

        return settingsComboBox.createComponent(currentKey);
    }
}
