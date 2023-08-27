package src;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.Date;
import java.util.List;

public class Install extends JDialog {

    private Main mainWindow;

    private JTextField dkPathField = new JTextField(28);

    private JButton installButton = GuiUtil.createDefaultButton("Install");

    private JTextArea installOutput = new JTextArea();

    private JCheckBox copyMusicCheckBox = new JCheckBox("Copy or extract music");

    private void printOutput(String text) {
        this.installOutput.append(
                "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] "
                        + text
                        + "\r\n");
    }

    private void clearOutput() {
        this.installOutput.setText("");
    }

    private void showFailureAlert() {
        JOptionPane.showMessageDialog(this,
                "Installation failed.\nSome extra information has been logged in the window.", "ImpLauncher Warning",
                JOptionPane.ERROR_MESSAGE);
    }

    public Install(Main mainWindow) {

        this.mainWindow = mainWindow;

        this.setTitle("Install KeeperFX");
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(500, 550);
        this.setResizable(false);
        this.setModal(true);
        this.setLocationRelativeTo(mainWindow);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Top part
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(50, 50, 50));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.add(mainPanel, BorderLayout.CENTER);

        ////////////////////////////////////////////////////////////////////////

        // Info label
        String dkPathInfoText = "Browse and select your original Dungeon Keeper folder." +
                " You can use the digital Gold edition (GOG/EA) or the original CD." +
                " This folder will be used to copy over the original Dungeon Keeper files.";
        JLabel dkPathInfoLabel = new JLabel("<html>" + dkPathInfoText + "</html>");
        dkPathInfoLabel.setPreferredSize(new Dimension(460, 85));
        dkPathInfoLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainPanel.add(dkPathInfoLabel);

        ////////////////////////////////////////////////////////////////////////

        // Info label
        String dkPathText = "Dungeon Keeper Location:";
        JLabel dkPathLabel = new JLabel("<html>" + dkPathText + "</html>");
        JPanel dkPathPanel = new JPanel(new BorderLayout());
        dkPathPanel.add(dkPathLabel, BorderLayout.NORTH);
        dkPathPanel.setBackground(new Color(50, 50, 50));
        dkPathPanel.setPreferredSize(new Dimension(460, 20));
        mainPanel.add(dkPathPanel);

        ////////////////////////////////////////////////////////////////////////

        // Input path
        dkPathField.setMargin(new Insets(7, 7, 7, 7));
        dkPathField.setPreferredSize(new Dimension(350, 30));
        mainPanel.add(dkPathField);

        // Input browse button
        JButton browseButton = GuiUtil.createDefaultButton("Browse");
        browseButton.setPreferredSize(new Dimension(100, 30));
        browseButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser("");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String installSourcePath = fileChooser.getSelectedFile().getAbsolutePath();
                    dkPathField.setText(installSourcePath);
                    this.printOutput("Path selected: " + installSourcePath);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this,
                        "Failed to open directory browser", "ImpLauncher Warning",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(browseButton);

        ////////////////////////////////////////////////////////////////////////

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.setBackground(new Color(50, 50, 50));
        optionsPanel.setPreferredSize(new Dimension(460, 70));
        optionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        mainPanel.add(optionsPanel);

        // Add Music checkbox
        this.copyMusicCheckBox.setSelected(true);
        optionsPanel.add(copyMusicCheckBox);

        ////////////////////////////////////////////////////////////////////////

        // this.installOutput.setPreferredSize(new Dimension(460, 200));
        this.installOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.installOutput.setEditable(false);
        this.installOutput.setAutoscrolls(true);

        JScrollPane scrollOutput = new JScrollPane(this.installOutput);
        scrollOutput.setPreferredSize(new Dimension(460, 185));
        scrollOutput.setBounds(0, 0, 460, 185);
        scrollOutput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollOutput.setBorder(null);
        scrollOutput.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
        scrollOutput.getHorizontalScrollBar().setUI(new ThemeBasicScrollBarUI());
        mainPanel.add(scrollOutput);

        ////////////////////////////////////////////////////////////////////////

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // "Close" button
        JButton closeButton = GuiUtil.createDefaultButton("Close");
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.setBackground(new Color(45, 45, 45));
        closeButton.addActionListener(e -> this.dispose());
        closeButton.setEnabled(true);
        bottomPanel.add(closeButton, BorderLayout.PAGE_END);

        // "Install" button
        this.installButton.setPreferredSize(new Dimension(150, 50));
        this.installButton.addActionListener(e -> this.installAction());
        this.installButton.setEnabled(true);
        bottomPanel.add(this.installButton, BorderLayout.PAGE_END);

        ////////////////////////////////////////////////////////////////////////

