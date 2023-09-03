package src;

import java.util.HashMap;
import java.util.Map;

public class WorkshopCategory {
    private static final Map<String, String> constantMap = new HashMap<>();

    static {
        constantMap.put("Map", "Map");
        constantMap.put("MapPack", "Map Pack");
        constantMap.put("Campaign", "Campaign");
        constantMap.put("MultiplayerMap", "Multiplayer Map");
        constantMap.put("MultiplayerMapPack", "Multiplayer Map Pack");
        constantMap.put("Creature", "Creature");
        constantMap.put("Application", "Application");
        constantMap.put("Other", "Other");
    }

    public static String getConstantByString(String value) {
        return constantMap.get(value);
    }
}