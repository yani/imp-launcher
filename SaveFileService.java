import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SaveFileService {

    public static File saveDir = new File(Main.launcherRootDir + File.separator + "save");

    public SaveFileService() {

    }

    public static ArrayList<SaveFile> getSaveFiles() {

        ArrayList<SaveFile> saveFiles = new ArrayList<SaveFile>();

        File[] directoryListing = SaveFileService.saveDir.listFiles();
        Arrays.sort(directoryListing);
        if (directoryListing != null) {
            for (File file : directoryListing) {
                String fileName = file.getName();
                if (fileName.startsWith("fx1g") && fileName.endsWith(".sav")) {
                    saveFiles.add(new SaveFile(file));
                }
            }
        }

        return saveFiles;
    }

}
