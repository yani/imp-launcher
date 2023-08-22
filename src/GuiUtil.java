package src;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuiUtil {

    public static JLabel createLink(String URL) {

        final String anchorLink = URL;

        JLabel label = new JLabel(anchorLink);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setForeground(new Color(0, 153, 255));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(anchorLink));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setText(anchorLink);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><a href='#' style='color: #0099ff'>" + anchorLink + "</a></html>");
            }
        });

        return label;
    }

    public static void turnComponentIntoLink(Component component, String URL) {

        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new BackgroundHighlightMouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static JButton createDefaultButton(String text) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new BackgroundHighlightMouseAdapter());

        return button;
    }
}
