package system.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame{
    private final JTextField txt_username = new JTextField(15);
    private final JPasswordField txt_password = new JPasswordField(15);
    private final JLabel lbl_username = new JLabel("Username");
    private final JLabel lbl_password = new JLabel("Password");

    JPanel form = new JPanel(new GridBagLayout());

}
