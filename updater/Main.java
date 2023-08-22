package updater;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {

        // Make sure the root directory is given as an argument
        if (args[0] == null) {
            JOptionPane.showMessageDialog(null, "You can not run the updater tool manually.", "ImpLauncher Updater",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Make sure root directory exists
        File rootDir = new File(args[0]);
        if (!rootDir.exists()) {
            JOptionPane.showMessageDialog(null,
                    "Something went wrong with the update..." +
                            "\nA wrong root directory has been passed.",
                    "ImpLauncher Updater",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {

            // Sleep a little bit to make sure the main application has exited
            Thread.sleep(1000);

            // Variables
            File originalJar = new File(rootDir + File.separator + "implauncher.jar");
            File newJar = new File(rootDir + File.separator + "implauncher-new.jar");

            // Make sure the new jar file exists
            if (!newJar.exists()) {
                JOptionPane.showMessageDialog(null,
                        "Something went wrong with the update..." +
                                "\n'implauncher-new.jar' does not exist.",
                        "ImpLauncher Updater",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // Remove the original jar
            if (originalJar.exists()) {
                if (!originalJar.delete()) {
                    JOptionPane.showMessageDialog(null,
                            "Something went wrong with the update..." +
                                    "\nFailed to delete the original application file.",
                            "ImpLauncher Updater",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }

            // Rename new jar to original jar
            if (!newJar.renameTo(originalJar)) {
                JOptionPane.showMessageDialog(null,
                        "Something went wrong with the update..." +
                                "\nFailed to rename the new file as the original file.",
                        "ImpLauncher Updater",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            // Get java bin path
            final String javaBin = System.getProperty("java.home") + File.separator +
                    "bin" + File.separator + "java";

            // Create .jar start command
            final ArrayList<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.add("-jar");
            command.add(originalJar.getAbsolutePath());

            // Start main application
            try {
                final ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            } catch (IOException ex) {
            }

            // Stop self
            System.exit(0);

        } catch (Exception ex) {

            // Something went wrong
            JOptionPane.showMessageDialog(null, "Something went wrong."
                    + "\nYou will have to manually update ImpLauncher."
                    + "\nYou can do so by deleting the file 'implauncher.jar' and renaming 'implauncher-new.jar' to 'implauncher.jar'.",
                    "ImpLauncher Updater", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }
}
