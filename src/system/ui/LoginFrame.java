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
        setSize(600,400);
        setResizable(false);
        initUI();
    }
    public void initUI() {
        Color bgLightBlue = new Color(240, 248, 255); // AliceBlue
        Color bgWhite = Color.WHITE;
        Color btnBlue = new Color(176, 224, 230);     // PowderBlue

        JPanel mainPanel = new JPanel(new GridLayout(1,2));

        JPanel leftPanel = new JPanel(new GridBagLayout()); // GridBag centers content easily
        leftPanel.setBackground(bgLightBlue);
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.insets = new Insets(10, 10, 20, 10);

        String imagePath = "./resources/logoFinal.png";

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel(new ImageIcon(scaledImage));
        if (originalIcon.getIconWidth() == -1) {
            lblImage.setText("[Logo Image Not Found]");
        }
        leftPanel.add(lblImage, gbcLeft);

        gbcLeft.gridy++; // Move down
        JLabel lblBrand = new JLabel("PharmaLink");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 28)); // Bold and Large
        lblBrand.setForeground(Color.DARK_GRAY);
        leftPanel.add(lblBrand, gbcLeft);
        mainPanel.add(leftPanel);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(bgWhite);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        lbl_username.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(lbl_username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        lbl_password.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(lbl_password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch input
        txt_username.setPreferredSize(new Dimension(150, 25));
        rightPanel.add(txt_username, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txt_password.setPreferredSize(new Dimension(150, 25));
        rightPanel.add(txt_password, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(bgWhite);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(btnBlue);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.addActionListener(e -> attemptLogin());

        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setBackground(btnBlue);
        btnSignup.setOpaque(true);
        btnSignup.setBorderPainted(false);
        btnSignup.addActionListener(e -> openSignUp());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignup);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(buttonPanel, gbc);

        mainPanel.add(rightPanel);

        add(mainPanel, BorderLayout.CENTER);
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
        cmbRole.addItem(User.userRole.PATIENT);
        //cmbRole.addItem(User.userRole.DOCTOR);
        //cmbRole.addItem(User.userRole.PHARMACIST);

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
                    System.out.println("Account created successfully!.");
                } else {
                    lblStatus.setText("Username exists! Please use a different username!");
                }
            } catch (SQLException | IllegalArgumentException exception) {
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
