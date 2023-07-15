import java.io.File;

public class KfxValidator {

    public static String[] requiredFiles = {

            // KeeperFX stuff
            "keeperfx.exe",
            "keeperfx_hvlog.exe",

            // SDL libs
            "SDL2.dll",
            "SDL2_mixer.dll",
            "SDL2_net.dll",

            // ?? LIBS
            "SMACKW32.DLL",
            "WSND7R.DLL",
            "MSS32.DLL",
            "dinput.dll",

            // OGG libs
            "libogg-0.dll",
            "libvorbis-0.dll",
            "libvorbisfile-3.dll",
    };

    public static String checkForMissingFile() {

        for (String filePath : KfxValidator.requiredFiles) {
            File file = new File(Main.launcherRootDir + File.separator + filePath);
            if (!file.exists() || !file.canRead()) {
                return file.getName();
            }
        }

        return null;
    }
}
