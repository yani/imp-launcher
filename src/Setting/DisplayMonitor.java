package src.Setting;

import java.awt.*;

import javax.swing.JComboBox;

public class DisplayMonitor extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();

        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        for (int i = 0; i < devices.length; i++) {
            GraphicsDevice device = devices[i];

            int newId = i + 1;
            settingsComboBox.put(String.valueOf(newId), device.toString());
        }

        return settingsComboBox.createComponent(currentKey);
    }
}
