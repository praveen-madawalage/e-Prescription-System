package system;

import system.ui.LoginFrame;

import javax.swing.*;

public class testing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
