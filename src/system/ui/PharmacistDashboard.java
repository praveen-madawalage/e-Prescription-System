package system.ui;

import system.db.MedicineRepository;
import system.db.PrescriptionRepository;
import system.models.Medicine;
import system.models.Pharmacist;
import system.models.Prescription;
import system.models.PrescriptionItem;
import system.utility.PrescriptionService;

import javax.swing.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class PharmacistDashboard extends JFrame {
    private final Pharmacist pharmacist;
    private final PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
    private final MedicineRepository medicineRepo = new MedicineRepository();
    private final PrescriptionService prescriptionService = new PrescriptionService();

    private final JTextField txtToken = new JTextField(10);
    private final JTextArea txtDetails = new JTextArea();
    private Prescription currentPrescription;
    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private List<Medicine> medicines;

    public PharmacistDashboard(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
        setTitle("Pharmacist Console - " + pharmacist.getFullname());
        setSize(900, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
    }
    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel prescriptionPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Secure Token: "));
        topPanel.add(txtToken);
        JButton btnSearch = new JButton("Search Token");
        btnSearch.addActionListener(e -> lookup());
        topPanel.add(btnSearch);
        prescriptionPanel.add(topPanel, BorderLayout.NORTH);

        txtDetails.setEditable(false);
        txtDetails.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        prescriptionPanel.add(new JScrollPane(txtDetails), BorderLayout.CENTER);

        JButton btnDispense = new JButton("Dispense");
        btnDispense.addActionListener(e -> dispense());
        prescriptionPanel.add(btnDispense, BorderLayout.SOUTH);

        tabbedPane.addTab("Prescription Lookup", prescriptionPanel);

        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryModel = new DefaultTableModel(new String[] {"Medicine ID", "Name", "Description", "Stock", "Unit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        inventoryTable = new JTable(inventoryModel);
        inventoryTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JTextField()));
        inventoryPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel inventoryButtons = new JPanel();
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadInventory());

        JButton btnUpdate = new JButton("Update Stock");
        btnUpdate.addActionListener(e -> updateInventory());

        JButton btnAdd = new JButton("Add Medicine");
        btnAdd.addActionListener(e -> addNewMedicine());

        inventoryButtons.add(btnRefresh);
        inventoryButtons.add(btnUpdate);
        inventoryButtons.add(btnAdd);
        inventoryPanel.add(inventoryButtons, BorderLayout.SOUTH);

        JLabel lblInventory = new JLabel("Medicine Inventory Panel");
        inventoryPanel.add(lblInventory, BorderLayout.NORTH);

        tabbedPane.addTab("Inventory Management", inventoryPanel);
        add(tabbedPane);
        loadInventory();
    }

    private void loadInventory() {
        try {
            medicines = medicineRepo.getAll();
            inventoryModel.setRowCount(0);
            for (Medicine med : medicines) {
                inventoryModel.addRow(new Object[] {
                        med.getId(),
                        med.getName(),
                        med.getDescription(),
                        med.getStock(),
                        med.getUnit()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Loading Inventory: " + ex.getMessage());
        }
    }

    private void updateInventory () {
        int row = inventoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a Medicine to Update.");
            return;
        }
        try {
            int medicineId = (Integer) inventoryTable.getValueAt(row, 0);
            int newStock = Integer.parseInt(inventoryTable.getValueAt(row, 3).toString());
            if (newStock < 0) {
                JOptionPane.showMessageDialog(this, "Stock cannot be negative!");
                return;
            }
            medicineRepo.updateStock(medicineId, newStock);
            JOptionPane.showMessageDialog(this, "Stock updated successfully!");
            loadInventory();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid stock value! Enter a number.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Updating Stock: " + ex.getMessage());
        }
    }
    private void lookup() {
        String token = txtToken.getText().trim();
        if (token.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a token!");
            return;
        }
        try {
            currentPrescription = prescriptionService.getByToken(token);
            if (currentPrescription == null) {
                txtDetails.setText("Details not found for token: " + token);
                return;
            }
            renderDetails();
        } catch (SQLException ex) {
            txtDetails.setText("Error: " + ex.getMessage());
        }
    }
    private void renderDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("Token: ").append(currentPrescription.getSecureToken()).append('\n');
        builder.append("Status: ").append(currentPrescription.getStatus()).append('\n');
        builder.append("Items: \n");
        for (PrescriptionItem i : currentPrescription.getItems()) {
            builder.append(" - ").append(i.getMedicineName())
                    .append(" x").append(i.getQuantity())
                    .append('\n');
        }
        txtDetails.setText(builder.toString());
    }
    private void dispense() {
        String token = txtToken.getText().trim();
        if (currentPrescription == null) {
            JOptionPane.showMessageDialog(this, "Lookup a prescription first!");
            return;
        }
        if (!token.equals(currentPrescription.getSecureToken())) {
            JOptionPane.showMessageDialog(this, "Token in search box does not match the loaded details.\nPlease click 'Search Token' again.");
            return;
        }
        if ("DISPENSED".equals(currentPrescription.getStatus())) {
            JOptionPane.showMessageDialog(this, "Prescription already dispensed");
            return;
        }
        try {
            prescriptionService.dispense(currentPrescription.getSecureToken());
            JOptionPane.showMessageDialog(this, "Prescription dispensed successfully");
            lookup();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void addNewMedicine() {
        JDialog addMedsDialog = new JDialog(this, "-Add new Medicine-", true);
        addMedsDialog.setSize(600, 280);
        addMedsDialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets (5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtName = new JTextField(25);
        JTextField txtDescription = new JTextField(25);
        JTextField txtUnit = new JTextField(25);
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        JLabel lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.RED);

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Medicine Name: "), gbc);
        gbc.gridx = 1;
        form.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Description: "), gbc);
        gbc.gridx = 1;
        form.add(txtDescription, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Initial Stock: "), gbc);
        gbc.gridx = 1;
        form.add(stockSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Unit (tablet / syrup .etc)"), gbc);
        gbc.gridx = 1;
        form.add(txtUnit, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnCreate = new JButton("Add Medicine");
        btnCreate.addActionListener(e -> {
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();
            int stock = (Integer) stockSpinner.getValue();
            String unit = txtUnit.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a name!");
                return;
            }
            if (unit.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Unit cannot be empty!");
                return;
            }
            try {
                medicineRepo.createMedicine(name, description, stock, unit);
                JOptionPane.showMessageDialog(this, "Medicine Created Successfully");
                addMedsDialog.dispose();
                loadInventory();
            } catch (SQLException ex) {
                lblStatus.setText("Error Saving Medicine: " + ex.getMessage());
            }
        });
        form.add(btnCreate, gbc);

        gbc.gridy = 5;
        form.add(lblStatus, gbc);
        addMedsDialog.add(form);
        addMedsDialog.setVisible(true);
    }
}
