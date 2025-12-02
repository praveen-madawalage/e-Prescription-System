package system.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame{
    private final JTextField txt_username = new JTextField(15);
    private final JPasswordField txt_password = new JPasswordField(15);
    private final JLabel lbl_username = new JLabel("Username: ");
    private final JLabel lbl_password = new JLabel("Password: ");

    public LoginFrame() {
        setTitle("e-Prescription System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,240);
        initUI();
    }
    public void initUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(lbl_username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(lbl_password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(txt_password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(txt_username, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        JButton btnSignup = new JButton("Sign Up");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignup);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(buttonPanel, gbc);

        add(form, BorderLayout.CENTER);
    }
}
