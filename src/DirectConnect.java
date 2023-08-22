package src;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DirectConnect extends JDialog {

    private Main mainWindow;

    public DirectConnect(Main mainWindow) {
        this.mainWindow = mainWindow;

        this.setTitle("Direct Connect");
        this.getContentPane().setBackground(new Color(50, 50, 50));
        this.setSize(320, 205);
        this.setResizable(false);
        this.setModal(true);
        this.setLocationRelativeTo(this.mainWindow);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ////////////////////////////////////////////////////////////////////////

        // Create a main panel for the index content
        RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS, 5);
        rl.setAlignment(Component.LEFT_ALIGNMENT);
        JPanel panel = new JPanel(rl);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(panel);

        ////////////////////////////////////////////////////////////////////////

        // 'Host IP' label
        JLabel hostIpLabel = new JLabel("<html>Host IP:</html>");
        hostIpLabel.setBorder(new EmptyBorder(12, 5, 15, 5));
        hostIpLabel.setForeground(new Color(230, 230, 230));
        hostIpLabel.setPreferredSize(new Dimension(380, 24));
        panel.add(hostIpLabel);

        // IP input
        JTextField ipField = new JTextField(26);
        ipField.setMargin(new Insets(7, 7, 7, 7));
        ipField.setPreferredSize(new Dimension(200, 30));
        panel.add(ipField);

        // 'ENET' label
        JLabel enetLabel = new JLabel("<html>Port 5556 is forced. ENET only.</html>");
        enetLabel.setBorder(new EmptyBorder(12, 5, 15, 5));
        enetLabel.setForeground(new Color(70, 70, 70));
        enetLabel.setPreferredSize(new Dimension(380, 24));
        panel.add(enetLabel);

        ////////////////////////////////////////////////////////////////////////

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // "Close" button
        JButton closeButton = GuiUtil.createDefaultButton("Cancel");
        closeButton.setPreferredSize(new Dimension(150, 50));
        closeButton.setBackground(new Color(45, 45, 45));
        closeButton.addActionListener(e -> this.dispose());
        closeButton.setEnabled(true);
        bottomPanel.add(closeButton, BorderLayout.PAGE_END);

        // "Connect" button
        JButton connectButton = GuiUtil.createDefaultButton("Connect");
        connectButton.setPreferredSize(new Dimension(150, 50));
        connectButton.setEnabled(true);
        connectButton.addActionListener(e -> {

            // Check for valid IP
            if (ipField.getText().isEmpty() || !DirectConnect.isValidIp(ipField.getText())) {
                JOptionPane.showMessageDialog(this,
                        "Invalid IP!",
                        "Direct Connect error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create argument list
            ArrayList<String> extraArguments = new ArrayList<String>();
            extraArguments.add("-connect");
            extraArguments.add(ipField.getText() + ":5556");

            // Start game
            new Thread(() -> (new GameLauncher(this.mainWindow,
                    Main.runOptions)).startGame(extraArguments)).start();

            // Close this window
            this.dispose();

        });
        bottomPanel.add(connectButton, BorderLayout.PAGE_END);

        ////////////////////////////////////////////////////////////////////////

        this.setVisible(true);
    }

    public static boolean isValidIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

}
