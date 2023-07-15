import java.io.File;
import java.io.RandomAccessFile;

public class SaveFile {
    File file;
    String fileName;
    String saveName = "";
    String campaignName = "";

    public SaveFile(File file) {

        if (!file.exists() || !file.canRead()) {
            // TODO: throw exception
            return;
        }

        this.file = file;
        this.fileName = file.getName();

        try {

            int byteBuff;

            // Open file
            RandomAccessFile raf = new RandomAccessFile(file.getPath(), "r");

            // Read a defining offset
            raf.seek(0x4);
            byteBuff = raf.read();

            // Make sure this file is a correct save file
            if (byteBuff != 73) { // 73 = (char) I
                return;
            }

            // Read save name
            long pos = 0x12; // starting position for save name filename
            while (true) {
                raf.seek(pos);
                int buff = raf.read();
                if (buff == 0) { // null byte = end of save name
                    break;
                }
                this.saveName += (char) buff;
                pos++;
            }

            // Read campaign/ruleset
            pos = 0x25; // starting position for save name filename
            while (true) {
                raf.seek(pos);
                int buff = raf.read();
                if (buff == 0) { // null byte = end of save name
                    break;
                }
                this.campaignName += (char) buff;
                pos++;
            }

            System.out.println(file.getName() + ": " + this.saveName + " (" + this.campaignName + ")");

        } catch (Exception ex) {
        }

    }

    @Override
    public String toString() {
        return this.fileName + ": " + this.saveName + " (" + this.campaignName + ")";
    }
}
