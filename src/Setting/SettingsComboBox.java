package src.Setting;

import java.util.*;

import javax.swing.JComboBox;

public class SettingsComboBox {

    private Map<String, String> linkedHashMap = new LinkedHashMap<>();

    public void put(String key, String value) {
        this.linkedHashMap.put(key, value);
    }

    private Map<String, String> getMap() {
        return Collections.unmodifiableMap(this.linkedHashMap);
    }

    public JComboBox<String> createComponent(String currentKey) {

        JComboBox<String> comboBox = new JComboBox<>();

        Map<String, String> map = this.getMap();

        String currentValue = (String) map.get(currentKey);

        // Add map values to combobox
        for (String value : map.values()) {
            comboBox.addItem(value);
            if (value == currentValue) {
                comboBox.setSelectedItem(value);
            }
        }

        // Add map to combobox
        comboBox.putClientProperty("map", map);

        return comboBox;
    }

    public static String getKey(JComboBox<String> comboBox) {

        String key = null;
        try {
            Map<String, String> map = (Map<String, String>) comboBox.getClientProperty("map");

            String selectedValue = (String) comboBox.getSelectedItem();

            // Retrieve the key based on the selected value
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals(selectedValue)) {
                    key = entry.getKey();
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return key;
    }

}
