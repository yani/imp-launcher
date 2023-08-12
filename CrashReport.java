import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.util.ArrayList;
import java.util.Base64;

public class CrashReport extends JDialog {

    public static String endPointURI = "https://keeperfx.net/api/v1/crash-report";

    private Main mainWindow;

    private JTextArea descriptionArea = new JTextArea();
    private JComboBox<SaveFile> saveGameBox;

    private String kfxLog = "";
    private String gameConsoleOutput = "";

    private JButton closeButton = GuiUtil.createDefaultButton("Close");
    private JButton sendButton = GuiUtil.createDefaultButton("Send");

    public CrashReport(Main mainWindow, String gameConsoleOutput) {

        // Load stuff
        this.mainWindow = mainWindow;
        this.gameConsoleOutput = gameConsoleOutput;

        // Load keeperfx.log
        // We load this before the user clicks 'Send' just as a precaution.
        File kfxLogFile = new File(Main.launcherRootDir + File.separator + "keeperfx.log");
        if (kfxLogFile.exists() && kfxLogFile.canRead()) {
            try {
                this.kfxLog = Files.readString(kfxLogFile.toPath());
            } catch (Exception ex) {
            }
        }

        // Setup this window
        this.setTitle("KeeperFX crash report");
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(600, 550);
        this.setResizable(false);
        this.setModal(true);
        this.setLocationRelativeTo(this.mainWindow);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.setPreferredSize(new Dimension(600, 70));
        this.add(titlePanel, BorderLayout.PAGE_START);

        // Crash icon (troll)
        try {
            Image crashImage = ImageIO.read(this.getClass().getResource("implauncher-data/creatr_portrt_troll.png"));
            ImageIcon crashIcon = new ImageIcon(crashImage);
            JLabel crashIconLabel = new JLabel(crashIcon);
            titlePanel.add(crashIconLabel);
        } catch (IOException ex) {
        }

        // "Has crashed!"
        JLabel crashLabel = new JLabel("KeeperFX has crashed!");
        crashLabel.setFont(new Font("MONOSPACE", Font.BOLD, 18));
        crashLabel.setForeground(Color.RED);
        crashLabel.setBorder(new EmptyBorder(10, 20, 0, 0));
        titlePanel.add(crashLabel);

        // Middle MAIN panel
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        this.add(panel, BorderLayout.CENTER);

        // Info text at top
        JLabel infoLabel = new JLabel(
                "<html>It seems like KeeperFX has crashed and is unable to recover. If you wish to submit technical details about this crash to the KeeperFX developers, you can describe your problem below and click 'Send'.</html>");
        infoLabel.setBorder(new EmptyBorder(20, 20, 10, 20));
        // infoLabel.setPreferredSize(new Dimension(600, 100));
        infoLabel.setPreferredSize(new Dimension(600, 60));
        panel.add(infoLabel);

        // Github panel
        JPanel githubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        githubPanel.setOpaque(false);
        githubPanel.setPreferredSize(new Dimension(600, 30));
        githubPanel.setBorder(new EmptyBorder(0, 15, 0, 20));
        panel.add(githubPanel);

        // Github labels
        JLabel linkLabel1 = new JLabel(
                "<html>You can also raise an issue on Github: </html>");
        JLabel linkLabel2 = GuiUtil.createLink("https://github.com/dkfans/keeperfx");
        githubPanel.add(linkLabel1);
        githubPanel.add(linkLabel2);

        // "Description" label
        JLabel descriptionLabel = new JLabel(
                "<html><h3>Description</h3></html>");
        descriptionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        descriptionLabel.setPreferredSize(new Dimension(600, 30));
        panel.add(descriptionLabel);

        // Add placeholder to description
        TextPrompt descriptionPlaceholder = new TextPrompt(
                "Please explain what happened, and if possible the steps to reproduce this crash.",
                descriptionArea);
        descriptionPlaceholder.setForeground(new Color(75, 75, 75));
        System.out.println(descriptionPlaceholder.getAlignmentX());
        descriptionPlaceholder.setAlignmentX(123);
        descriptionPlaceholder.setAlignmentY(123);

        // Description
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(560, 90));
        descriptionScrollPane.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
        descriptionScrollPane.getHorizontalScrollBar().setUI(new ThemeBasicScrollBarUI());
        descriptionScrollPane.setBorder(null);
        panel.add(descriptionScrollPane);

        // "Include savegame" label
        JLabel saveGameLabel = new JLabel(
                "<html><h3>Include savegame <span style='color:#666666;font-size: 10px; margin-left: 9px;'>(optional)</span></h3></html>");
        saveGameLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        saveGameLabel.setPreferredSize(new Dimension(600, 30));
        panel.add(saveGameLabel);

