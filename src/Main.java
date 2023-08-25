package src;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main extends JFrame {

    public static Main main;

    public static String impLauncherVersion = "0.3.3";

    public static String launcherRootDir;

    public static KfxReleaseType kfxReleaseType;
    public static String kfxVersion;

    public static CfgProperties keeperFxCfg;
    public static RunOptions runOptions;

    public JButton playButton = GuiUtil.createDefaultButton("Play");
    public JButton installButton = GuiUtil.createDefaultButton("Install");
    public JButton logFileButton = GuiUtil.createDefaultButton("Log");
    public JButton directConnectButton = GuiUtil.createDefaultButton("Direct Connect");

    public static JLabel versionLabel;
    public static JLabel mpLobbyCountLabel;

    public static void main(String[] args) {

        // Load launcher root dir variable
        Main.loadLauncherRootDir();

        // Run migrations
        Migrations.run();

        // Set theme defaults
        Theme.setupTheme();

        // Create Main GUI
        Main.main = new Main();

        // Run startup checks
        Main.main.appStartup();

        // Load run options
        Main.runOptions = new RunOptions();
    }

    public static void loadLauncherRootDir() {

        // Check if debugger is attached
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("jdwp") >= 0;

        if (isDebug) {
            System.out.println("[DEBUG] Java Debug Wire Protocol agent is being used");

            // If debugger is attached we'll use a development keeperfx directory
            try {
                Main.launcherRootDir = new File("./kfx").getCanonicalPath().toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(ERROR);
            }
        } else {

            // Set launcher root dir
            // Location of the dir the .jar file is located in.
            try {
                URL classJarLocation = Main.main.getClass().getProtectionDomain().getCodeSource().getLocation();
                System.out.println("Class Jar Loc: " + classJarLocation);
                if (classJarLocation != null) {
                    Path classJarPath = Paths.get(classJarLocation.toURI());
                    System.out.println("Class Jar Path: " + classJarPath);
                    if (classJarPath.toString().toLowerCase().endsWith(".jar")) {
                        Main.launcherRootDir = classJarPath.getParent().toString();
                    }
                }
                if (Main.launcherRootDir == null) {
                    Main.launcherRootDir = new File(".").getCanonicalPath().toString();
                }
                System.out.println("Working directory: " + Main.launcherRootDir);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(ERROR);
            }

        }
    }

    public Main() {

        // Right panel
        JPanel panelRight = new JPanel(new BorderLayout());
        panelRight.setBackground(new Color(35, 35, 35));
        panelRight.setPreferredSize(new Dimension(160, 500));
        panelRight.setBorder(new EmptyBorder(6, 6, 6, 6));
        this.add(panelRight, BorderLayout.LINE_END);

        ////////////////////////////////////////////////////////////////////////////////

        // Top part of right panel
        JPanel panelRightTop = new JPanel(new GridLayout(0, 1, 6, 6));
        panelRightTop.setBackground(new Color(35, 35, 35));
        panelRight.add(panelRightTop, BorderLayout.PAGE_START);

        // "Workshop" button (Top, Right panel)
        JButton workshopButton = GuiUtil.createDefaultButton("Workshop");
        workshopButton.setPreferredSize(new Dimension(150, 50));
        workshopButton.addActionListener(e -> Main.openBrowserURL("https://keeperfx.net/workshop/browse"));
        panelRightTop.add(workshopButton, BorderLayout.PAGE_START);

        // "Settings" button (Top, Right panel)
        JButton settingsButton = GuiUtil.createDefaultButton("Settings");
        settingsButton.setPreferredSize(new Dimension(150, 50));
        settingsButton.addActionListener(e -> new Settings(this));
        panelRightTop.add(settingsButton, BorderLayout.PAGE_START);

        // "Install" button (Top, Right panel)
        this.installButton.setPreferredSize(new Dimension(150, 50));
        this.installButton.addActionListener(e -> new Install(this));
        panelRightTop.add(this.installButton, BorderLayout.PAGE_START);

        // "Log" button (Top, Right panel)
        this.logFileButton.setPreferredSize(new Dimension(150, 50));
        this.logFileButton.addActionListener(e -> this.openLogFile());
        this.logFileButton.setEnabled(false);
        panelRightTop.add(this.logFileButton, BorderLayout.PAGE_START);

        // "Direct Connect" button (Top, Right panel)
        this.directConnectButton.setPreferredSize(new Dimension(150, 50));
        this.directConnectButton.setEnabled(false);
        this.directConnectButton.addActionListener(e -> new DirectConnect(this));
        panelRightTop.add(this.directConnectButton, BorderLayout.PAGE_START);

        // Multiplayer lobby count
        Main.mpLobbyCountLabel = new JLabel("");
        Main.mpLobbyCountLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        Main.mpLobbyCountLabel.setPreferredSize(new Dimension(150, 20));
        Main.mpLobbyCountLabel.setForeground(Color.GRAY);
        panelRightTop.add(Main.mpLobbyCountLabel, BorderLayout.CENTER);

        ////////////////////////////////////////////////////////////////////////////////

        // Bottom part of right panel
        RelativeLayout panelRightBottomLayout = new RelativeLayout(RelativeLayout.Y_AXIS, 3);
        panelRightBottomLayout.setAlignment(Component.BOTTOM_ALIGNMENT);
        JPanel panelRightBottom = new JPanel(panelRightBottomLayout);
        panelRightBottom.setBackground(new Color(35, 35, 35));
        // panelRightBottom.setOpaque(true);
        panelRight.add(panelRightBottom, BorderLayout.PAGE_END);

        // Version
        Main.versionLabel = new JLabel("Grabbing version...");
        Main.versionLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
        Main.versionLabel.setPreferredSize(new Dimension(150, 20));
        Main.versionLabel.setForeground(Color.DARK_GRAY);
        panelRightBottom.add(Main.versionLabel);

        // "Play" button (Bottom, Right panel)
        this.playButton.setPreferredSize(new Dimension(150, 50));
        this.playButton.setFont(new Font("Consolas", Font.BOLD, 16));
        this.playButton.setEnabled(false); // Will be enabled after startup check
        panelRightBottom.add(this.playButton);

        // Play behavior
        this.playButton.addActionListener(
                e -> new Thread(() -> (new GameLauncher(this,
                        Main.runOptions)).startGame()).start());

        // This can be used when working on the Crash report functionality:
        // this.playButton.addActionListener(
        // e -> (new Thread(() -> {
        // new CrashReport(this, Main.impLauncherVersion);
        // })).start());

        ////////////////////////////////////////////////////////////////////////////////

        // Main window
        this.setTitle("KeeperFX - ImpLauncher " + Main.impLauncherVersion);
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(700, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set icon
        try {
            this.setIconImage(ImageIO.read(this.getClass().getResource("/implauncher-data/imp-portrait.png")));
        } catch (IOException ex) {
        }

        // IndexPanel
        this.add(new IndexPanel(), BorderLayout.CENTER);

        // Show window
        this.setVisible(true);
    }

    public void appStartup() {

        // Make sure this desktop is supported so commands can be ran
        if (!Desktop.isDesktopSupported()) {
            JOptionPane.showMessageDialog(this, "This desktop is not supported", "ImpLauncher Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Make sure we have permissions in the root directory
        // KeeperFX also needs to have permission so we can just force it
        File tempPermissionTestFile = new File(Main.launcherRootDir + File.separator + "implauncher.tmp");
        boolean permissionResult = false;
        try {
            if (tempPermissionTestFile.createNewFile()) {
                if (tempPermissionTestFile.delete()) {
                    permissionResult = true;
                }
            }
        } catch (Exception ex) {
        }
        if (permissionResult == false) {

            String permissionMessage = "Insufficient file permissions.\n" +
                    "ImpLauncher can not edit the files in the KeeperFX directory.";

            if (System.getProperty("os.name").toLowerCase().contains("windows") == true) {
                permissionMessage += "\n\nIt's suggested to move KeeperFX to a location like: 'C:\\Games\\KeeperFX'\n";
            }

            JOptionPane.showMessageDialog(this, permissionMessage, "ImpLauncher Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Check valid Kfx installation
        // Just makes sure the launcher is placed with the keeperfx files
        String missingFile = KfxValidator.checkForMissingFile();
        if (missingFile != null) {
            JOptionPane.showMessageDialog(this,
                    "Missing KeeperFX file: '" + missingFile + "'" +
                            "\n\nMake sure to place ImpLauncher in your KeeperFX directory.",
                    "ImpLauncher Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Check if DK files are moved to KeeperFX
        if (Install.isInstalled()) {
            this.enablePlayButton();
            this.installButton.setText("Reinstall");
        } else {
            // Show message box that opens the Install panel
            int openInstaller = JOptionPane.showConfirmDialog(this,
                    "KeeperFX requires the original Dungeon Keeper files in order to be playable.\nDo you want to start the installer?",
                    "ImpLauncher Installation", JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (openInstaller == JOptionPane.YES_OPTION) {
                new Install(this);
            }
        }

        // Check 'keeperfx.cfg'
        File keeperFxCfg = new File(Main.launcherRootDir + File.separator + "keeperfx.cfg");
        if (!keeperFxCfg.exists()) {

            // Check if we can copy _keeperfx.cfg (with an underscore)
            File defaultKeeperFxCfg = new File(Main.launcherRootDir + File.separator + "_keeperfx.cfg");

            if (defaultKeeperFxCfg.exists()) {
                try {
                    Files.copy(defaultKeeperFxCfg.toPath(), keeperFxCfg.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to copy '_keeperfx.cfg' to 'keeperfx.cfg'.",
                            "ImpLauncher Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

            // Double check and show message to user
            if (!keeperFxCfg.exists() || !keeperFxCfg.canRead()) {
                JOptionPane.showMessageDialog(this,
                        "No 'keeperfx.cfg' found. The game will not work without it.",
                        "ImpLauncher Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(ERROR);
            }
        }

        // Load CFG
        try {
            Main.keeperFxCfg = new CfgProperties();
            Main.keeperFxCfg.load(new FileInputStream(keeperFxCfg.getPath().toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed read 'keeperfx.cfg'",
                    "ImpLauncher Error",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(ERROR);
        }

        // Get version of KeeperFX
        try {
            Main.kfxVersion = EXEFileInfo.getFileVersion(Main.launcherRootDir + File.separator + "keeperfx.exe");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to get KeeperFX version",
                    "ImpLauncher Error",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(ERROR);
        }

        // Define KeeperFX version
        if (Main.kfxVersion.contains("Prototype")) {
            Main.kfxReleaseType = KfxReleaseType.PROTOTYPE;
        } else if (Main.kfxVersion.contains("Alpha")) {
            Main.kfxReleaseType = KfxReleaseType.ALPHA;
        } else {
            Main.kfxReleaseType = KfxReleaseType.STABLE;
        }

        // Update thread
        // This thread handles the following stuff:
        // - It checks if it self-updated (and will remove update tool and show notice)
        // - It will check if it needs to update itself
        // - It will then check if we need to update the game
        new Thread(() -> {
            try {

                // Check for active update process
                File tempUpdaterToolJar = new File(Main.launcherRootDir + File.separator + "implauncher-updater.jar");
                if (tempUpdaterToolJar.exists()) {

                    // Remove the temporary updater tool if it is present (after a second)
                    // This means that we were updating and we can now tell the user!
                    Thread.sleep(1000);
                    if (tempUpdaterToolJar.delete()) {
                        JOptionPane.showMessageDialog(this,
                                "ImpLauncher has been updated to version " + impLauncherVersion + "!",
                                "ImpLauncher Updater", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                // Handle possible self-update
                SelfUpdater selfUpdater = new SelfUpdater(this);
                selfUpdater.checkForUpdates();

                // Handle possible updates
                // Only when release is STABLE or ALPHA
                if (Main.kfxReleaseType == KfxReleaseType.STABLE || Main.kfxReleaseType == KfxReleaseType.ALPHA) {
                    GameUpdater updater = new GameUpdater(this);
                    updater.checkForUpdates();
                }

            } catch (Exception ex) {
            }
        }).start();

        // Check if we must enable Log file button
        this.handleLogFileButton();

        // Show version in GUI
        Main.updateDisplayVersion();

        // Show live count of multiplayer lobbies
        new Thread(() -> {
            while (true) {
                try {
                    // Get the masterserver host
                    String masterServerHost = (String) Main.keeperFxCfg.get("MASTERSERVER_HOST");
                    if (masterServerHost.isEmpty()) {
                        continue;
                    }

                    // Get the lobby count
                    int count = Masterserver.getLobbyCount(masterServerHost);
                    if (count == -1 || count == 0) {
                        Main.mpLobbyCountLabel.setText("");
                    } else if (count == 1) {
                        Main.mpLobbyCountLabel.setText(
                                "<html><span style='color: white;'>" + count + "</span> open MP lobby!</html>");
                    } else {
                        Main.mpLobbyCountLabel.setText(
                                "<html><span style='color: white;'>" + count + "</span> open MP lobbies!</html>");
                    }

                    // Sleep 10 seconds
                    Thread.sleep(10000);
                } catch (Exception ex) {
                }
            }

        }).start();
    }

    public void enablePlayButton() {
        this.playButton.setText("Play");

        this.playButton.setForeground(Color.WHITE);
        this.playButton.setBackground(new Color(0, 125, 185));
        this.playButton.setBorder(BorderFactory.createLineBorder(new Color(31, 153, 205), 1));

        this.playButton.setEnabled(true);
        this.playButton.setFocusable(true);

        // Also enable Direct Connect
        this.directConnectButton.setEnabled(true);
    }

    public void setPlayButtonAsPlaying() {
        this.playButton.setText("Running");

        this.playButton.setForeground(Color.GRAY);
        this.playButton.setBackground(new Color(30, 30, 30));
        this.playButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180,
                180), 1));

        this.playButton.setEnabled(false);
        this.playButton.setFocusable(false);

        // Also disable Direct Connect
        this.directConnectButton.setEnabled(false);
    }

    public void handleLogFileButton() {
        File logFile = new File(Main.launcherRootDir + File.separator + "keeperfx.log");
        this.logFileButton.setEnabled(logFile.exists());
    }

    public static void openBrowserURL(String URL) {
        try {
            Desktop.getDesktop()
                    .browse(new URI(URL));
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(null,
                    "Failed to open webbrowser.\nURL: " + URL,
                    "ImpLauncher Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void openLogFile() {
        try {
            File logFile = new File(Main.launcherRootDir + File.separator + "keeperfx.log");
            Desktop.getDesktop().open(logFile);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open 'keeperfx.log'",
                    "ImpLauncher Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    public static void updateDisplayVersion() {
        Main.main.setTitle("KeeperFX - " + Main.kfxVersion + " - ImpLauncher " + Main.impLauncherVersion);
        Main.versionLabel.setText(Main.kfxVersion);
        Main.versionLabel.repaint();
        Main.versionLabel.getParent().repaint();
        Main.versionLabel.getParent().getParent().repaint();
    }
}