package src;

import java.awt.*;
import java.io.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.metal.MetalButtonUI;

import src.Setting.*;

public class Settings extends JDialog {

    private Main mainWindow;
    private JPanel panelLeftTop;
    private JPanel cardPanel = new JPanel(new CardLayout());
    private JButton saveButton = GuiUtil.createDefaultButton("Save & Close");

    private boolean runOptionChanged = false;

    // Gameplay panel
    private JComboBox<String> languageDropdown;
    private JCheckBox skipIntroCheckBox;
    private JCheckBox cheatsCheckBox;
    private JCheckBox censorCheckBox;
    private JComboBox<String> screenshotsDropdown;

    // Graphics
    private JComboBox<String> resolutionDropdown;
    private JComboBox<String> displayModeDropdown;
    private JCheckBox smoothenVidCheckBox;

    // Sound
    private JCheckBox soundDisabledCheckBox;
    private JCheckBox useMusicFilesCheckBox;
    private JCheckBox atmosEnabledCheckBox;
    private JComboBox<String> atmosFrequencyDropdown;
    private JComboBox<String> atmosVolumeDropdown;
    private JCheckBox pauseMusicWhenPausedCheckBox;
    private JCheckBox muteAudioWhenNoFocusCheckBox;

    // Input panel
    private JTextField mouseSensitivityField;
    private JCheckBox altInputCheckBox;
    private JCheckBox unlockCursorOnPauseCheckBox;
    private JCheckBox screenEdgeCameraPanCheckBox;

    // Multiplayer panel
    private JTextField masterServerHostField;

    // Updates panel
    private JComboBox<String> gameBuildDropdown;

