import java.awt.*;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ThemeBasicScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        this.trackColor = new Color(30, 30, 30);
        this.thumbColor = new Color(45, 45, 45);
        this.scrollBarWidth = 10;
        this.incrGap = 0;
        this.decrGap = 0;
    }

    protected JButton createZeroButton() {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
}
