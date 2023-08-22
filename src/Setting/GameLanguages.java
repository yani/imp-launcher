package src.Setting;

import javax.swing.JComboBox;

public class GameLanguages extends SettingsComboBox {

    public static JComboBox<String> createComboBox(String currentKey) {
        SettingsComboBox settingsComboBox = new SettingsComboBox();
        settingsComboBox.put("ENG", "English");
        settingsComboBox.put("ITA", "Italiano");
        settingsComboBox.put("FRE", "Fran\u00E7ais");
        settingsComboBox.put("SPA", "Espa\u00F1ol");
        settingsComboBox.put("DUT", "Nederlands");
        settingsComboBox.put("GER", "Deutsch");
        settingsComboBox.put("POL", "Polski");
        settingsComboBox.put("SWE", "Svenska");
        settingsComboBox.put("JAP", "\u65E5\u672C\u8A9E");
        settingsComboBox.put("RUS", "\u0420\u0443\u0441\u0441\u043A\u0438\u0439");
        settingsComboBox.put("KOR", "\uD55C\uAD6D\uC5B4");
        settingsComboBox.put("CHI", "\u7B80\u4F53\u4E2D\u6587");
        settingsComboBox.put("CHT", "\u7E41\u9AD4\u4E2D\u6587");
        settingsComboBox.put("CZE", "\u010Ce\u0161ka");
        settingsComboBox.put("LAT", "Lat\u012Bna");
        return settingsComboBox.createComponent(currentKey);
    }
}
