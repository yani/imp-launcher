package src;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;

class BackgroundHighlightMouseAdapter extends MouseAdapter {

    private Color originalBackgroundColor;

    public BackgroundHighlightMouseAdapter() {
        super();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Component source = e.getComponent();
        this.originalBackgroundColor = source.getBackground();
        if (source.isEnabled()) {
            source.setBackground(this.originalBackgroundColor.brighter());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Component source = e.getComponent();
        source.setBackground(this.originalBackgroundColor);
    }
}