package Settings;

import javax.swing.JComboBox;

public class DisplayModes extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("x", "Fullscreen");
        settingsComboBox.put("w", "Windowed");

        return settingsComboBox.createComponent(currentKey);
    }
}
