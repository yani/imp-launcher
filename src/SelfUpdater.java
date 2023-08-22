package src;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    File theJarFile;

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

                String classJarFilename = classJarPath.substring(classJarPath.lastIndexOf("/") + 1);
                this.theJarFile = new File(Main.launcherRootDir + File.separator + classJarFilename);

                new Thread(() -> this.updateCheckThread()).start();

            } else {
                System.out.println("Can't update ImpLauncher if we are not executing from a .jar");
            }

        }
    }

    public void updateCheckThread() {

        JSONObject json = HttpUtil
                .getJsonObjectFromRestAPI(
                        URI.create("https://keeperfx.net/api/v1/workshop/item/" + SelfUpdater.workshopID));

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
                System.out.println("File downloaded successfully!");
                this.updateStatusLabel("Download complete! Replacing original .jar file...");
                progressBar.setValue(0);

                // Replace .jar file
                this.theJarFile.delete();
                File tempFile = new File(tempFilePath.toString());
                tempFile.renameTo(this.theJarFile);
                this.theJarFile.setExecutable(true);

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
                    "Success!\n" +
                            "ImpLauncher has been updated to: " + this.newVersion
                            + "\n\nClick OK to restart the application.",
                    "ImpLauncher update completed!",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dialog.dispose();

            // Restart ImpLauncher
            this.restartApplication();
        });
    }

    public void restartApplication() {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(this.theJarFile.getPath());

        try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ex) {
        }
        System.exit(0);
    }

}
