package src;

import java.awt.*;
import javax.swing.*;

class ThemeComboBoxRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Set the font to non-bold
        Font font = getFont();
        this.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

        return this;

    }
}