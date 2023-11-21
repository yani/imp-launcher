package src;

import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.json.simple.*;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

public class GameUpdater {

    private boolean shouldCancel = false;

    JFrame mainWindow;
    JDialog dialog = new JDialog();

    KfxReleaseType currentReleaseType;
    KfxReleaseType newReleaseType;
    String currentSemver;
    String newSemver;
    String downloadURL;

    Thread updateThread;
    Thread archiveThread;
    public static boolean cfgHasUpdated = false;

    JButton updateButton = GuiUtil.createDefaultButton("Update");
    JButton closeButton = GuiUtil.createDefaultButton("Close");
    JButton cancelButton = GuiUtil.createDefaultButton("Cancel");
    JProgressBar progressBar = new JProgressBar(0, 100);
    JLabel statusLabel = new JLabel("<html>Status: Ready</html>");

    public GameUpdater(JFrame mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void customVersionDownload(String currentSemVerString, KfxReleaseType wantedReleaseType) {
        this.currentSemver = currentSemVerString;
        if (wantedReleaseType == KfxReleaseType.STABLE) {
            this.checkStable(false);
        } else if (wantedReleaseType == KfxReleaseType.ALPHA) {
            this.checkAlpha(false);
        }
    }

    public void checkForUpdates() {
        this.checkForUpdates(Main.kfxReleaseType);
    }

    public void checkForUpdates(KfxReleaseType wantedKfxReleaseType) {

        Pattern pattern1 = Pattern.compile(".*?([0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*).*");

        Matcher matcher1 = pattern1.matcher(Main.kfxVersion);
        if (matcher1.matches()) {
            // this.currentSemver = m.group(1) + "-unique";
            this.currentSemver = matcher1.group(1);
        }

        if (this.currentSemver == null) {

            Pattern pattern2 = Pattern.compile(".*?([0-9]*\\.[0-9]*\\.[0-9]*).*");
            Matcher matcher2 = pattern2.matcher(Main.kfxVersion);
            if (matcher2.matches()) {
                // this.currentSemver = m.group(1) + "-unique";
                this.currentSemver = matcher2.group(1);
            }
        }

        if (this.currentSemver == null) {
            System.out.println("Unable to determine semver from keeperfx.exe fileversion info");
            return;
        }

        System.out.println("Current KFX version: " + this.currentSemver);

        if (wantedKfxReleaseType == KfxReleaseType.STABLE) {
            this.checkStable();
        }

        if (wantedKfxReleaseType == KfxReleaseType.ALPHA) {
            this.checkAlpha();
        }

    }

    public void checkStable() {
        this.checkStable(true);
    }

    public void checkStable(Boolean showSaveGameNotice) {

        System.out.println("Checking for Stable update..");

        JSONObject json = HttpUtil.getJsonObjectFromRestAPI(URI.create("https://keeperfx.net/api/v1/stable/latest"));
        JSONObject releaseObj = (JSONObject) json.get("release");

        String releaseName = (String) releaseObj.get("name");

        // Pattern pattern = Pattern.compile(".*?([0-9]*\\.[0-9]*\\.[0-9]*).*?Build
        // ([0-9]*)");
        Pattern pattern = Pattern.compile(".*?([0-9]*\\.[0-9]*\\.[0-9]*).*?");
        Matcher m = pattern.matcher(releaseName);
        if (!m.matches()) {
            System.out.println("Unable to grab latest KeeperFX.net stable version. Unable to get version from 'name'.");
            return;
        }

        // String latestStableSemver = m.group(1) + "." + m.group(2);
        String latestStableSemver = m.group(1);
        System.out.println("Latest KeeperFX.net stable version: " + latestStableSemver);

        // New version!
        if (!this.currentSemver.equals(latestStableSemver)) {
            System.out.println("Versions do not match. Asking users to download");
            this.showUpdaterUI(
                    KfxReleaseType.STABLE,
                    this.currentSemver,
                    latestStableSemver,
                    (String) releaseObj.get("download_url"),
                    showSaveGameNotice);
        }
    }

    public void checkAlpha() {
        this.checkAlpha(true);
    }

    public void checkAlpha(Boolean showSaveGameNotice) {

        System.out.println("Checking for Alpha update..");

        JSONObject json = HttpUtil.getJsonObjectFromRestAPI(URI.create("https://keeperfx.net/api/v1/alpha/latest"));
        JSONObject releaseObj = (JSONObject) json.get("alpha_build");

        String alphaBuildName = (String) releaseObj.get("name");

        Pattern pattern = Pattern.compile(".*?([0-9]*_[0-9]*_[0-9]*_[0-9]*).*");
        Matcher m = pattern.matcher(alphaBuildName);
        if (!m.matches()) {
            System.out.println("Unable to grab latest KeeperFX.net alpha version. Unable to get version from 'name'.");
            return;
        }

        String latestAlphaSemver = m.group(1);
        latestAlphaSemver = latestAlphaSemver.replace("_", ".");
        System.out.println("Latest KeeperFX.net alpha version: " + latestAlphaSemver);

        // New version!
        if (!this.currentSemver.equals(latestAlphaSemver)) {
            System.out.println("Versions do not match. Asking users to download");
            this.showUpdaterUI(
                    KfxReleaseType.ALPHA,
                    this.currentSemver,
                    latestAlphaSemver,
                    (String) releaseObj.get("download_url"),
                    showSaveGameNotice);
        }
    }

    public void showUpdaterUI(KfxReleaseType newReleaseType, String currentSemver, String newSemver,
            String downloadURL, Boolean showSaveGameNotice) {

        this.currentReleaseType = Main.kfxReleaseType;
        this.newReleaseType = newReleaseType;
        this.currentSemver = currentSemver;
        this.newSemver = newSemver;
        this.downloadURL = downloadURL;

        // Create update dialog
        this.dialog.setTitle("KeeperFX updater");
        this.dialog.getContentPane().setBackground(new Color(50, 50, 50));
        this.dialog.setSize(400, 260);
        this.dialog.setResizable(false);
        this.dialog.setModal(true);
        this.dialog.setLocationRelativeTo(this.mainWindow);
        this.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                shouldCancel = true;
                if (updateThread != null) {
                    updateThread.interrupt();
                    updateThread = null;
                }
                if (archiveThread != null) {
                    archiveThread.interrupt();
                    archiveThread = null;
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

        String currentVersionString = currentSemver;
        if (currentReleaseType == KfxReleaseType.ALPHA) {
            currentVersionString += " (Alpha)";
        }
        String newVersionString = newSemver;
        if (newReleaseType == KfxReleaseType.ALPHA) {
            newVersionString += " (Alpha)";
        }

        // Info text at top
        JLabel infoLabel = new JLabel(
                "<html>" +
                        "New KeeperFX version available!<br /><br />" +
                        "Current version: <span style='color:white'>" + currentVersionString + "</span><br />" +
                        "New version: <span style='color:lime'>" + newVersionString + "</span>" +
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
            shouldCancel = true;
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

            // Vars for checking if file is locked
            boolean keeperFxFileLocked = false;
            File keeperFxFile = new File(Main.launcherRootDir + File.separator + "keeperfx.exe");
            FileChannel channel = null;
            FileLock lock = null;
            RandomAccessFile randAccFile = null;

            // The following trick works for Windows and checks if the file is locked
            if (keeperFxFile.exists()) {
                if (!keeperFxFile.renameTo(keeperFxFile)) {
                    keeperFxFileLocked = true;
                } else {

                    // Get file channel
                    try {
                        randAccFile = new RandomAccessFile(keeperFxFile, "rw");
                        channel = randAccFile.getChannel();
                    } catch (FileNotFoundException ex) {
                        return;
                    }

                    // Try to get lock on file
                    try {
                        lock = channel.tryLock();
                    } catch (Exception ex) {
                        keeperFxFileLocked = true;
                    }

                    // Release lock and close streams
                    try {
                        if (lock != null) {
                            lock.release();
                        }
                        if (channel != null) {
                            channel.close();
                        }
                        if (randAccFile != null) {
                            randAccFile.close();
                        }
                    } catch (Exception ex) {
                        return;
                    }
                }
            }

            // Show message and do not update when 'keeperfx.exe' is locked.
            if (keeperFxFileLocked) {
                JOptionPane.showMessageDialog(this.mainWindow,
                        "The 'keeperfx.exe' executable seems to be locked.\nThis mostly means that your game is running.\nPlease exit your game and try again.",
                        "Updater failure",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            // Show notice that save files might be lost
            if (showSaveGameNotice) {

                // Ask if user wants to backup their save files
                String[] options = { "Yes", "No", "Cancel" };
                int selectedOption = JOptionPane.showOptionDialog(
                        this.mainWindow,
                        "Save games might break when updating."
                                + "\n\nDo you want to backup any existing save games?",
                        "ImpLauncher - KeeperFX", // Dialog title
                        JOptionPane.DEFAULT_OPTION, // Option type (DEFAULT_OPTION for OK/Cancel)
                        JOptionPane.INFORMATION_MESSAGE, // Message type (PLAIN_MESSAGE for informational message)
                        null,
                        options,
                        options[0] // Default option (Stable)
                );

                // Check the selected option and take action accordingly
                if (selectedOption == JOptionPane.CLOSED_OPTION || options[selectedOption].equals("Cancel")) {
                    this.dialog.dispose();
                    return;
                }

                System.out.println("Selected option: " + options[selectedOption]);

                // Check if user wants to backup their save files
                if (options[selectedOption].equals("Yes")) {

                    this.archiveThread = new Thread(() -> {

                        this.updateStatusLabel("Starting save backup process...");

                        File savesDir = new File(Main.launcherRootDir + File.separator + "save");
                        File backupDir = new File(
                                Main.launcherRootDir + File.separator + "save" + File.separator + "backup");

                        // Make sure 'save' dir exists
                        if (!savesDir.exists()) {
                            JOptionPane.showMessageDialog(this.mainWindow,
                                    "'/save' directory not found",
                                    "Updater failure",
                                    JOptionPane.ERROR_MESSAGE);
                            this.updateStatusLabel("/save' directory not found");
                            return;
                        }

                        // Check if we need to create 'saves/backup' dir
                        if (!backupDir.exists()) {
                            this.updateStatusLabel("Making saves backup dir...");
                            if (!backupDir.mkdir()) {
                                JOptionPane.showMessageDialog(this.mainWindow,
                                        "Failed creating '/save/backup' directory",
                                        "Updater failure",
                                        JOptionPane.ERROR_MESSAGE);
                                this.updateStatusLabel("Failed creating '/save/backup' directory");
                                return;
                            }
                        }

                        // Figure out save files to archive
                        this.updateStatusLabel("Figuring out what files need to be included in save backup...");

                        // Get files to be archived
                        String[] saveFileNames = { "fx1*.sav", "scr_*.dat", "settings.dat" };
                        File[] matchingFiles = GameUpdater.getMatchingFiles(savesDir, saveFileNames);

                        if (matchingFiles.length > 0) {
                            this.updateStatusLabel("Archiving save files...");

                            // Create name for archive
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                            String timestamp = dateFormat.format(new Date());
                            String backupFileName = "keeperfx-save-backup_" + timestamp;

                            // Remove backup file if it already exists
                            File backupFile = new File(Main.launcherRootDir + File.separator + "save" + File.separator
                                    + "backup" + File.separator + backupFileName);
                            if (backupFile.exists()) {
                                backupFile.delete();
                            }

                            // Archive service
                            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.SEVEN_Z);

                            // Create archive with save files
                            try {
                                File archive = archiver.create(backupFileName, backupDir, matchingFiles);
                                this.updateStatusLabel("Save files archived...");
                            } catch (Exception ex) {

                                JOptionPane.showMessageDialog(this.mainWindow,
                                        "Failed archiving the save files.",
                                        "Updater failure",
                                        JOptionPane.ERROR_MESSAGE);
                                this.updateStatusLabel("Failed archiving the save files");
                                return;
                            }
                        } else {
                            this.updateStatusLabel("Nothing to backup");
                        }

                        if (this.shouldCancel != true) {
                            // Start update thread
                            this.updateThread = this.createUpdateThread();
                            this.updateThread.start();
                        }

                    });

                    this.archiveThread.start();
                    return;
                }

            }

            // Start update thread
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
        statusLabel.setText("<html>" + text + "</html>");
    }

    private void handleKeeperFxCfgEntry(ArchiveEntry entry, String fileSubPath) {

        try {

            // Create a temp directory
            Path tempDir = Files.createTempDirectory("keeperfx-cfg");

            // Extract the .cfg file into the custom temp directory
            System.out.println("Extracting: " + fileSubPath);
            entry.extract(tempDir.toFile());

            // Load the .cfg file
            File cfgFile = new File(tempDir + File.separator + fileSubPath);
            CfgProperties cfgProperties = new CfgProperties();
            cfgProperties.load(new FileInputStream(cfgFile.getPath().toString()));

            // Loop trough all cfg properties for the updated config file
            Collections.list(cfgProperties.keys()).forEach(e -> {

                // Get and validate key
                String key = (String) e;
                if (key.length() < 1) {
                    return;
                }

                // If the current value does not match we should update the original .cfg
                String currentValue = Main.keeperFxCfg.getProperty(key);
                if (currentValue == null) {
                    System.out.println("New .cfg property found: " + key);
                    Main.keeperFxCfg.setProperty(key, cfgProperties.getProperty(key));
                    GameUpdater.cfgHasUpdated = true;
                }

            });

            // Remove temporary extracted .cfg file and dir
            cfgProperties.clear();
            cfgFile.delete();
            tempDir.toFile().delete();

            // Save new .cfg file
            if (cfgHasUpdated == true) {
                Main.keeperFxCfg.update(new File(Main.launcherRootDir + File.separator + "keeperfx.cfg"));
                cfgHasUpdated = false;
                System.out.println(".cfg file updated!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: show messagebox telling that the configuration update has failed
        }

    }

    private Thread createUpdateThread() {

        return new Thread(() -> {

            Path tempFilePath = null;

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
                long fileSizeInMB = fileSize / 1000000;

                // Show the user we are starting
                // progressBar.setValue(1);
                System.out.println("Size: " + fileSizeInMB + "MB");
                this.updateStatusLabel("Starting download... (" + fileSizeInMB + "MB)");

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
                    int totalMBDownloaded = 0;
                    while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {

                        // Write download buffer to file
                        fileOutputStream.write(buffer, 0, bytesRead);

                        // Handle status and percentage
                        totalBytesRead += bytesRead;
                        totalMBDownloaded = ((int) totalBytesRead) / 1000000;
                        this.updateStatusLabel(
                                "Downloading... (" + totalMBDownloaded + "MB / " + fileSizeInMB + "MB)");
                        progressBar.setValue(
                                (int) ((totalBytesRead / fileSize) * 100));
                        progressBar.revalidate();
                        progressBar.repaint();

                        // Check if we cancel the download
                        if (shouldCancel) {
                            throw new InterruptedException();
                        }
                    }
                }

                // Download complete!
                System.out.println("File downloaded successfully!");
                this.updateStatusLabel("Download complete! Starting extraction...");
                progressBar.setValue(0);

                // Create archiver to extract the file
                File archive = new File(tempFilePath.toString());
                Archiver archiver = ArchiverFactory.createArchiver(archive);

                // Count the number of files in the archive for the progress bar
                int totalArchiveFileCount = 0;

                // Check if this is a known archive
                if (fileName.equals("keeperfx_0_5_0b_complete.7z")) {
                    totalArchiveFileCount = 7739;
                }
                if (fileName.equals("keeperfx_1_0_0_complete.7z")) {
                    totalArchiveFileCount = 5122;
                }

                // Count files
                if (totalArchiveFileCount == 0) {
                    ArchiveStream countStream = archiver.stream(archive);
                    while (countStream.getNextEntry() != null) {
                        totalArchiveFileCount++;
                        this.updateStatusLabel("Counting files for extraction: " + totalArchiveFileCount);
                    }
                    countStream.close();
                }

                System.out.println("Total files: " + totalArchiveFileCount);

                // Create archive stream
                ArchiveStream stream = archiver.stream(archive);
                ArchiveEntry entry;
                File entryOutputFile;
                String fileSubPath;

                // Extract each file
                int currentFileNumber = 1;
                while ((entry = stream.getNextEntry()) != null) {

                    if (entry.isDirectory()) {
                        currentFileNumber++;
                        continue;
                    }

                    // Set output file path
                    fileSubPath = entry.getName();
                    entryOutputFile = new File(Main.launcherRootDir + File.separator + fileSubPath);

                    // Handle KeeperFX configuration file
                    if (fileSubPath.equals("keeperfx.cfg") || fileSubPath.equals("_keeperfx.cfg")) {

                        // If this is a fresh install, we can simply copy the file
                        if (fileSubPath.equals("keeperfx.cfg") && !entryOutputFile.exists()) {
                            this.updateStatusLabel("Extracting the KeeperFX configuration file...");
                        } else {

                            // If it's an update (not a fresh install) we'll just copy the new
                            // configuration variables into the existing configuration file
                            this.updateStatusLabel("Looking for new stuff in downloaded .cfg file");
                            handleKeeperFxCfgEntry(entry, fileSubPath);
                            currentFileNumber++;
                            continue;
                        }

                    }

                    // Let user know status
                    System.out.println("Extracting: " + fileSubPath);
                    this.updateStatusLabel("Extracting: " + fileSubPath);
                    progressBar.setValue((int) (((float) currentFileNumber / (float) totalArchiveFileCount) * 100));

                    // Remove existing file
                    if (entryOutputFile.exists()) {
                        entryOutputFile.delete();
                    }

                    // Extract current file
                    entry.extract(new File(Main.launcherRootDir));

                    currentFileNumber++;

                    // Check if we cancel the extraction
                    if (shouldCancel) {
                        throw new InterruptedException();
                    }
                }

                // Close archive stream
                stream.close();

                // Show complete!
                System.out.println("Files extracted!");
                this.updateStatusLabel("Files extracted!");

                // Update version variables of main app and show in GUI
                Main.kfxReleaseType = newReleaseType;
                Main.kfxVersion = this.newSemver;
                if (this.newReleaseType == KfxReleaseType.ALPHA) {
                    Main.kfxVersion += " Alpha";
                }

                // Hide dialog and show notice
                this.dialog.setVisible(false);
                JOptionPane.showMessageDialog(this.mainWindow,
                        "Success!\n" +
                                "Your KeeperFX has been updated to: " + this.newSemver,
                        "KeeperFX update completed!",
                        JOptionPane.INFORMATION_MESSAGE);

                // Close the dialog. We do this after hiding it and showing the above message so
                // it doesn't trigger anything before the user clicks ok.
                this.dialog.dispose();

                // Run app startup stuff again
                // This will also update the displayed version
                Main.main.appStartup();

            } catch (InterruptedException ex) {

                shouldCancel = false;
                this.updateStatusLabel("Canceled");

            } catch (Exception ex) {

                ex.printStackTrace();
                this.updateStatusLabel("Update failed...");

            }

            // Reset buttons
            updateButton.setText("Update");
            updateButton.setEnabled(true);
            cancelButton.setVisible(false);
            closeButton.setVisible(true);

            // Clear temporary downloaded file
            if (tempFilePath != null) {
                File tempFile = new File(tempFilePath.toString());
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }

        });
    }

    private static File[] getMatchingFiles(File directory, String[] fileNames) {
        List<File> matchingFilesList = new ArrayList<>();

        for (String fileName : fileNames) {
            // Create a FilenameFilter for the current wildcard pattern
            String regex = fileName.replace(".", "\\.").replace("*", ".*");
            FilenameFilter filenameFilter = (dir, name) -> name.matches(regex);

            // List files using the directory and the FilenameFilter
            File[] matchingFiles = directory.listFiles(filenameFilter);

            if (matchingFiles != null) {
                // Add matching files to the result list
                matchingFilesList.addAll(Arrays.asList(matchingFiles));
            }
        }

        // Convert the list to an array
        return matchingFilesList.toArray(new File[0]);
    }
}