        // Generate save file list
        ArrayList<SaveFile> saveGameFiles = new ArrayList<SaveFile>();
        saveGameFiles.add(null);
        for (SaveFile saveFile : SaveFileService.getSaveFiles()) {
            saveGameFiles.add(saveFile);
        }

        // Show save file combo box
        saveGameBox = new JComboBox<SaveFile>(saveGameFiles.toArray(new SaveFile[0]));
        saveGameBox.setPreferredSize(new Dimension(560, 30));
        panel.add(saveGameBox);

        // Additional data information label
        JLabel dataInfoLabel = new JLabel(
                "<html>Additional data has been gathered and will be added to the report: 'keeperfx.log', game output, version</html>");
        dataInfoLabel.setBorder(new EmptyBorder(30, 20, 20, 20));
        dataInfoLabel.setPreferredSize(new Dimension(600, 50));
        // dataInfoLabel.setForeground(new Color(100, 100, 100));
        panel.add(dataInfoLabel);

        ////////////////////////////////////////////////////////////////////////

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // "Close" button
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.setBackground(new Color(100, 100, 100));
        closeButton.addActionListener(e -> this.dispose());
        closeButton.setEnabled(true);
        bottomPanel.add(closeButton, BorderLayout.PAGE_END);

        // "Send" button
        sendButton.setPreferredSize(new Dimension(150, 50));
        sendButton.addActionListener(e -> this.sendReport());
        sendButton.setEnabled(true);
        bottomPanel.add(sendButton, BorderLayout.PAGE_END);

        // Show window
        this.setVisible(true);
    }

    private void sendReport() {

        System.out.println("Sending crash report...");

        // Disable buttons
        this.closeButton.setEnabled(false);
        this.sendButton.setEnabled(false);

        // Get data
        String description = this.descriptionArea.getText();
        SaveFile saveFile = (SaveFile) this.saveGameBox.getSelectedItem();

        // Create JSONObject to post to server
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description", description);
        jsonObject.put("game_version", Main.kfxVersion);
        jsonObject.put("game_log", this.kfxLog);
        jsonObject.put("game_output", this.gameConsoleOutput);
        jsonObject.put("source", "ImpLauncher " + Main.impLauncherVersion);

        // Add save file to report
        if (saveFile != null) {

            try {

                // Get temp dir
                String tempDirPathString = System.getProperty("java.io.tmpdir");
                File tempDir = new File(tempDirPathString);
                System.out.println("Temp dir: " + tempDir);

                // Create temporary name for archive
                String fileName = "keeperfx-" + saveFile.fileName + "-" +
                        java.util.UUID.randomUUID().toString().substring(0, 8);

                // Archive service
                Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.SEVEN_Z);

                // Create archive with save file
                File archive = archiver.create(fileName, tempDir, saveFile.file);
                System.out.println("Temporary save archive created:" + archive.getPath());

                // Add to JSON
                try (FileInputStream fileInputStream = new FileInputStream(archive)) {

                    // Add data
                    byte[] fileBytes = new byte[(int) archive.length()];
                    fileInputStream.read(fileBytes);
                    jsonObject.put("save_file_data", (String) Base64.getEncoder().encodeToString(fileBytes));

                    // Add filename
                    // Filename should come after data so if it fails the filename is not added.
                    jsonObject.put("save_file_name", (String) saveFile.fileName + ".7z");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {

            // Send HTTP request
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CrashReport.endPointURI))
                    .POST(BodyPublishers.ofString(jsonObject.toJSONString()))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<?> response = client.send(request, BodyHandlers.ofString());
            System.out.println("Crash Report HTTP request status code: " + response.statusCode());

            // Convert to JSONObject
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse((String) response.body());

            // Check if JSONObject is valid
            if (data_obj != null) {

                // Check for success
                boolean success = (boolean) data_obj.get("success");
                if (success) {

                    // SUCCESS!
                    System.out.println("Successfully submitted crash report!");

                    // Show messagebox with ID and exit crash report panel
                    String crashReportIdString = String.valueOf(data_obj.get("id"));
                    JOptionPane.showMessageDialog(this,
                            "Your crash report has been successfully submitted!\n\n" +
                                    "The KeeperFX team can not guarantee immediate results, \n" +
                                    "but your feedback is very helpful for the developers working on KeeperFX.\n\n" +
                                    "Report ID: " + crashReportIdString,
                            "Crash Report Submitted!",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    return;

                } else {

                    // No success
                    System.out.println("API crash-report endpoint error: " + data_obj.get("error"));
                    System.out.println("Failed to submit crash report...!");
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Something went wrong
        JOptionPane.showMessageDialog(this, "Failed to submit crash report.", "ImpLauncher Error",
                JOptionPane.ERROR_MESSAGE);
        this.dispose();
        return;
    }
}
