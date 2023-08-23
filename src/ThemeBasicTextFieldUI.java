package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class ThemeBasicTextFieldUI extends BasicTextFieldUI {
    private static final ThemeBasicTextFieldUI INSTANCE = new ThemeBasicTextFieldUI();

    public static ComponentUI createUI(JComponent c) {
        return INSTANCE;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        JTextField textField = (JTextField) getComponent();
        textField.setBackground(new Color(32, 32, 32));
        textField.setCaretColor(new Color(200, 200, 200));
    }
}