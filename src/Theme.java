package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class Theme {

    public static void setupTheme() {

        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Window
            UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(new Color(35, 35, 35)));
            UIManager.put("InternalFrame.activeTitleForeground", new ColorUIResource(Color.WHITE));
            UIManager.put("InternalFrame.titleFont", new Font("Dialog", Font.PLAIN, 11));
            UIManager.put("JFrame.activeTitleBackground", new Color(35, 35, 35));

            // Option panes
            UIManager.put("OptionPane.background", new Color(40, 40, 40));
            UIManager.put("OptionPane.foreground", new Color(230, 230, 230));
            UIManager.put("OptionPane.messageForeground", new Color(230, 230, 230));

            // Panels
            UIManager.put("Panel.background", new Color(40, 40, 40));
            UIManager.put("Panel.foreground", Color.WHITE);

            // Labels
            UIManager.put("Label.foreground", new Color(230, 230, 230));

            // Buttons
            UIManager.put("Button.background", new Color(32, 32, 32));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.disabledText", new Color(100, 100, 100));
            UIManager.put("Button.border",
                    BorderFactory.createLineBorder(new ColorUIResource(new Color(0, 0, 0, 0)), 7));
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.highlight", new ColorUIResource(new Color(0, 0, 0, 0)));

            // Text field
            UIManager.put("TextField.background", new Color(32, 32, 32));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.border", new EmptyBorder(7, 7, 7, 7));
            UIManager.put("TextField.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("TextField.highlight", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("TextField.disabledText", new Color(100, 100, 100));
            UIManager.put("TextField.caretColor", Color.WHITE);

            // TextArea
            UIManager.put("TextArea.background", new Color(32, 32, 32));
            UIManager.put("TextArea.foreground", Color.WHITE);
            UIManager.put("TextArea.border", BorderFactory.createLineBorder(new Color(32, 32, 32), 7));
            UIManager.put("TextArea.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("TextArea.highlight", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("TextArea.disabledText", new Color(100, 100, 100));
            UIManager.put("TextArea.caretColor", Color.WHITE);

            // Checkbox
            UIManager.put("CheckBox.background", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("CheckBox.foreground", Color.WHITE);
            UIManager.put("CheckBox.disabledText", new Color(100, 100, 100));
            UIManager.put("CheckBox.border", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("CheckBox.highlight", new ColorUIResource(new Color(0, 0, 0, 0)));

            // ComboBox
            UIManager.put("ComboBox.background", new ColorUIResource(new Color(32, 32, 32)));
            UIManager.put("ComboBox.foreground", new ColorUIResource(UIManager.getColor("TextField.foreground")));
            UIManager.put("ComboBox.disabledText", new Color(100, 100, 100));
            UIManager.put("ComboBox.buttonBackground", new Color(32, 32, 32));
            UIManager.put("ComboBox.selectionBackground", new Color(32, 32, 32));
            UIManager.put("ComboBox.borderPainted", false);
            UIManager.put("ComboBox.buttonDarkShadow", null);
            UIManager.put("ComboBox.buttonShadow", null);
            UIManager.put("ComboBox.buttonHighlight", null);
            UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.WHITE));

            // Progress bar
            // progressBar.setUI((ProgressBarUI) BasicProgressBarUI.createUI(progressBar));
            // // <<<< SET THIS on custom progress bars
            UIManager.put("ProgressBar.background", new Color(32, 32, 32));
            UIManager.put("ProgressBar.selectionBackground", new Color(230, 230, 230));
            UIManager.put("ProgressBar.foreground", new Color(52, 52, 52));
            UIManager.put("ProgressBar.border", new ColorUIResource(new Color(32, 32, 32)));

            // ScrollPane
            UIManager.put("ScrollPane.background", new Color(45, 45, 45));

        } catch (Exception ex) {
        }
    }

    /*
     * AATextInfoPropertyKey
     * AuditoryCues.allAuditoryCues
     * AuditoryCues.cueList
     * AuditoryCues.defaultCueList
     * AuditoryCues.noAuditoryCues
     * Button.background
     * Button.border
     * Button.darkShadow
     * Button.defaultButtonFollowsFocus
     * Button.disabledText
     * Button.disabledToolBarBorderBackground
     * Button.focus
     * Button.focusInputMap
     * Button.font
     * Button.foreground
     * Button.gradient
     * Button.highlight
     * Button.light
     * Button.margin
     * Button.rollover
     * Button.rolloverIconType
     * Button.rolloverIconType - ocean
     * Button.select
     * Button.shadow
     * Button.textIconGap
     * Button.textShiftOffset
     * Button.toolBarBorderBackground
     * ButtonUI
     * ButtonUI - javax.swing.plaf.metal.MetalButtonUI
     * CheckBox.background
     * CheckBox.border
     * CheckBox.disabledText
     * CheckBox.focus
     * CheckBox.focusInputMap
     * CheckBox.font
     * CheckBox.foreground
     * CheckBox.gradient
     * CheckBox.icon
     * CheckBox.margin
     * CheckBox.rollover
     * CheckBox.textIconGap
     * CheckBox.textShiftOffset
     * CheckBox.totalInsets
     * CheckBoxMenuItem.acceleratorFont
     * CheckBoxMenuItem.acceleratorForeground
     * CheckBoxMenuItem.acceleratorSelectionForeground
     * CheckBoxMenuItem.arrowIcon
     * CheckBoxMenuItem.background
     * CheckBoxMenuItem.border
     * CheckBoxMenuItem.borderPainted
     * CheckBoxMenuItem.checkIcon
     * CheckBoxMenuItem.commandSound
     * CheckBoxMenuItem.commandSound - sounds/MenuItemCommand.wav
     * CheckBoxMenuItem.disabledForeground
     * CheckBoxMenuItem.font
     * CheckBoxMenuItem.foreground
     * CheckBoxMenuItem.gradient
     * CheckBoxMenuItem.margin
     * CheckBoxMenuItem.selectionBackground
     * CheckBoxMenuItem.selectionForeground
     * CheckBoxMenuItemUI
     * CheckBoxMenuItemUI - javax.swing.plaf.basic.BasicCheckBoxMenuItemUI
     * CheckBoxUI
     * CheckBoxUI - javax.swing.plaf.metal.MetalCheckBoxUI
     * Checkbox.select
     * ColorChooser.background
     * ColorChooser.font
     * ColorChooser.foreground
     * ColorChooser.swatchesDefaultRecentColor
     * ColorChooser.swatchesRecentSwatchSize
     * ColorChooser.swatchesSwatchSize
     * ColorChooserUI
     * ColorChooserUI - javax.swing.plaf.basic.BasicColorChooserUI
     * ComboBox.ancestorInputMap
     * ComboBox.background
     * ComboBox.buttonBackground
     * ComboBox.buttonDarkShadow
     * ComboBox.buttonHighlight
     * ComboBox.buttonShadow
     * ComboBox.disabledBackground
     * ComboBox.disabledForeground
     * ComboBox.font
     * ComboBox.foreground
     * ComboBox.isEnterSelectablePopup
     * ComboBox.noActionOnKeyNavigation
     * ComboBox.selectionBackground
     * ComboBox.selectionForeground
     * ComboBox.timeFactor
     * ComboBoxUI
     * ComboBoxUI - javax.swing.plaf.metal.MetalComboBoxUI
     * Desktop.ancestorInputMap
     * Desktop.background
     * Desktop.minOnScreenInsets
     * DesktopIcon.background
     * DesktopIcon.border
     * DesktopIcon.font
     * DesktopIcon.foreground
     * DesktopIcon.width
     * DesktopIconUI
     * DesktopIconUI - javax.swing.plaf.metal.MetalDesktopIconUI
     * DesktopPaneUI
     * DesktopPaneUI - javax.swing.plaf.basic.BasicDesktopPaneUI
     * EditorPane.background
     * EditorPane.border
     * EditorPane.caretBlinkRate
     * EditorPane.caretForeground
     * EditorPane.focusInputMap
     * EditorPane.font
     * EditorPane.foreground
     * EditorPane.inactiveForeground
     * EditorPane.margin
     * EditorPane.selectionBackground
     * EditorPane.selectionForeground
     * EditorPaneUI
     * EditorPaneUI - javax.swing.plaf.basic.BasicEditorPaneUI
     * FileChooser.ancestorInputMap
     * FileChooser.detailsViewIcon
     * FileChooser.homeFolderIcon
     * FileChooser.listViewIcon
     * FileChooser.newFolderIcon
     * FileChooser.readOnly
     * FileChooser.upFolderIcon
     * FileChooser.useSystemExtensionHiding
     * FileChooser.usesSingleFilePane
     * FileChooserUI
     * FileChooserUI - javax.swing.plaf.metal.MetalFileChooserUI
     * FileView.computerIcon
     * FileView.directoryIcon
     * FileView.fileIcon
     * FileView.floppyDriveIcon
     * FileView.hardDriveIcon
     * For most of them, values will be null. Below 60 keys only have the valid
     * values.
     * FormattedTextField.background
     * FormattedTextField.border
     * FormattedTextField.caretBlinkRate
     * FormattedTextField.caretForeground
     * FormattedTextField.focusInputMap
     * FormattedTextField.font
     * FormattedTextField.foreground
     * FormattedTextField.inactiveBackground
     * FormattedTextField.inactiveForeground
     * FormattedTextField.margin
     * FormattedTextField.selectionBackground
     * FormattedTextField.selectionForeground
     * FormattedTextFieldUI
     * FormattedTextFieldUI - javax.swing.plaf.basic.BasicFormattedTextFieldUI
     * InternalFrame.activeTitleBackground
     * InternalFrame.activeTitleForeground
     * InternalFrame.activeTitleGradient
     * InternalFrame.border
     * InternalFrame.borderColor
     * InternalFrame.borderDarkShadow
     * InternalFrame.borderHighlight
     * InternalFrame.borderLight
     * InternalFrame.borderShadow
     * InternalFrame.closeIcon
     * InternalFrame.closeSound
     * InternalFrame.closeSound - sounds/FrameClose.wav
     * InternalFrame.icon
     * InternalFrame.iconifyIcon
     * InternalFrame.inactiveTitleBackground
     * InternalFrame.inactiveTitleForeground
     * InternalFrame.maximizeIcon
     * InternalFrame.maximizeSound
     * InternalFrame.maximizeSound - sounds/FrameMaximize.wav
     * InternalFrame.minimizeIcon
     * InternalFrame.minimizeSound
     * InternalFrame.minimizeSound - sounds/FrameMinimize.wav
     * InternalFrame.optionDialogBorder
     * InternalFrame.paletteBorder
     * InternalFrame.paletteCloseIcon
     * InternalFrame.paletteTitleHeight
     * InternalFrame.restoreDownSound
     * InternalFrame.restoreDownSound - sounds/FrameRestoreDown.wav
     * InternalFrame.restoreUpSound
     * InternalFrame.restoreUpSound - sounds/FrameRestoreUp.wav
     * InternalFrame.titleFont
     * InternalFrameTitlePane.closeButtonOpacity
     * InternalFrameTitlePane.iconifyButtonOpacity
     * InternalFrameTitlePane.maximizeButtonOpacity
     * InternalFrameUI
     * InternalFrameUI - javax.swing.plaf.metal.MetalInternalFrameUI
     * Label.background
     * Label.disabledForeground
     * Label.disabledShadow
     * Label.font
     * Label.foreground
     * LabelUI
     * LabelUI - javax.swing.plaf.metal.MetalLabelUI
     * List.background
     * List.cellRenderer
     * List.dropCellBackground
     * List.dropLineColor
     * List.focusCellHighlightBorder
     * List.focusInputMap
     * List.focusInputMap.RightToLeft
     * List.font
     * List.foreground
     * List.noFocusBorder
     * List.selectionBackground
     * List.selectionForeground
     * List.timeFactor
     * ListUI
     * ListUI - javax.swing.plaf.basic.BasicListUI
     * Menu.acceleratorFont
     * Menu.acceleratorForeground
     * Menu.acceleratorSelectionForeground
     * Menu.arrowIcon
     * Menu.background
     * Menu.border
     * Menu.borderPainted
     * Menu.cancelMode
     * Menu.cancelMode - hideLastSubmenu
     * Menu.checkIcon
     * Menu.crossMenuMnemonic
     * Menu.disabledForeground
     * Menu.font
     * Menu.foreground
     * Menu.margin
     * Menu.menuPopupOffsetX
     * Menu.menuPopupOffsetY
     * Menu.opaque
     * Menu.preserveTopLevelSelection
     * Menu.selectionBackground
     * Menu.selectionForeground
     * Menu.shortcutKeys
     * Menu.submenuPopupOffsetX
     * Menu.submenuPopupOffsetY
     * MenuBar.background
     * MenuBar.border
     * MenuBar.borderColor
     * MenuBar.font
     * MenuBar.foreground
     * MenuBar.gradient
     * MenuBar.highlight
     * MenuBar.shadow
     * MenuBar.windowBindings
     * MenuBarUI
     * MenuBarUI - javax.swing.plaf.metal.MetalMenuBarUI
     * MenuItem.acceleratorDelimiter
     * MenuItem.acceleratorDelimiter - -
     * MenuItem.acceleratorFont
     * MenuItem.acceleratorForeground
     * MenuItem.acceleratorSelectionForeground
     * MenuItem.arrowIcon
     * MenuItem.background
     * MenuItem.border
     * MenuItem.borderPainted
     * MenuItem.checkIcon
     * MenuItem.commandSound
     * MenuItem.commandSound - sounds/MenuItemCommand.wav
     * MenuItem.disabledForeground
     * MenuItem.font
     * MenuItem.foreground
     * MenuItem.margin
     * MenuItem.selectionBackground
     * MenuItem.selectionForeground
     * MenuItemUI
     * MenuItemUI - javax.swing.plaf.basic.BasicMenuItemUI
     * MenuUI
     * MenuUI - javax.swing.plaf.basic.BasicMenuUI
     * OptionPane.background
     * OptionPane.border
     * OptionPane.buttonAreaBorder
     * OptionPane.buttonClickThreshhold
     * OptionPane.errorDialog.border.background
     * OptionPane.errorDialog.titlePane.background
     * OptionPane.errorDialog.titlePane.foreground
     * OptionPane.errorDialog.titlePane.shadow
     * OptionPane.errorIcon
     * OptionPane.errorSound
     * OptionPane.errorSound - sounds/OptionPaneError.wav
     * OptionPane.font
     * OptionPane.foreground
     * OptionPane.informationIcon
     * OptionPane.informationSound
     * OptionPane.informationSound - sounds/OptionPaneInformation.wav
     * OptionPane.messageAreaBorder
     * OptionPane.messageForeground
     * OptionPane.minimumSize
     * OptionPane.questionDialog.border.background
     * OptionPane.questionDialog.titlePane.background
     * OptionPane.questionDialog.titlePane.foreground
     * OptionPane.questionDialog.titlePane.shadow
     * OptionPane.questionIcon
     * OptionPane.questionSound
     * OptionPane.questionSound - sounds/OptionPaneQuestion.wav
     * OptionPane.warningDialog.border.background
     * OptionPane.warningDialog.titlePane.background
     * OptionPane.warningDialog.titlePane.foreground
     * OptionPane.warningDialog.titlePane.shadow
     * OptionPane.warningIcon
     * OptionPane.warningSound
     * OptionPane.warningSound - sounds/OptionPaneWarning.wav
     * OptionPane.windowBindings
     * OptionPaneUI
     * OptionPaneUI - javax.swing.plaf.basic.BasicOptionPaneUI
     * Panel.background
     * Panel.font
     * Panel.foreground
     * PanelUI
     * PanelUI - javax.swing.plaf.basic.BasicPanelUI
     * PasswordField.background
     * PasswordField.border
     * PasswordField.caretBlinkRate
     * PasswordField.caretForeground
     * PasswordField.echoChar
     * PasswordField.focusInputMap
     * PasswordField.font
     * PasswordField.foreground
     * PasswordField.inactiveBackground
     * PasswordField.inactiveForeground
     * PasswordField.margin
     * PasswordField.selectionBackground
     * PasswordField.selectionForeground
     * PasswordFieldUI
     * PasswordFieldUI - javax.swing.plaf.basic.BasicPasswordFieldUI
     * PopupMenu.background
     * PopupMenu.border
     * PopupMenu.consumeEventOnClose
     * PopupMenu.font
     * PopupMenu.foreground
     * PopupMenu.popupSound
     * PopupMenu.popupSound - sounds/PopupMenuPopup.wav
     * PopupMenu.selectedWindowInputMapBindings
     * PopupMenu.selectedWindowInputMapBindings.RightToLeft
     * PopupMenuSeparatorUI
     * PopupMenuSeparatorUI - javax.swing.plaf.metal.MetalPopupMenuSeparatorUI
     * PopupMenuUI
     * PopupMenuUI - javax.swing.plaf.basic.BasicPopupMenuUI
     * ProgressBar.background
     * ProgressBar.border
     * ProgressBar.cellLength
     * ProgressBar.cellSpacing
     * ProgressBar.cycleTime
     * ProgressBar.font
     * ProgressBar.foreground
     * ProgressBar.horizontalSize
     * ProgressBar.repaintInterval
     * ProgressBar.selectionBackground
     * ProgressBar.selectionForeground
     * ProgressBar.verticalSize
     * ProgressBarUI
     * ProgressBarUI - javax.swing.plaf.metal.MetalProgressBarUI
     * RadioButton.background
     * RadioButton.border
     * RadioButton.darkShadow
     * RadioButton.disabledText
     * RadioButton.focus
     * RadioButton.focusInputMap
     * RadioButton.font
     * RadioButton.foreground
     * RadioButton.gradient
     * RadioButton.highlight
     * RadioButton.icon
     * RadioButton.light
     * RadioButton.margin
     * RadioButton.rollover
     * RadioButton.select
     * RadioButton.shadow
     * RadioButton.textIconGap
     * RadioButton.textShiftOffset
     * RadioButton.totalInsets
     * RadioButtonMenuItem.acceleratorFont
     * RadioButtonMenuItem.acceleratorForeground
     * RadioButtonMenuItem.acceleratorSelectionForeground
     * RadioButtonMenuItem.arrowIcon
     * RadioButtonMenuItem.background
     * RadioButtonMenuItem.border
     * RadioButtonMenuItem.borderPainted
     * RadioButtonMenuItem.checkIcon
     * RadioButtonMenuItem.commandSound
     * RadioButtonMenuItem.commandSound - sounds/MenuItemCommand.wav
     * RadioButtonMenuItem.disabledForeground
     * RadioButtonMenuItem.font
     * RadioButtonMenuItem.foreground
     * RadioButtonMenuItem.gradient
     * RadioButtonMenuItem.margin
     * RadioButtonMenuItem.selectionBackground
     * RadioButtonMenuItem.selectionForeground
     * RadioButtonMenuItemUI
     * RadioButtonMenuItemUI - javax.swing.plaf.basic.BasicRadioButtonMenuItemUI
     * RadioButtonUI
     * RadioButtonUI - javax.swing.plaf.metal.MetalRadioButtonUI
     * RootPane.ancestorInputMap
     * RootPane.colorChooserDialogBorder
     * RootPane.defaultButtonWindowKeyBindings
     * RootPane.errorDialogBorder
     * RootPane.fileChooserDialogBorder
     * RootPane.frameBorder
     * RootPane.informationDialogBorder
     * RootPane.plainDialogBorder
     * RootPane.questionDialogBorder
     * RootPane.warningDialogBorder
     * RootPaneUI
     * RootPaneUI - javax.swing.plaf.metal.MetalRootPaneUI
     * ScrollBar.allowsAbsolutePositioning
     * ScrollBar.ancestorInputMap
     * ScrollBar.ancestorInputMap.RightToLeft
     * ScrollBar.background
     * ScrollBar.darkShadow
     * ScrollBar.foreground
     * ScrollBar.gradient
     * ScrollBar.highlight
     * ScrollBar.maximumThumbSize
     * ScrollBar.minimumThumbSize
     * ScrollBar.shadow
     * ScrollBar.thumb
     * ScrollBar.thumbDarkShadow
     * ScrollBar.thumbHighlight
     * ScrollBar.thumbShadow
     * ScrollBar.track
     * ScrollBar.trackHighlight
     * ScrollBar.width
     * ScrollBarUI
     * ScrollBarUI - javax.swing.plaf.metal.MetalScrollBarUI
     * ScrollPane.ancestorInputMap
     * ScrollPane.ancestorInputMap.RightToLeft
     * ScrollPane.background
     * ScrollPane.border
     * ScrollPane.font
     * ScrollPane.foreground
     * ScrollPaneUI
     * ScrollPaneUI - javax.swing.plaf.metal.MetalScrollPaneUI
     * Separator.background
     * Separator.foreground
     * Separator.highlight
     * Separator.shadow
     * SeparatorUI
     * SeparatorUI - javax.swing.plaf.metal.MetalSeparatorUI
     * Slider.altTrackColor
     * Slider.background
     * Slider.focus
     * Slider.focusGradient
     * Slider.focusInputMap
     * Slider.focusInputMap.RightToLeft
     * Slider.focusInsets
     * Slider.font
     * Slider.foreground
     * Slider.gradient
     * Slider.highlight
     * Slider.horizontalSize
     * Slider.horizontalThumbIcon
     * Slider.majorTickLength
     * Slider.minimumHorizontalSize
     * Slider.minimumVerticalSize
     * Slider.onlyLeftMouseButtonDrag
     * Slider.shadow
     * Slider.tickColor
     * Slider.trackWidth
     * Slider.verticalSize
     * Slider.verticalThumbIcon
     * SliderUI
     * SliderUI - javax.swing.plaf.metal.MetalSliderUI
     * Spinner.ancestorInputMap
     * Spinner.arrowButtonBorder
     * Spinner.arrowButtonInsets
     * Spinner.arrowButtonSize
     * Spinner.background
     * Spinner.border
     * Spinner.editorAlignment
     * Spinner.editorBorderPainted
     * Spinner.font
     * Spinner.foreground
     * SpinnerUI
     * SpinnerUI - javax.swing.plaf.basic.BasicSpinnerUI
     * SplitPane.ancestorInputMap
     * SplitPane.background
     * SplitPane.border
     * SplitPane.centerOneTouchButtons
     * SplitPane.darkShadow
     * SplitPane.dividerFocusColor
     * SplitPane.dividerSize
     * SplitPane.highlight
     * SplitPane.oneTouchButtonsOpaque
     * SplitPane.shadow
     * SplitPaneDivider.border
     * SplitPaneDivider.draggingColor
     * SplitPaneUI
     * SplitPaneUI - javax.swing.plaf.metal.MetalSplitPaneUI
     * TabbedPane.ancestorInputMap
     * TabbedPane.background
     * TabbedPane.borderHightlightColor
     * TabbedPane.contentAreaColor
     * TabbedPane.contentBorderInsets
     * TabbedPane.contentOpaque
     * TabbedPane.darkShadow
     * TabbedPane.focus
     * TabbedPane.focusInputMap
     * TabbedPane.font
     * TabbedPane.foreground
     * TabbedPane.highlight
     * TabbedPane.labelShift
     * TabbedPane.light
     * TabbedPane.selectHighlight
     * TabbedPane.selected
     * TabbedPane.selectedLabelShift
     * TabbedPane.selectedTabPadInsets
     * TabbedPane.selectionFollowsFocus
     * TabbedPane.shadow
     * TabbedPane.tabAreaBackground
     * TabbedPane.tabAreaInsets
     * TabbedPane.tabInsets
     * TabbedPane.tabRunOverlay
     * TabbedPane.tabsOpaque
     * TabbedPane.tabsOverlapBorder
     * TabbedPane.textIconGap
     * TabbedPane.unselectedBackground
     * TabbedPaneUI
     * TabbedPaneUI - javax.swing.plaf.metal.MetalTabbedPaneUI
     * Table.ancestorInputMap
     * Table.ancestorInputMap.RightToLeft
     * Table.ascendingSortIcon
     * Table.background
     * Table.descendingSortIcon
     * Table.dropCellBackground
     * Table.dropLineColor
     * Table.dropLineShortColor
     * Table.focusCellBackground
     * Table.focusCellForeground
     * Table.focusCellHighlightBorder
     * Table.font
     * Table.foreground
     * Table.gridColor
     * Table.scrollPaneBorder
     * Table.selectionBackground
     * Table.selectionForeground
     * Table.sortIconColor
     * TableHeader.ancestorInputMap
     * TableHeader.background
     * TableHeader.cellBorder
     * TableHeader.focusCellBackground
     * TableHeader.font
     * TableHeader.foreground
     * TableHeaderUI
     * TableHeaderUI - javax.swing.plaf.basic.BasicTableHeaderUI
     * TableUI
     * TableUI - javax.swing.plaf.basic.BasicTableUI
     * TextArea.background
     * TextArea.border
     * TextArea.caretBlinkRate
     * TextArea.caretForeground
     * TextArea.focusInputMap
     * TextArea.font
     * TextArea.foreground
     * TextArea.inactiveForeground
     * TextArea.margin
     * TextArea.selectionBackground
     * TextArea.selectionForeground
     * TextAreaUI
     * TextAreaUI - javax.swing.plaf.basic.BasicTextAreaUI
     * TextField.background
     * TextField.border
     * TextField.caretBlinkRate
     * TextField.caretForeground
     * TextField.darkShadow
     * TextField.focusInputMap
     * TextField.font
     * TextField.foreground
     * TextField.highlight
     * TextField.inactiveBackground
     * TextField.inactiveForeground
     * TextField.light
     * TextField.margin
     * TextField.selectionBackground
     * TextField.selectionForeground
     * TextField.shadow
     * TextFieldUI
     * TextFieldUI - javax.swing.plaf.metal.MetalTextFieldUI
     * TextPane.background
     * TextPane.border
     * TextPane.caretBlinkRate
     * TextPane.caretForeground
     * TextPane.focusInputMap
     * TextPane.font
     * TextPane.foreground
     * TextPane.inactiveForeground
     * TextPane.margin
     * TextPane.selectionBackground
     * TextPane.selectionForeground
     * TextPaneUI
     * TextPaneUI - javax.swing.plaf.basic.BasicTextPaneUI
     * TitledBorder.border
     * TitledBorder.font
     * TitledBorder.titleColor
     * ToggleButton.background
     * ToggleButton.border
     * ToggleButton.darkShadow
     * ToggleButton.disabledText
     * ToggleButton.focus
     * ToggleButton.focusInputMap
     * ToggleButton.font
     * ToggleButton.foreground
     * ToggleButton.gradient
     * ToggleButton.highlight
     * ToggleButton.light
     * ToggleButton.margin
     * ToggleButton.select
     * ToggleButton.shadow
     * ToggleButton.textIconGap
     * ToggleButton.textShiftOffset
     * ToggleButtonUI
     * ToggleButtonUI - javax.swing.plaf.metal.MetalToggleButtonUI
     * ToolBar.ancestorInputMap
     * ToolBar.background
     * ToolBar.border
     * ToolBar.borderColor
     * ToolBar.darkShadow
     * ToolBar.dockingBackground
     * ToolBar.dockingForeground
     * ToolBar.floatingBackground
     * ToolBar.floatingForeground
     * ToolBar.font
     * ToolBar.foreground
     * ToolBar.highlight
     * ToolBar.isRollover
     * ToolBar.light
     * ToolBar.nonrolloverBorder
     * ToolBar.rolloverBorder
     * ToolBar.separatorSize
     * ToolBar.shadow
     * ToolBarSeparatorUI
     * ToolBarSeparatorUI - javax.swing.plaf.basic.BasicToolBarSeparatorUI
     * ToolBarUI
     * ToolBarUI - javax.swing.plaf.metal.MetalToolBarUI
     * ToolTip.background
     * ToolTip.backgroundInactive
     * ToolTip.border
     * ToolTip.borderInactive
     * ToolTip.font
     * ToolTip.foreground
     * ToolTip.foregroundInactive
     * ToolTip.hideAccelerator
     * ToolTipManager.enableToolTipMode
     * ToolTipManager.enableToolTipMode - activeApplication
     * ToolTipUI
     * ToolTipUI - javax.swing.plaf.metal.MetalToolTipUI
     * Tree.ancestorInputMap
     * Tree.background
     * Tree.changeSelectionWithFocus
     * Tree.closedIcon
     * Tree.collapsedIcon
     * Tree.drawsFocusBorderAroundIcon
     * Tree.dropCellBackground
     * Tree.dropLineColor
     * Tree.editorBorder
     * Tree.expandedIcon
     * Tree.focusInputMap
     * Tree.focusInputMap.RightToLeft
     * Tree.font
     * Tree.foreground
     * Tree.hash
     * Tree.leafIcon
     * Tree.leftChildIndent
     * Tree.line
     * Tree.lineTypeDashed
     * Tree.openIcon
     * Tree.paintLines
     * Tree.rightChildIndent
     * Tree.rowHeight
     * Tree.scrollsOnExpand
     * Tree.selectionBackground
     * Tree.selectionBorderColor
     * Tree.selectionForeground
     * Tree.textBackground
     * Tree.textForeground
     * Tree.timeFactor
     * TreeUI
     * TreeUI - javax.swing.plaf.metal.MetalTreeUI
     * UIManager keys and values list:
     * Viewport.background
     * Viewport.font
     * Viewport.foreground
     * ViewportUI
     * ViewportUI - javax.swing.plaf.basic.BasicViewportUI
     * activeCaption
     * activeCaptionBorder
     * activeCaptionText
     * control
     * controlDkShadow
     * controlHighlight
     * controlLtHighlight
     * controlShadow
     * controlText
     * desktop
     * html.missingImage
     * html.pendingImage
     * inactiveCaption
     * inactiveCaptionBorder
     * inactiveCaptionText
     * info
     * infoText
     * menu
     * menuText
     * scrollbar
     * text
     * textHighlight
     * textHighlightText
     * textInactiveText
     * textText
     * window
     * windowBorder
     * windowText
     */
}
