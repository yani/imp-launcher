package src.Setting;

import javax.swing.JComboBox;

public class ScreenResolutions extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("640x400", "640 x 400");
        settingsComboBox.put("640x480", "640 x 480");
        settingsComboBox.put("1024x768", "1024 x 768");
        settingsComboBox.put("1280x800", "1280 x 800");
        settingsComboBox.put("1280x1024", "1280 x 1024");
        settingsComboBox.put("1366x768", "1366 x 768");
        settingsComboBox.put("1440x900", "1440 x 900");
        settingsComboBox.put("1536x864", "1536 x 864");
        settingsComboBox.put("1600x900", "1600 x 900");
        settingsComboBox.put("1600x1200", "1600 x 1200");
        settingsComboBox.put("1920x1080", "1920 x 1080");
        settingsComboBox.put("1920x1200", "1920 x 1200");
        settingsComboBox.put("2560x1440", "2560 x 1440");

        return settingsComboBox.createComponent(currentKey);
    }
}
