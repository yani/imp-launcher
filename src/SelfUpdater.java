package src;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import org.json.simple.*;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

public class SelfUpdater {

    public static String endPointHost = "keeperfx.net";

    public static int workshopID = 410;

    JFrame mainWindow;

    Thread updateThread;

    String newVersion;
    String downloadURL;

    JDialog dialog = new JDialog();

    JButton updateButton = GuiUtil.createDefaultButton("Update");
    JButton closeButton = GuiUtil.createDefaultButton("Close");
    JButton cancelButton = GuiUtil.createDefaultButton("Cancel");
    JProgressBar progressBar = new JProgressBar(0, 100);
    JLabel statusLabel = new JLabel("<html>Status: Ready</html>");

    public SelfUpdater(JFrame mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void checkForUpdates() {

        System.out.println("Current ImpLauncher version: " + Main.impLauncherVersion);

        URL classJarLocation = this.mainWindow.getClass().getProtectionDomain().getCodeSource().getLocation();

        System.out.println(classJarLocation);

        if (classJarLocation != null) {

            String classJarPath = classJarLocation.toString();
            System.out.println(classJarPath);

            String classJarExtension = classJarPath.substring(classJarPath.lastIndexOf(".") + 1).toLowerCase();

            if (classJarExtension.equals("jar")) {
                this.updateCheck();
            } else {
                System.out.println("Can't update ImpLauncher if we are not executing from a .jar");
            }
        }
    }

    public void updateCheck() {

        JSONObject json = HttpUtil
                .getJsonObjectFromRestAPI(
                        URI.create("https://" + SelfUpdater.endPointHost + "/api/v1/workshop/item/"
                                + SelfUpdater.workshopID));

        if (json == null) {
            return;
        }

        JSONObject workshopItem = (JSONObject) json.get("workshop_item");
        JSONArray files = (JSONArray) workshopItem.get("files");
        JSONObject latestFile = (JSONObject) files.get(0);

        String latestImplauncherFilename = (String) latestFile.get("filename");

        Pattern pattern = Pattern.compile(".*?([0-9]*\\.[0-9]*\\.[0-9]*).*");

        String newVersion = null;

        Matcher m = pattern.matcher(latestImplauncherFilename);
        if (m.matches()) {
            // newVersion = m.group(1) + "-unique";
            newVersion = m.group(1);
        }

        if (newVersion == null) {
            System.out.println("Unable to determine semver for online ImpLauncher version");
            return;
        }

        System.out.println("Latest ImpLauncher version on KFX workshop: " + newVersion);

        if (newVersion.equals(Main.impLauncherVersion) == false) {
            System.out.println("ImpLauncher version does not match. Asking user to update..");
            this.showSelfUpdaterUI(newVersion, (String) latestFile.get("url"));
        }
    }

    public void showSelfUpdaterUI(String newVersion, String downloadURL) {

        this.newVersion = newVersion;
        this.downloadURL = downloadURL;

        // Create update dialog
        this.dialog.setTitle("ImpLauncher updater");
        this.dialog.getContentPane().setBackground(new Color(50, 50, 50));
        this.dialog.setSize(400, 260);
        this.dialog.setResizable(false);
        this.dialog.setModal(true);
        this.dialog.setLocationRelativeTo(this.mainWindow);
        this.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (updateThread != null) {
                    updateThread.interrupt();
                    updateThread = null;
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////

        // Create a main panel for the index content
        RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS, 5);
        rl.setAlignment(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel(rl);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.dialog.add(panel);

        ////////////////////////////////////////////////////////////////////////

        // Info text at top
        JLabel infoLabel = new JLabel(
                "<html>" +
                        "New ImpLauncher version available!<br /><br />" +
                        "Current version: <span style='color:white'>" + Main.impLauncherVersion + "</span><br />" +
                        "New version: <span style='color:lime'>" + newVersion + "</span>" +
                        "</html>");
        infoLabel.setBorder(new EmptyBorder(5, 5, 15, 5));
        // infoLabel.setPreferredSize(new Dimension(600, 100));
        infoLabel.setPreferredSize(new Dimension(380, 72));
        infoLabel.repaint();
        panel.add(infoLabel);

        ////////////////////////////////////////////////////////////////////////

        progressBar.setUI((ProgressBarUI) BasicProgressBarUI.createUI(progressBar));
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(379, 35));
        progressBar.revalidate();
        progressBar.repaint();
        panel.add(progressBar);

        // Status label
        statusLabel.setBorder(new EmptyBorder(12, 5, 15, 5));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setPreferredSize(new Dimension(380, 24));
        panel.add(statusLabel);

        ////////////////////////////////////////////////////////////////////////

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        this.dialog.add(bottomPanel, BorderLayout.PAGE_END);

        // "Close" button
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.setBackground(new Color(45, 45, 45));
        closeButton.addActionListener(e -> this.dialog.dispose());
        closeButton.setEnabled(true);
        cancelButton.setVisible(true);
        bottomPanel.add(closeButton, BorderLayout.PAGE_END);

        // "Cancel" button
        cancelButton.setPreferredSize(new Dimension(150, 50));
        cancelButton.setBackground(new Color(45, 45, 45));
        cancelButton.addActionListener(e -> {
            if (this.updateThread != null) {
                this.updateThread.interrupt();
                this.updateThread = null;
            }
        });
        cancelButton.setVisible(false);
        bottomPanel.add(cancelButton, BorderLayout.PAGE_END);

        // "Update" button
        updateButton.setPreferredSize(new Dimension(150, 50));
        updateButton.addActionListener(e -> {
            System.out.println("ImpLauncher download URL: " + this.downloadURL);
            this.updateThread = this.createUpdateThread();
            this.updateThread.start();
        });
        updateButton.setEnabled(true);
        bottomPanel.add(updateButton, BorderLayout.PAGE_END);

        ////////////////////////////////////////////////////////////////////////

        // dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Show dialog
        this.dialog.revalidate();
        this.dialog.repaint();
        this.dialog.setVisible(true);

    }

    private void updateStatusLabel(String text) {
        statusLabel.setText("<html>Status: " + text + "</html>");
    }

    private Thread createUpdateThread() {

        return new Thread(() -> {

            Path tempFilePath;

            // Update buttons
            updateButton.setText("Updating...");
            updateButton.setEnabled(false);
            closeButton.setVisible(false);
            cancelButton.setVisible(true);

            // Show percentage on progress bar
            progressBar.setStringPainted(true);
            progressBar.setEnabled(true);

            // Start update
            try {

                // Extract updater tool
                this.updateStatusLabel("Extracting updater tool...");
                try {
                    InputStream inputStream = Main.class.getResourceAsStream("/self-updater/implauncher-updater.jar");
                    OutputStream outputStream = new FileOutputStream(
                            Main.launcherRootDir + File.separator + "implauncher-updater.jar");
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (Exception ex) {
                    System.out.println("Failed to extract updater tool!");
                    this.updateStatusLabel("Failed to extract updater tool!");
                    return;
                }

                // Start download
                this.updateStatusLabel("Checking for download...");

                // Create HttpClient
                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build();

                // Create HttpRequest Builder
                HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(this.downloadURL));

                // Create the HEAD request
                HttpRequest headRequest = httpRequestBuilder
                        .method("HEAD", HttpRequest.BodyPublishers.noBody())
                        .build();

                // Send the HEAD request
                HttpResponse<Void> headResponse = client.send(headRequest, HttpResponse.BodyHandlers.discarding());

                // Retrieve the headers from the HEAD response
                HttpHeaders headers = headResponse.headers();

                // Check if the Content-Length header is present
                if (!headers.firstValue("Content-Length").isPresent()) {
                    String errorMessage = "No content length found";
                    System.out.println(errorMessage);
                    throw new Exception(errorMessage);
                }

                // Get the file size from the Content-Length header
                String contentLengthHeader = headers.firstValue("Content-Length").get();
                long fileSize = Long.parseLong(contentLengthHeader);
                long fileSizeInKB = fileSize / 1000;

                // Show the user we are starting
                // progressBar.setValue(1);
                System.out.println("Size: " + fileSizeInKB + "MB");
                this.updateStatusLabel("Starting download... (" + fileSizeInKB + "KB)");

                // Create a temporary file to save the downloaded content
                String fileName = downloadURL.substring(downloadURL.lastIndexOf("/") + 1);
                System.out.println("Download filename: " + fileName);
                tempFilePath = Files.createTempFile("tmp-", fileName);
                System.out.println("Temp file: " + tempFilePath);

                // Create the GET request
                HttpRequest downloadRequest = httpRequestBuilder
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                // Send request
                HttpResponse<InputStream> downloadResponse = client.send(downloadRequest,
                        HttpResponse.BodyHandlers.ofInputStream());

                // Check response code
                if (downloadResponse.statusCode() != HttpURLConnection.HTTP_OK) {
                    String errorMessage = "Wrong HTTP status code: " + downloadResponse.statusCode();
                    System.out.println(errorMessage);
                    throw new Exception(errorMessage);
                }

                // Handle download stream
                InputStream inputStream = downloadResponse.body();
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        FileOutputStream fileOutputStream = new FileOutputStream(tempFilePath.toString())) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    float totalBytesRead = 0;
                    int totalKBDownloaded = 0;
                    while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {

                        // Write download buffer to file
                        fileOutputStream.write(buffer, 0, bytesRead);

                        // Handle status and percentage
                        totalBytesRead += bytesRead;
                        totalKBDownloaded = ((int) totalBytesRead) / 1000;
                        this.updateStatusLabel(
                                "Downloading... (" + totalKBDownloaded + "KB / " + fileSizeInKB + "KB)");
                        progressBar.setValue(
                                (int) ((totalBytesRead / fileSize) * 100));
                        progressBar.revalidate();
                        progressBar.repaint();
                    }
                }

                // Download complete!
                System.out.println("File downloaded successfully! Moving to implauncher dir...");
                this.updateStatusLabel("Download complete! Moving to implauncher dir...");
                progressBar.setValue(0);

                // Move temp file to root app dir
                File tempNewAppJar = new File(tempFilePath.toString());
                File newAppJar = new File(Main.launcherRootDir + File.separator + "implauncher-new.jar");
                tempNewAppJar.renameTo(newAppJar);
                newAppJar.setExecutable(true);

                // Show complete!
                System.out.println("Complete!");
                this.updateStatusLabel("Complete!");

            } catch (InterruptedException ex) {

                this.updateStatusLabel("Canceled");

                // Reset buttons
                updateButton.setText("Update");
                updateButton.setEnabled(true);
                cancelButton.setVisible(false);
                closeButton.setVisible(true);

                // Cancel update process
                return;

            } catch (Exception ex) {
                ex.printStackTrace();

                this.updateStatusLabel("Update failed...");

                // Reset buttons
                updateButton.setText("Update");
                updateButton.setEnabled(true);
                cancelButton.setVisible(false);
                closeButton.setVisible(true);

                // Cancel update process
                return;
            }

            // Clear temporary downloaded file
            if (tempFilePath != null) {
                File tempFile = new File(tempFilePath.toString());
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }

            // Show complete notice and close dialog
            JOptionPane.showMessageDialog(this.mainWindow,
                    "ImpLauncher needs to restart to complete the update process."
                            + "\n\nClick OK to restart the application.",
                    "ImpLauncher updater",
                    JOptionPane.INFORMATION_MESSAGE);

            // Get java bin path
            final String javaBin = System.getProperty("java.home") + File.separator +
                    "bin" + File.separator + "java";

            // Create .jar start command
            final ArrayList<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.add("-jar");
            command.add(Main.launcherRootDir + File.separator + "implauncher-updater.jar");
            command.add(Main.launcherRootDir); // Pass the app root-dir so this logic does not have to be copied

            // Start updater
            try {
                final ProcessBuilder builder = new ProcessBuilder(command);
                builder.start();
            } catch (IOException ex) {
            }

            // Stop self
            this.dialog.dispose();
            System.exit(0);
        });
    }
}
