package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.*;

class ThemeBasicComboBoxUI extends BasicComboBoxUI {

    protected JButton createArrowButton() {
        BasicArrowButton arrowButton = new BasicArrowButton(BasicArrowButton.SOUTH, new Color(32, 32, 32),
                null,
                new Color(130, 130, 130),
                null);

        arrowButton.setBorder(new LineBorder(new Color(32, 32, 32), 1));
        return arrowButton;
    }
}