package Settings;

import javax.swing.JComboBox;

public class Screenshots extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("BMP", "Windows bitmap (BMP)");
        settingsComboBox.put("RAW", "HSI 'mhwanh' (RAW)");

        return settingsComboBox.createComponent(currentKey);
    }
}