    private void loadPanelComponents() {

        // Gameplay
        languageDropdown = GameLanguages.createComboBox(Main.keeperFxCfg.getProperty("LANGUAGE"));
        skipIntroCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("nointro") != null);
        cheatsCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("alex") != null);
        censorCheckBox = new JCheckBox("Enabled", ((String) Main.keeperFxCfg.getProperty("CENSORSHIP")).equals("ON"));
        screenshotsDropdown = Screenshots.createComboBox(Main.keeperFxCfg.getProperty("SCREENSHOT"));

        // Input
        mouseSensitivityField = new JTextField(Main.keeperFxCfg.getProperty("POINTER_SENSITIVITY"), 20);
        altInputCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("altinput") != null);
        unlockCursorOnPauseCheckBox = new JCheckBox("Enabled",
                ((String) Main.keeperFxCfg.getProperty("UNLOCK_CURSOR_WHEN_GAME_PAUSED")).equals("ON"));
        screenEdgeCameraPanCheckBox = new JCheckBox("Enabled",
                ((String) Main.keeperFxCfg.getProperty("CURSOR_EDGE_CAMERA_PANNING")).equals("ON"));

        // Graphics
        String fullResolutionString = (String) Main.keeperFxCfg.getProperty("INGAME_RES");
        String resolution = null;
        String displayMode = null;
        Pattern pattern = Pattern.compile(".*?([0-9]*x[0-9]*)([w-x]).*");
        Matcher m = pattern.matcher(fullResolutionString);
        if (m.matches()) {
            resolution = m.group(1);
            displayMode = m.group(2);
        }
        resolutionDropdown = ScreenResolutions.createComboBox(resolution);
        displayModeDropdown = DisplayModes.createComboBox(displayMode);
        smoothenVidCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("vidsmooth") != null);

        // Sound
        soundDisabledCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("nosound") == null);
        useMusicFilesCheckBox = new JCheckBox("Enabled", Main.runOptions.getOption("nocd") != null);
        atmosEnabledCheckBox = new JCheckBox("Enabled",
                ((String) Main.keeperFxCfg.getProperty("ATMOSPHERIC_SOUNDS")).equals("ON"));
        atmosFrequencyDropdown = LowMidHigh.createComboBox(Main.keeperFxCfg.getProperty("ATMOS_FREQUENCY"));
        atmosVolumeDropdown = LowMidHigh.createComboBox(Main.keeperFxCfg.getProperty("ATMOS_VOLUME"));
        pauseMusicWhenPausedCheckBox = new JCheckBox("Enabled",
                ((String) Main.keeperFxCfg.getProperty("PAUSE_MUSIC_WHEN_GAME_PAUSED")).equals("ON"));
        muteAudioWhenNoFocusCheckBox = new JCheckBox("Enabled",
                ((String) Main.keeperFxCfg.getProperty("MUTE_AUDIO_ON_FOCUS_LOST")).equals("ON"));

        // Multiplayer
        masterServerHostField = new JTextField(Main.keeperFxCfg.getProperty("MASTERSERVER_HOST"), 20);

        // Updates
        gameBuildDropdown = GameBuild.createComboBox(Main.kfxReleaseType.toString());
    }

    public Settings(Main mainWindow) {

        loadPanelComponents();

        this.mainWindow = mainWindow;

        this.setTitle("KeeperFX Settings");
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(700, 500);
        this.setResizable(false);
        this.setModal(true);
        this.setLocationRelativeTo(this.mainWindow);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ////////////////////////////////////////////////////////////////////////

        // Left panel
        JPanel panelLeft = new JPanel(new BorderLayout());
        panelLeft.setBackground(new Color(32, 32, 32));
        panelLeft.setPreferredSize(new Dimension(160, 500));
        panelLeft.setBorder(new EmptyBorder(6, 6, 6, 6));
        this.add(panelLeft, BorderLayout.LINE_START);

        ////////////////////////////////////////////////////////////////////////

        // Top part of left panel
        this.panelLeftTop = new JPanel(new GridLayout(0, 1, 6, 6));
        this.panelLeftTop.setBackground(new Color(32, 32, 32));
        panelLeft.add(this.panelLeftTop, BorderLayout.PAGE_START);

        // Left panel menu items
        this.panelLeftTop.add(this.createMenuItemButton("Gameplay", "GAMEPLAY_PANEL"), BorderLayout.PAGE_START);
        this.panelLeftTop.add(this.createMenuItemButton("Graphics", "GFX_PANEL"), BorderLayout.PAGE_START);
        this.panelLeftTop.add(this.createMenuItemButton("Sound", "SOUND_PANEL"), BorderLayout.PAGE_START);
        this.panelLeftTop.add(this.createMenuItemButton("Input", "INPUT_PANEL"), BorderLayout.PAGE_START);
        this.panelLeftTop.add(this.createMenuItemButton("Multiplayer", "MP_PANEL"), BorderLayout.PAGE_START);
        this.panelLeftTop.add(this.createMenuItemButton("Updates", "UPDATE_PANEL"), BorderLayout.PAGE_START);

        // Disable first button
        this.panelLeftTop.getComponents()[0].setEnabled(false);

        ////////////////////////////////////////////////////////////////////////

        // Bottom part of left panel
        JPanel panelLeftBottom = new JPanel(new GridLayout(0, 1, 6, 6));
        panelLeftBottom.setBackground(new Color(32, 32, 32));
        panelLeft.add(panelLeftBottom, BorderLayout.PAGE_END);

        // "Save" button (Bottom, Right panel)
        saveButton.setPreferredSize(new Dimension(150, 50));
        saveButton.addActionListener(e -> this.saveButtonAction());
        saveButton.setBackground(new Color(0, 125, 185));
        saveButton.setVisible(false);
        panelLeftBottom.add(saveButton);

        // "Close" button (Bottom, Right panel)
        JButton closeButton = GuiUtil.createDefaultButton("Close");
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.addActionListener(e -> this.dispose());
        panelLeftBottom.add(closeButton);

        ////////////////////////////////////////////////////////////////////////

        cardPanel.add(this.gameplayPanel(), "GAMEPLAY_PANEL");
        cardPanel.add(this.gfxPanel(), "GFX_PANEL");
        cardPanel.add(this.soundPanel(), "SOUND_PANEL");
        cardPanel.add(this.inputPanel(), "INPUT_PANEL");
        cardPanel.add(this.mpPanel(), "MP_PANEL");
        cardPanel.add(this.updatePanel(), "UPDATE_PANEL");
        this.add(cardPanel, BorderLayout.CENTER);

        ////////////////////////////////////////////////////////////////////////

        // Show window
        this.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    private JButton createMenuItemButton(String buttonText, String panelName) {
        JButton button = GuiUtil.createDefaultButton(buttonText);
        button.setPreferredSize(new Dimension(150, 50));
        button.addActionListener(e -> ((CardLayout) cardPanel.getLayout()).show(cardPanel, panelName));
        button.addActionListener(e -> {
            // Enable all buttons
            for (Component menuItem : this.panelLeftTop.getComponents()) {
                menuItem.setEnabled(true);
            }
            // Disable active button
            button.setEnabled(false);
        });
        button.setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return new Color(0, 225, 255);
            }
        });
        return button;
    }

    private JPanel gameplayPanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Game Language", this.languageDropdown, false));
        panel.add(this.createSettingOption("Skip Intro", this.skipIntroCheckBox, true));
        panel.add(this.createSettingOption("Cheats", this.cheatsCheckBox, true));
        panel.add(this.createSettingOption("Censorship", this.censorCheckBox, false));
        panel.add(this.createSettingOption("Screenshot type", this.screenshotsDropdown, false));
        return createSettingsPanelContainer(panel);
    }

    private JPanel gfxPanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Resolution", this.resolutionDropdown, false));
        panel.add(this.createSettingOption("Display mode", this.displayModeDropdown, false));
        panel.add(this.createSettingOption("Smoothen Video", this.smoothenVidCheckBox, true));
        return createSettingsPanelContainer(panel);
    }

    private JPanel soundPanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Sound", this.soundDisabledCheckBox, true));
        panel.add(this.createSettingOption("Use music files instead of CD", this.useMusicFilesCheckBox, true));
        panel.add(this.createSettingOption("Atmospheric Sounds", this.atmosEnabledCheckBox, false));
        panel.add(this.createSettingOption("Atmospheric Frequency", this.atmosFrequencyDropdown, false));
        panel.add(this.createSettingOption("Atmospheric Volume", this.atmosVolumeDropdown, false));
        panel.add(
                this.createSettingOption("Pause music when game is paused", this.pauseMusicWhenPausedCheckBox, false));
        panel.add(
                this.createSettingOption("Mute audio when game loses focus", this.muteAudioWhenNoFocusCheckBox, false));
        return createSettingsPanelContainer(panel);
    }

    private JPanel inputPanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Mouse Sensitivity %\n(0 = raw input)", this.mouseSensitivityField, false));
        panel.add(this.createSettingOption("Alternative Input", this.altInputCheckBox, true));
        panel.add(this.createSettingOption("Unlock cursor when paused", this.unlockCursorOnPauseCheckBox, false));
        panel.add(this.createSettingOption("Screen edge camera panning", this.screenEdgeCameraPanCheckBox, false));
        return createSettingsPanelContainer(panel);
    }

    private JPanel mpPanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Masterserver", this.masterServerHostField, false));
        return createSettingsPanelContainer(panel);
    }

    private JPanel updatePanel() {
        JPanel panel = createSettingsPanel();
        panel.add(this.createSettingOption("Game Build", this.gameBuildDropdown, false));
        return createSettingsPanelContainer(panel);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panel.setBorder(null);
        return panel;
    }

    private JPanel createSettingsPanelContainer(JPanel settingsPanel) {
        // Set size to match with amount of components
        settingsPanel.setPreferredSize(new Dimension(700 - 180, settingsPanel.getComponentCount() * 80));

        // Scroll pane
        JScrollPane containerScrollPane = new JScrollPane(settingsPanel);
        containerScrollPane.setPreferredSize(new Dimension(700 - 160, 465));
        containerScrollPane.setBorder(null);
        containerScrollPane.getVerticalScrollBar().setUI(new ThemeBasicScrollBarUI());
        containerScrollPane.getHorizontalScrollBar().setUI(new ThemeBasicScrollBarUI());
        containerScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Container
        JPanel containerPanel = new JPanel();
        containerPanel.add(containerScrollPane);
        containerPanel.setPreferredSize(new Dimension(700 - 160, 500));
        return containerPanel;
    }

    private void saveButtonAction() {

        // GAMEPLAY
        Main.keeperFxCfg.setProperty("LANGUAGE", GameLanguages.getKey(this.languageDropdown));
        Main.runOptions.toggleOption("nointro", this.skipIntroCheckBox.isSelected());
        Main.runOptions.toggleOption("alex", this.cheatsCheckBox.isSelected());
        Main.keeperFxCfg.setProperty("CENSORSHIP", this.censorCheckBox.isSelected() ? "ON" : "OFF");
        Main.keeperFxCfg.setProperty("SCREENSHOT", Screenshots.getKey(this.screenshotsDropdown));

        // GRAPHICS
        String resolutionString = ScreenResolutions.getKey(this.resolutionDropdown)
                + ScreenResolutions.getKey(this.displayModeDropdown) + "32";
        Main.keeperFxCfg.setProperty("INGAME_RES", resolutionString);
        Main.keeperFxCfg.setProperty("FRONTEND_RES",
                resolutionString + " " + resolutionString + " " + resolutionString);
        Main.runOptions.toggleOption("vidsmooth", this.smoothenVidCheckBox.isSelected());

        // SOUND
        Main.runOptions.toggleOption("nosound", this.soundDisabledCheckBox.isSelected() == false);
        Main.runOptions.toggleOption("nocd", this.useMusicFilesCheckBox.isSelected());
        Main.keeperFxCfg.setProperty("ATMOSPHERIC_SOUNDS", this.atmosEnabledCheckBox.isSelected() ? "ON" : "OFF");
        Main.keeperFxCfg.setProperty("ATMOS_FREQUENCY", LowMidHigh.getKey(this.atmosFrequencyDropdown));
        Main.keeperFxCfg.setProperty("ATMOS_VOLUME", LowMidHigh.getKey(this.atmosVolumeDropdown));
        Main.keeperFxCfg.setProperty("PAUSE_MUSIC_WHEN_GAME_PAUSED",
                this.pauseMusicWhenPausedCheckBox.isSelected() ? "ON" : "OFF");
        Main.keeperFxCfg.setProperty("MUTE_AUDIO_ON_FOCUS_LOST",
                this.muteAudioWhenNoFocusCheckBox.isSelected() ? "ON" : "OFF");

        // INPUT
        Main.keeperFxCfg.setProperty("POINTER_SENSITIVITY", this.mouseSensitivityField.getText());
        Main.runOptions.toggleOption("altinput", this.altInputCheckBox.isSelected());
        Main.keeperFxCfg.setProperty("UNLOCK_CURSOR_WHEN_GAME_PAUSED",
                this.unlockCursorOnPauseCheckBox.isSelected() ? "ON" : "OFF");
        Main.keeperFxCfg.setProperty("CURSOR_EDGE_CAMERA_PANNING",
                this.screenEdgeCameraPanCheckBox.isSelected() ? "ON" : "OFF");

        // MP
        Main.keeperFxCfg.setProperty("MASTERSERVER_HOST", this.masterServerHostField.getText());

        // Updates
        // TODO: allow user to choose if they want to enable/disable automatic updates

        // Save .cfg file
        try {
            Main.keeperFxCfg.update(new File(Main.launcherRootDir + File.separator + "keeperfx.cfg"));
        } catch (Exception ex) {
        }

        // Show message that the game need to be launched with ImpLauncher if a run
        // option has changed
        if (this.runOptionChanged) {
            JOptionPane.showMessageDialog(this,
                    "You have changed one or more settings that only work when the game is launched using ImpLauncher."
                            + "\nThese settings are marked with a blue asterisk \"*\"."
                            + "\nYou will have to use ImpLauncher to start your game.",
                    "ImpLauncher",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Remember run options
        Main.runOptions.saveOptionsToFile();

        // Reset save button and close settings dialog
        this.saveButton.setVisible(false);
        this.dispose();

        // Handle game build change
        KfxReleaseType newKfxReleaseType = KfxReleaseType.valueOf(GameBuild.getKey(this.gameBuildDropdown));
        if (newKfxReleaseType != Main.kfxReleaseType) {
            if (newKfxReleaseType == KfxReleaseType.STABLE || newKfxReleaseType == KfxReleaseType.ALPHA) {
                new Thread(() -> (new GameUpdater(Main.main)).checkForUpdates(newKfxReleaseType)).start();
            }
        }
    }

    private JPanel createSettingOption(String labelText, JComponent component, boolean isRunOption) {
        int maxHeight = 38;
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.setPreferredSize(new Dimension(700 - 180, maxHeight + 32));
        optionPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 20, 20, 20), null));

        // Show asterisk next to run options
        if (isRunOption) {
            labelText += "<span style='color: #00E0F4'>*</span>";
        }

        JLabel label = new JLabel("<html>" + labelText + "</html>");
        label.setBorder(new EmptyBorder(0, 0, 0, 20));
        label.setPreferredSize(new Dimension(180, maxHeight + 32));

        optionPanel.add(label, BorderLayout.LINE_START);
        optionPanel.add(component, BorderLayout.CENTER);

        component.setBackground(new Color(40, 40, 40));

        if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setUI(new ThemeBasicComboBoxUI());
            // comboBox.setEditor(new ThemeComboBoxEditor());
            comboBox.setRenderer(new ThemeComboBoxRenderer());
            comboBox.addActionListener(e -> this.saveButton.setVisible(true));
            if (isRunOption) {
                comboBox.addActionListener(e -> this.runOptionChanged = true);
            }
        } else if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            checkBox.addActionListener(e -> this.saveButton.setVisible(true));
            if (isRunOption) {
                checkBox.addActionListener(e -> this.runOptionChanged = true);
            }
        } else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setUI(new ThemeBasicTextFieldUI());
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    saveButton.setVisible(true);
                    if (isRunOption) {
                        runOptionChanged = true;
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    saveButton.setVisible(true);
                    if (isRunOption) {
                        runOptionChanged = true;
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // Handle attribute changes (not relevant for plain text)
                }
            });
        } else {
            System.out.print(component);
        }

        return optionPanel;
    }
}
