package src;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RunOptions {

    public static String gameLauncherArgsFilename = "imp-launcher.args.json";

    public static File optionsFile = new File(
            Main.launcherRootDir + File.separator + RunOptions.gameLauncherArgsFilename);

    private JSONObject dataObj;

    public RunOptions() {

        if (!RunOptions.optionsFile.exists()) {
            try (FileWriter writer = new FileWriter(RunOptions.optionsFile.getPath().toString())) {
                writer.write("{}");
                writer.close();
            } catch (IOException ex) {
            }
        }

        if (!RunOptions.optionsFile.exists()) {
            System.out.println("Failed to write " + RunOptions.gameLauncherArgsFilename);
            return;
        }

        try {
            String fileContents = new String(Files.readAllBytes(RunOptions.optionsFile.toPath()));
            JSONParser parse = new JSONParser();
            this.dataObj = (JSONObject) parse.parse(fileContents);
        } catch (Exception ex) {
        }

    }

    public void toggleOption(String argName, boolean isEnabled) {
        if (isEnabled) {
            Main.runOptions.setOption(argName, true);
        } else {
            Main.runOptions.removeOption(argName);
        }
    }

    public void setOption(String argName, Object var) {
        this.dataObj.put(argName, var);
    }

    public Object getOption(String argName) {
        return this.dataObj.get(argName);
    }

    public void removeOption(String argName) {
        if (this.dataObj.containsKey(argName)) {
            this.dataObj.remove(argName);
        }
    }

    public Set<String> getAllKeys() {
        return this.dataObj.keySet();
    }

    public void saveOptionsToFile() {
        try (FileWriter writer = new FileWriter(RunOptions.optionsFile.getPath().toString())) {
            this.dataObj.writeJSONString(writer);
            writer.close();
        } catch (IOException ex) {
        }

    }
}
