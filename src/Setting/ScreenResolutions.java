package src.Setting;

import javax.swing.JComboBox;

public class ScreenResolutions extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        settingsComboBox.put("640x400", "640 x 400"); // 8:5
        settingsComboBox.put("640x480", "640 x 480"); // 4:3
        settingsComboBox.put("800x600", "800 x 600"); // 4:3
        settingsComboBox.put("1024x768", "1024 x 768"); // 4:3
        settingsComboBox.put("1280x720", "1280 x 720"); // 16:9
        settingsComboBox.put("1280x800", "1280 x 800"); // 8:5
        settingsComboBox.put("1280x1024", "1280 x 1024"); // 5:4
        settingsComboBox.put("1366x768", "1366 x 768"); // 16:9
        settingsComboBox.put("1440x900", "1440 x 900"); // 8:5
        settingsComboBox.put("1536x864", "1536 x 864"); // 16:9
        settingsComboBox.put("1600x900", "1600 x 900"); // 16:9
        settingsComboBox.put("1600x1200", "1600 x 1200"); // 4:3
        settingsComboBox.put("1920x1080", "1920 x 1080"); // 16:9
        settingsComboBox.put("1920x1200", "1920 x 1200"); // 8:5
        settingsComboBox.put("2560x1440", "2560 x 1440"); // 16:9
        settingsComboBox.put("2560x1600", "2560 x 1600"); // 16:10
        settingsComboBox.put("2880x1800", "2880 x 1800"); // 16:10
        settingsComboBox.put("3440x1440", "3440 x 1440"); // 21:9
        settingsComboBox.put("3840x2160", "3840 x 2160"); // 16:9
        settingsComboBox.put("4096x2160", "4096 x 2160"); // 17:9

        return settingsComboBox.createComponent(currentKey);
    }
}
