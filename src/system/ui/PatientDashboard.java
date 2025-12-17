package system.ui;

import system.db.PrescriptionRepository;
import system.models.Patient;
import system.models.Prescription;
import system.models.PrescriptionItem;
import system.utility.PrescriptionService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientDashboard extends JFrame {
    private final Patient patient;
    private final PrescriptionRepository prescriptionRepo = new PrescriptionRepository();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final DefaultListModel<Prescription> historyModel = new DefaultListModel<>();
    private final JTextArea txtDetails = new JTextArea();

    public PatientDashboard(Patient patient) {
        this.patient = patient;
        setTitle("Patient View: " + patient.getFullname());
        setSize(360, 640);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        loadHistory();
    }
    private void initUI() {
        // Define Palette Colors
        Color bgLightBlue = new Color(240, 248, 255); // AliceBlue
        Color bgWhite = Color.WHITE;
        Color btnBlue = new Color(176, 224, 230); // Powder Blue

        setBackground(btnBlue);
        setLayout(new BorderLayout(5, 5));
        JLabel lblHeader = new JLabel("-Your Prescriptions-");
        lblHeader.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        lblHeader.setBackground(bgLightBlue);
        lblHeader.setOpaque(true);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        JList<Prescription> historyList = new JList<>(historyModel);
        historyList.setCellRenderer(new PrescriptionRenderer());
        historyList.setBackground(bgWhite);
        historyList.addListSelectionListener(e -> showDetails(historyList.getSelectedValue()));
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.getViewport().setBackground(bgWhite);
        add(scrollPane, BorderLayout.CENTER);

        txtDetails.setEditable(false);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setBackground(bgWhite);
        TitledBorder border = BorderFactory.createTitledBorder("Prescription Details.");
        border.setTitleColor(Color.DARK_GRAY);
        txtDetails.setBorder(border);

        add(txtDetails, BorderLayout.SOUTH);
    }
    private void loadHistory() {
        historyModel.clear();
        try {
            List<Prescription> prescriptions = prescriptionService.getPatientHistory(patient.getID());
            prescriptions.forEach(historyModel::addElement);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + e.getMessage());
        }
    }
    private void showDetails(Prescription prescription) {
        if (prescription == null) {
            txtDetails.setText("");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, HH:mm");
        StringBuilder builder = new StringBuilder();
        builder.append("Status: ").append(prescription.getStatus()).append('\n');
        builder.append("Token: ").append(prescription.getSecureToken()).append('\n');
        builder.append("Items: \n");
        for (PrescriptionItem i : prescription.getItems()) {
            builder.append(" * ").append(i.getMedicineName())
                    .append(" x ").append(i.getQuantity())
                    .append("\n");
        }
        txtDetails.setText(builder.toString());
    }
    private static class PrescriptionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Prescription prescription) {
                String label = prescription.getSecureToken() + " - " + prescription.getStatus();
                setText(label);
                if ("PENDING".equals(prescription.getStatus()) && !isSelected) {
                    setBackground(new Color(255, 255, 200));
                }
            }
            return component;
        }
    }
}
