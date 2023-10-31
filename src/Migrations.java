package src;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.*;

import javax.swing.JOptionPane;

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

        // Check for the existence of old and re-bundled KFX assets.
        // The included KFX assets were re-bundled to lower the amount of zip files.
        // It's hard to remove the older files when the new files are rolled out,
        // so this migration does that for the user.
        Map<String, String> reBundledAssets = Migrations.getReBundledAssets();
        Map<String, String> oldAssets = Migrations.getOldAssets();
        try {

            File dir = new File(Main.launcherRootDir + "/fxdata");

            // Check if one of each filelist is found
            File firstFile = new File(
                    dir.getAbsolutePath() + "/" + reBundledAssets.entrySet().iterator().next().getKey());
            File oldFile = new File(dir.getAbsolutePath() + "/druid.zip");
            if (firstFile.exists() && oldFile.exists()) {

                // Check if if the new files are present so we can delete the old files
                boolean shouldDelete = true;
                for (String filename : reBundledAssets.keySet()) {

                    File newFile = new File(dir.getAbsolutePath() + "/" + filename);
                    if (!newFile.exists()) {
                        System.out.println("Migration: " + filename + " does not exist");
                        shouldDelete = false;
                        break;
                    }

                    String crc32Value = Migrations.getCRC32value(newFile);
                    String requiredCrc32Value = reBundledAssets.get(filename);
                    if (!crc32Value.equals(requiredCrc32Value)) {
                        shouldDelete = false;
                        break;
                    }
                }

                if (shouldDelete) {

                    String fileListString = "";
                    ArrayList<String> fileNamesToDelete = new ArrayList<String>();

                    for (String filename : oldAssets.keySet()) {

                        File newFile = new File(dir.getAbsolutePath() + "/" + filename);
                        if (!newFile.exists()) {
                            continue;
                        }

                        String crc32Value = Migrations.getCRC32value(newFile);
                        String requiredCrc32Value = oldAssets.get(filename);
                        if (!crc32Value.equals(requiredCrc32Value) && !crc32Value.equals("3960707929")) {
                            continue;
                        }

                        fileNamesToDelete.add(filename);
                        fileListString += "- fxdata/" + filename + "\n";
                    }

                    if (fileNamesToDelete.size() > 0) {
                        System.out.println("Asking user if we should remove files during the asset-rebundle migration");

                        int deleteQuestion = JOptionPane.showConfirmDialog(null,
                                "ImpLauncher has found original KFX assets that have been re-bundled.\nThese files can safely be deleted:"
                                        + "\n\n" + fileListString
                                        + "\n Do you wish to automatically delete these files?",
                                "ImpLauncher - KeeperFX", JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE);
                        if (deleteQuestion == JOptionPane.YES_OPTION) {
                            for (String filename : fileNamesToDelete) {
                                File file = new File(dir.getAbsolutePath() + "/" + filename);
                                if (file.exists()) {
                                    file.delete();
                                }
                            }

                            JOptionPane.showMessageDialog(null, "Successfully deleted old assets!",
                                    "ImpLauncher - KeeperFX", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(null, "Something went wrong while handling the asset re-bundle migration",
                    "ImpLauncher - Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static Map<String, String> getReBundledAssets() {
        Map<String, String> reBundledAssets = new HashMap<String, String>();

        reBundledAssets.put("creatures.zip", "2109114875");
        reBundledAssets.put("trapsdoors.zip", "1081330644");
        reBundledAssets.put("decorative_objects.zip", "2818188277");
        reBundledAssets.put("natural_features.zip", "4122517959");

        return reBundledAssets;
    }

    public static Map<String, String> getOldAssets() {
        Map<String, String> oldAssets = new HashMap<String, String>();
        oldAssets.put("goldenarmor.zip", "984811839");
        oldAssets.put("whiteflag.zip", "3473430213");
        oldAssets.put("gemtraps.zip", "3458436481");
        oldAssets.put("waterplants.zip", "2969497260");
        oldAssets.put("knightstatue.zip", "882673639");
        oldAssets.put("mushrooms.zip", "1499414880");
        // oldAssets.put("druid.zip", "2258624893");
        oldAssets.put("torches.zip", "2900881663");
        oldAssets.put("banner.zip", "2766031992");
        oldAssets.put("lanternpost.zip", "3834956157");
        oldAssets.put("fern.zip", "715435298");
        oldAssets.put("windbanner.zip", "2356471797");
        // oldAssets.put("time_mage.zip", "1770950666");
        oldAssets.put("boom_barrel.zip", "1782908367");
        oldAssets.put("gimly.zip", "2299161908");
        oldAssets.put("trapcolors.zip", "2511003861");
        return oldAssets;
    }

    public static String getCRC32value(File file) throws IOException {

        InputStream inputStream = new FileInputStream(file);
        CheckedInputStream checkedInputStream = new CheckedInputStream(inputStream, new CRC32());
        byte[] buffer = new byte[512];
        while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {
        }

        long crc32Value = checkedInputStream.getChecksum().getValue();

        checkedInputStream.close();
        inputStream.close();

        return Long.toString(crc32Value);

    }
}
