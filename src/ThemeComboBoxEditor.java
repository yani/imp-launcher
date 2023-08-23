package src;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ThemeComboBoxEditor implements ComboBoxEditor {
    JTextField textField;

    public ThemeComboBoxEditor() {
        this.textField = new JTextField();
        this.textField.setUI(new ThemeBasicTextFieldUI());
    }

    public Component getEditorComponent() {
        return textField;
    }

    public void setItem(Object anObject) {
        if (anObject != null) {
            this.textField.setText(anObject.toString());
        }
    }

    public Object getItem() {
        return this.textField.getText();
    }

    public void selectAll() {
        this.textField.selectAll();
    }

    public void addActionListener(ActionListener l) {
        this.textField.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        this.textField.removeActionListener(l);
    }

}