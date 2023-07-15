import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.util.ArrayList;

public class CrashReport extends JDialog {

    private Main mainWindow;

    private JTextArea descriptionArea = new JTextArea();
    private JComboBox<SaveFile> saveGameBox;
    private JTextArea gameOutputArea = new JTextArea();
    private JTextArea kfxLogArea = new JTextArea();

    private JButton closeButton = GuiUtil.createDefaultButton("Close");
    private JButton sendButton = GuiUtil.createDefaultButton("Send");

    public CrashReport(Main mainWindow, String gameConsoleOutput) {

        this.mainWindow = mainWindow;

        this.setTitle("KeeperFX crash report");
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(600, 750);
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
                "<html>It seems KeeperFX has crashed and is unable to recover. If you wish to submit technical details about this crash to the KeeperFX developers, you can describe your problem below and click 'Send'.</html>");
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

        // .listen(e -> Main.openBrowserURL("https://github.com/dkfans/keeperfx"));
        githubPanel.add(linkLabel1);
        githubPanel.add(linkLabel2);

        // "Description" label
        JLabel descriptionLabel = new JLabel(
                "<html><h3>Description</h3></html>");
        descriptionLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        descriptionLabel.setPreferredSize(new Dimension(600, 30));
        panel.add(descriptionLabel);

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

        // "Game output" label
        JLabel gameOutputLabel = new JLabel(
                "<html><h3>Game output log <span style='color:#666666;font-size: 10px; margin-left: 9px;'>(automatically gathered)</span></h3></html>");
        gameOutputLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        gameOutputLabel.setPreferredSize(new Dimension(600, 30));
        panel.add(gameOutputLabel);

        // Game output
        gameOutputArea.setEnabled(false);
        gameOutputArea.setDisabledTextColor(new Color(100, 100, 100));
        gameOutputArea.setText(gameConsoleOutput);
        JScrollPane gameOutputScrollPane = new JScrollPane(gameOutputArea);
        gameOutputScrollPane.setPreferredSize(new Dimension(560, 90));
        gameOutputScrollPane.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
        gameOutputScrollPane.getHorizontalScrollBar().setUI(new ThemeBasicScrollBarUI());
        gameOutputScrollPane.setBorder(null);
        panel.add(gameOutputScrollPane);

        // "KeeperFX Log" label
        JLabel kfxLogLabel = new JLabel(
                "<html><h3>'keeperfx.log' contents <span style='color:#666666;font-size: 10px; margin-left: 9px;'>(automatically gathered)</span></h3></html>");
        kfxLogLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        kfxLogLabel.setPreferredSize(new Dimension(600, 30));
        panel.add(kfxLogLabel);

        // KeeperFX Log
        kfxLogArea.setEnabled(false);
        kfxLogArea.setDisabledTextColor(new Color(100, 100, 100));
        kfxLogArea.setText("Log not found or not readable");
        JScrollPane kfxLogScrollPane = new JScrollPane(kfxLogArea);
        kfxLogScrollPane.setPreferredSize(new Dimension(560, 90));
        kfxLogScrollPane.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
        kfxLogScrollPane.getHorizontalScrollBar().setUI(new ThemeBasicScrollBarUI());
        kfxLogScrollPane.setBorder(null);
        panel.add(kfxLogScrollPane);

        ////////////////////////////////////////////////////////////////////////

        // Load keeperfx.log into textarea
        File kfxLogFile = new File(Main.launcherRootDir + File.separator + "keeperfx.log");
        if (kfxLogFile.exists() && kfxLogFile.canRead()) {
            try {
                kfxLogArea.setText(
                        Files.readString(kfxLogFile.toPath()));
                kfxLogArea.setCaretPosition(0);
            } catch (Exception ex) {
            }
        }

        ////////////////////////////////////////////////////////////////////////

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // "Close" button
        closeButton.setPreferredSize(new Dimension(150, 50));
        // closeButton.setBackground(new Color(30, 30, 30, 60));
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
        String gameOutput = this.gameOutputArea.getText();
        String kfxLog = this.kfxLogArea.getText();

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
                Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR,
                        CompressionType.XZ);

                // Create archive with save file
                File archive = archiver.create(fileName, tempDir, saveFile.file);

                System.out.println("temporary save archive created:" + archive.getPath());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("NOT IMPLEMENTED YET!!!");
    }
}
