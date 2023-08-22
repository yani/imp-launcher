package src.Setting;

import javax.swing.JComboBox;

public class LowMidHigh extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("LOW", "Low");
        settingsComboBox.put("MEDIUM", "Medium");
        settingsComboBox.put("HIGH", "High");

        return settingsComboBox.createComponent(currentKey);
    }
}