        // Show window
        this.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    public void installAction() {

        InstallType installType = null;

        this.clearOutput();

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        String dkDirString = dkPathField.getText();

        if (dkDirString.length() == 0) {
            JOptionPane.showMessageDialog(this,
                    "You did not enter a location.",
                    "ImpLauncher Installation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File dkDir = new File(dkDirString);

        // Make sure directory exists and is readable
        if (!dkDir.exists() || !dkDir.isDirectory() || !dkDir.canRead()) {
            this.printOutput("Dungeon Keeper dir is not readable: " + dkDirString);
            JOptionPane.showMessageDialog(this,
                    "The chosen Dungeon Keeper directory is not a readable directory!",
                    "ImpLauncher Installation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        // Check for CD ROOT
        // If this check passes we also set the root DK path to /keeper,
        // which is the CD data dir
        File dkPathKeeperDir = new File(dkDir.getPath() + "/keeper");
        File dkPathSetupFile = new File(dkDir.getPath() + "/setup.exe");
        if (dkPathKeeperDir.exists() && dkPathSetupFile.exists()) {
            installType = InstallType.CD;
            dkDir = new File(dkDir.getPath() + "/keeper");
            this.printOutput("'KEEPER' dir and 'setup.exe' found");
        }

        // Check for CD '/keeper' DIR
        if (installType == null) {
            File dkPathDSetupFile = new File(dkDir.getPath() + "/dsetup.dll");
            File dkPathWinSetup = new File(dkDir.getPath() + "/winsetup");
            if (dkPathDSetupFile.exists() && dkPathWinSetup.exists()) {
                installType = InstallType.CD;
                this.printOutput("'dsetup.dll' file and 'winsetup' dir found");
            }
        }

        // Check for GOG
        if (installType == null) {
            File gogFile = new File(dkDir.getPath() + "/game.gog");
            if (gogFile.exists()) {
                installType = InstallType.GOG;
                this.printOutput("'game.gog' file found");
            }
        }

        // Check for a manual installation
        // This also works for the common Lutris install script
        if (installType == null) {
            File manualInstallFile = new File(dkDir.getPath() + "/data/BLUEPAL.DAT");
            if (manualInstallFile.exists()) {
                installType = InstallType.MANUAL;
                this.printOutput("'data/BLUEPAL.DAT' file found");
            }
        }

        // Check for KFX installation
        if (installType == null) {
            File kfxFile = new File(dkDir.getPath() + "/keeperfx.exe");
            if (kfxFile.exists()) {
                installType = InstallType.KFX;
                this.printOutput("'keeperfx.exe' file found");
            }
        }

        // Make sure we now know what installation the user has
        if (installType == null) {
            this.printOutput("Unable to determine Dungeon Keeper installation type");
            this.printOutput("Is this the correct Dungeon Keeper folder?");
            this.showFailureAlert();
            return;
        }

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        // Show install type and startup message
        this.printOutput("Starting installation...");
        this.printOutput("Install type: " + installType.toString());

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        // Create directories if they don't exists yet
        File dataStorage = new File(Main.launcherRootDir + "/data");
        File musicStorage = new File(Main.launcherRootDir + "/music");
        if (!dataStorage.exists()) {
            if (!dataStorage.mkdir()) {
                this.printOutput("Failed to create directory: " + dataStorage.getAbsolutePath());
                this.showFailureAlert();
                return;
            } else {
                this.printOutput("data directory created: " + dataStorage.getAbsolutePath());
            }
        }
        if (!musicStorage.exists()) {
            if (!musicStorage.mkdir()) {
                this.printOutput("Failed to create directory: " + musicStorage.getAbsolutePath());
                this.showFailureAlert();
                return;
            } else {
                this.printOutput("music directory created: " + musicStorage.getAbsolutePath());
            }
        }

        this.printOutput("KeeperFX directories are present");

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        this.printOutput("Copying required Dungeon Keeper files...");

        boolean installResult = false;

        // Handle CD installation
        if (installType == InstallType.CD) {
            installResult = this.installUsingCD(dkDir);
        }

        // Handle GOG installation
        if (installType == InstallType.GOG) {
            installResult = this.installUsingGOG(dkDir);
        }

        // Handle KFX installation
        // Uses a pre-existing KFX installation (mostly for power users)
        if (installType == InstallType.KFX) {
            installResult = this.installUsingKFX(dkDir);
        }

        // Handle manual installation
        if (installType == InstallType.MANUAL) {
            installResult = this.installUsingManualInstall(dkDir);
        }

        if (installResult == false) {
            this.showFailureAlert();
            return;
        }

        this.printOutput("All required Dungeon Keeper files copied!");

        // ////////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////////

        if (true) {
            boolean copyMusicResult = false;

            // Handle CD music
            // >>>>> ripping is not yet supported :(
            if (installType == InstallType.CD) {
                int openWorkshopResult = JOptionPane.showConfirmDialog(this,
                        "Ripping music from the CD is not supported yet.\n\nDo you wish to open the workshop page to download the music and add it manually?",
                        "ImpLauncher Installation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (openWorkshopResult == JOptionPane.YES_OPTION) {
                    Main.openBrowserURL("https://keeperfx.net/workshop/item/393/keeperfx-music");
                }
            }

            if (installType != InstallType.CD) {
                // Handle GOG music
                if (installType == InstallType.GOG) {
                    copyMusicResult = this.copyMusicFromGOG(dkDir);
                }

                // Handle KFX music
                // Uses a pre-existing KFX installation (mostly for power users)
                if (installType == InstallType.KFX) {
                    copyMusicResult = this.copyMusicFromKFX(dkDir);
                }

                if (copyMusicResult == false) {

                    // Show message box with download link on failure
                    int openWorkshopResult = JOptionPane.showConfirmDialog(this,
                            "ImpLauncher failed to copy the music files.\n\nDo you wish to open the workshop page to download the music and add it manually?",
                            "ImpLauncher Installation", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (openWorkshopResult == JOptionPane.YES_OPTION) {
                        Main.openBrowserURL("https://keeperfx.net/workshop/item/393/keeperfx-music");
                    }
                }
            }

        }

        ////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        // Success!!
        this.printOutput("Installation finished!");
        this.dispose();

        // Re-run app startup stuff
        Main.main.appStartup();

        // Show message and ask to open settings panel
        int openSettingsResult = JOptionPane.showConfirmDialog(this.mainWindow,
                "Installation successful!\nDo you want to open the Settings panel to configure your game?",
                "KeeperFX Installation", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
        if (openSettingsResult == JOptionPane.YES_OPTION) {
            new Settings(Main.main);
        }
    }

    public boolean installUsingGOG(File dkRootDir) {
        // We can install using the default installation procedure because
        // the GOG structure is the default CD structure but in UPPERCASE.
        // (And the default installation procedure always makes everything lowercase)
        return this.installDefault(dkRootDir, InstallFiles.gogFiles);
    }

    public boolean installUsingCD(File dkRootDir) {
        return this.installDefault(dkRootDir, InstallFiles.cdFiles);
    }

    public boolean installUsingKFX(File dkRootDir) {
        return this.installDefault(dkRootDir, InstallFiles.kfxFiles);
    }

    public boolean installUsingManualInstall(File dkRootDir) {
        // This installation has a wrong file structure, but we'll support it regardless
        return this.installDefault(dkRootDir, InstallFiles.manualInstallFiles);
    }

    private boolean installDefault(File dkRootDir, String[] sourceFiles) {

        for (String filePath : sourceFiles) {

            File file = new File(dkRootDir.getPath() + File.separator + filePath);
            if (!file.exists() || !file.canRead()) {
                this.printOutput("Required file not found: " + file.getPath());
                return false;
            }

            File newFile = new File(Main.launcherRootDir + File.separator + filePath.toLowerCase());

            try {
                Files.copy(file.toPath(), newFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                this.printOutput("File copied: '" + file.getPath() + "' -> '" +
                        newFile.toPath() + "'");
            } catch (Exception ex) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ex.printStackTrace(new PrintStream(out));
                this.printOutput("Exception: " + new String(out.toByteArray()));
                this.printOutput("Failed to copy file: " + file.getPath());
                return false;
            }
        }

        return true;
    }

    public boolean copyMusicFromGOG(File dkRootDir) {
        return this.copyOggMusicFromDir(dkRootDir);
    }

    public boolean copyMusicFromKFX(File dkRootDir) {
        return this.copyOggMusicFromDir(new File(dkRootDir.getPath() + File.separator + "music"));
    }

    private boolean copyOggMusicFromDir(File musicRootDir) {

        boolean musicSuccessfullyCopied = true;

        this.printOutput("Copying OGG music files...");

        // Loop trough music
        for (String musicFileName : InstallFiles.gogMusicFiles) {

            // Create file paths
            File musicFile = new File(musicRootDir + File.separator + musicFileName);
            File newMusicFile = new File(
                    Main.launcherRootDir + File.separator + "music" + File.separator +
                            musicFileName);

            // Make sure music file exists
            if (!musicFile.exists() || !musicFile.canRead()) {
                this.printOutput("Music file does not exist: " + musicFile.getPath());
                musicSuccessfullyCopied = false;
                continue;
            }

            // Copy music file
            try {
                Files.copy(musicFile.toPath(), newMusicFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                this.printOutput(
                        "Music file copied: '" + musicFile.getPath() + "' -> '" +
                                newMusicFile.toPath() + "'");
            } catch (Exception ex) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ex.printStackTrace(new PrintStream(out));
                this.printOutput("Exception: " + new String(out.toByteArray()));
                this.printOutput("Failed to copy music file: " + musicFile.getPath());
                musicSuccessfullyCopied = false;
                continue;
            }

        }

        return musicSuccessfullyCopied;
    }

    public static boolean isInstalled() {
        // Loop trough music
        for (String filePath : InstallFiles.kfxFiles) {
            // Create file paths
            File file = new File(Main.launcherRootDir + File.separator + filePath);
            if (!file.exists()) {
                return false;
            }
        }

        return true;
    }
}
