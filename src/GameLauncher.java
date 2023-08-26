package src;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public class GameLauncher {

    // Reference to the main window
    Main mainWindow;

    // Run Options
    RunOptions runOptions;

    // System runtime for executing processes
    Runtime runTime = Runtime.getRuntime();

    // KeeperFX Executable bin
    // String kfxBin = ;

    // // LINUX command
    // public static String[] linuxPrefixCommands = { "wine" };

    public GameLauncher(Main mainWindow, RunOptions runOptions) {
        this.mainWindow = mainWindow;
        this.runOptions = runOptions;
    };

    public void startGame() {
        this.startGame(null);
    }

    public void startGame(ArrayList<String> extraArguments) {

        this.mainWindow.setPlayButtonAsPlaying();

        int exitCode = -1;
        String gameConsoleOutput = "";

        try {

            // Create arguments array list
            ArrayList<String> arguments = new ArrayList<>();

            // Check if we are NOT on Windows
            if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {

                // Check if Wine is installed
                if (!GameLauncher.isWineAvailable()) {
                    // TODO: make messagebox open a website?
                    // Show message
                    JOptionPane.showMessageDialog(this.mainWindow,
                            "Wine is required to run KeeperFX on non-Windows systems.", "ImpLauncher",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Start with Wine
                arguments.add("wine");
            }

            // Add KeeperFX bin
            arguments.add("keeperfx.exe");

            // Add custom run options
            for (String option : this.runOptions.getAllKeys()) {
                arguments.add("-" + option);
                Object value = this.runOptions.getOption(option);
                if (value instanceof String || value instanceof Integer) {
                    arguments.add((String) value);
                }
            }

            // Add extra arguments
            if (extraArguments != null) {
                arguments.addAll(extraArguments);
            }

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(arguments.toArray(String[]::new));
            processBuilder.directory(new File(Main.launcherRootDir)); // Set working directory
            processBuilder.redirectErrorStream(true);

            // Create and start the process
            Process process = processBuilder.start();
            // if (process.isAlive()) {
            // this.mainWindow.setPlayButtonAsPlaying();
            // }

            // Open output reader and redirect output to java output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                gameConsoleOutput += line + "\n";
            }

            // Wait for process to finish
            process.waitFor();
            System.out.println("keeperfx process ended");

            // Close output reader
            reader.close();

            // Get exit code
            exitCode = process.exitValue();
            System.out.println("keeperfx exitcode: " + exitCode);

            // Check for successful execution
            if (exitCode == 0) {
                this.mainWindow.handleLogFileButton();
                this.mainWindow.enablePlayButton();
                return;
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        // Show/hide log file button on main window
        // This is mostly just for the first time a user starts the game
        this.mainWindow.handleLogFileButton();

        // Enable play button again
        this.mainWindow.enablePlayButton();

        // Make sure we have the exit code
        gameConsoleOutput += "KeeperFX Exitcode: " + exitCode;

        // Show a message that our game crashed
        new CrashReport(this.mainWindow, gameConsoleOutput);

        return;
    }

    public static boolean isWineAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("wine --version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
