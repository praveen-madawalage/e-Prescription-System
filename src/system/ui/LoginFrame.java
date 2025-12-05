package system.ui;

import system.db.UserRepository;
import system.models.Doctor;
import system.models.Patient;
import system.models.Pharmacist;
import system.models.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame{
    private final JTextField txt_username = new JTextField(15);
    private final JPasswordField txt_password = new JPasswordField(15);
    private final JLabel lbl_username = new JLabel("Username: ");
    private final JLabel lbl_password = new JLabel("Password: ");
    //private final JLabel lbl_status = new JLabel();

    private final UserRepository userRepository = new UserRepository();

    public LoginFrame() {
        setTitle("e-Prescription System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(this);
        setSize(360,240);
        setResizable(false);
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
        form.add(txt_username, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(txt_password, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {attemptLogin();});
        JButton btnSignup = new JButton("Sign Up");
        btnSignup.addActionListener(e -> {openSignUp();});
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignup);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(buttonPanel, gbc);

        add(form, BorderLayout.CENTER);
    }
    public void openSignUp() {
        JDialog signupDialog = new JDialog(this, "Sign Up", true);
        signupDialog.setSize(400, 300);
        signupDialog.setResizable(false);
        signupDialog.setLocationRelativeTo(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Full Name: ");
        JTextField txtName = new JTextField(20);

        JLabel lblUsername = new JLabel("Username: ");
        JTextField txtUsername = new JTextField(20);

        JLabel lblPassword = new JLabel("Password: ");
        JPasswordField txtPassword = new JPasswordField(20);

        JLabel lblRole = new JLabel("Role: ");
        JComboBox<User.userRole> cmbRole = new JComboBox<>();
        cmbRole.addItem(null);
        for (User.userRole userRole : User.userRole.values()) {
            cmbRole.addItem(userRole);
        }
        JLabel lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.blue);

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(lblName, gbc);
        gbc.gridx = 1;
        form.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(lblUsername, gbc);
        gbc.gridx = 1;
        form.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(lblPassword, gbc);
        gbc.gridx = 1;
        form.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(lblRole, gbc);
        gbc.gridx = 1;
        form.add(cmbRole, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnCreateAcc = new JButton("Create Account");
        btnCreateAcc.addActionListener(e -> {
            String name = txtName.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            User.userRole role = (User.userRole) cmbRole.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
                lblStatus.setText("All fields are required!");
            }
            try {
                boolean createUser = userRepository.createUser(name, username, password, role);
                if (createUser) {
                    JOptionPane.showMessageDialog(signupDialog, "Account Created Successfully! Please Log in." );
                    signupDialog.dispose();
                } else {
                    lblStatus.setText("Username exists! Please use a different username!");
                }
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(signupDialog, "Error Occurred: " + exception.getMessage());
            }
        });
        form.add(btnCreateAcc, gbc);
        gbc.gridy = 5;
        form.add(lblStatus, gbc);

        signupDialog.add(form);
        signupDialog.setVisible(true);
    }
    public void attemptLogin() {
        try {
            User user = userRepository.authenticate(txt_username.getText(), new String (txt_password.getPassword()));
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid credentials!",
                        "" , JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            openDashboard(user);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error: " + e.getMessage(),
                    "", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void openDashboard (User user) {
        SwingUtilities.invokeLater(() -> {
                switch (user.getRole()) {
                    case DOCTOR -> new DoctorDashboard((Doctor) user).setVisible(true);
                    case PATIENT -> new PatientDashboard((Patient) user).setVisible(true);
                    case PHARMACIST -> new PharmacistDashboard((Pharmacist) user).setVisible(true);
                }
        }
        );
    }
}
