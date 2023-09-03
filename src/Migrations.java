package src;

import java.io.File;

public class Migrations {
    public static void run() {

        // Rename run args json file
        File oldRunArgsFile = new File(Main.launcherRootDir + File.separator + "imp-launcher.args.json");
        File newRunArgsFile = new File(Main.launcherRootDir + File.separator + "implauncher.run-args.json");
        if (oldRunArgsFile.exists()) {
            if (!newRunArgsFile.exists()) {
                oldRunArgsFile.renameTo(newRunArgsFile);
            } else {
                oldRunArgsFile.delete();
            }
        }

        // Rename uppercase files in /data
        for (String filePath : InstallFiles.manualInstallFiles) {
            File file = new File(Main.launcherRootDir + File.separator + filePath);
            File fileLowerCase = new File(Main.launcherRootDir + File.separator + filePath.toLowerCase());
            if (file.exists() && !fileLowerCase.exists()) {
                file.renameTo(fileLowerCase);
            }
        }

    }
}
