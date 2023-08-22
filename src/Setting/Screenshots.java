package src.Setting;

import javax.swing.JComboBox;

public class Screenshots extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("PNG", "Portable Network Graphics (PNG)");
        settingsComboBox.put("JPG", "Joint photographic experts group (JPG)");
        settingsComboBox.put("BMP", "Windows bitmap (BMP)");
        settingsComboBox.put("RAW", "HSI 'mhwanh' (RAW)");

        return settingsComboBox.createComponent(currentKey);
    }
}
