package system.ui;

import system.db.MedicineRepository;
import system.db.UserRepository;
import system.models.Doctor;
import system.models.Medicine;
import system.models.Patient;
import system.models.PrescriptionItem;
import system.utility.PrescriptionService;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboard extends JFrame {
    private final Doctor doctor;
    private final UserRepository userRepo = new UserRepository();
    private final MedicineRepository medicineRepo = new MedicineRepository();
    private final PrescriptionService prescriptionService = new PrescriptionService();

    private final JComboBox<Patient> cmbPatient = new JComboBox<>();
    private final JComboBox<Medicine> cmbMedicine = new JComboBox<>();
    private final JSpinner spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1 , 100, 1));
    private final JTextField txtDosage = new JTextField(20);
    private final JTextArea txtNotes = new JTextArea(3, 20);
    private final DefaultListModel<PrescriptionItem> itemsModel = new DefaultListModel<>();
    private final JLabel lblStatus = new JLabel(" ");

    public DoctorDashboard(Doctor doctor) {
        this.doctor = doctor;
        setTitle("Doctor Workspace - " + doctor.getFullname());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUi();
        loadPatients();
        loadMedicine();
        setResizable(false);
    }

    private void initUi() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10 , 10));
        topPanel.add(new JLabel("Select Patient: "));
        topPanel.add(cmbPatient);
        topPanel.add(new JLabel("Notes (optional): "));
        topPanel.add(new JScrollPane(txtNotes));
        main.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(buildMedicinePanel());
        centerPanel.add(buildItemsPanel());
        main.add(centerPanel, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save Prescription");
        btnSave.addActionListener(e -> savePrescription());
        lblStatus.setForeground(Color.DARK_GRAY);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.add(btnSave, BorderLayout.CENTER);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);
        main.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel buildMedicinePanel() {
        JPanel medicinePanel = new JPanel();
        medicinePanel.setLayout(new BoxLayout(medicinePanel, BoxLayout.Y_AXIS));
        medicinePanel.setBorder(BorderFactory.createTitledBorder("Add Medicine"));

        medicinePanel.add(new JLabel("Medicine: "));
        medicinePanel.add(cmbMedicine);

        medicinePanel.add(Box.createVerticalStrut(5));
        medicinePanel.add(new JLabel("Quantity: "));
        medicinePanel.add(spnQuantity);

        medicinePanel.add(Box.createVerticalStrut(5));
        medicinePanel.add(new JLabel("Dosage Instructions: "));
        medicinePanel.add(txtDosage);

        JButton addMeds = new JButton("Add Medicine");
        addMeds.addActionListener(e -> addMedicine());
        medicinePanel.add(Box.createVerticalStrut(10));
        medicinePanel.add(addMeds);

        return medicinePanel;
    }

    private JPanel buildItemsPanel() {
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Prescription Items"));
        JList<PrescriptionItem> itemsList = new JList<>(itemsModel);
        itemsPanel.add(new JScrollPane(itemsList), BorderLayout.CENTER);

        JButton btnRemove = new JButton("Remove Item");
        btnRemove.addActionListener(e -> {
            int index = itemsList.getSelectedIndex();
            if (index >= 0) {
                itemsModel.remove(index);
            }
        });
        itemsPanel.add(btnRemove, BorderLayout.SOUTH);

        return itemsPanel;
    }
    private void loadPatients() {
        try {
            DefaultComboBoxModel<Patient> patientModel = new DefaultComboBoxModel<>();
            for (Patient patient : userRepo.getPatient()) {
                patientModel.addElement(patient);
            }
            cmbPatient.setModel(patientModel);
        } catch (SQLException ex) {
            showStatus("Failed to load Patients: " + ex.getMessage(), true);
        }
    }
    private void loadMedicine() {
        try {
            DefaultComboBoxModel<Medicine> medicineModel = new DefaultComboBoxModel<>();
            for (Medicine medicine : medicineRepo.getAll()) {
                medicineModel.addElement(medicine);
            }
            cmbMedicine.setModel(medicineModel);
        } catch (SQLException ex) {
            showStatus("Failed to load Medicines: " + ex.getMessage(), true);
        }
    }
    private void addMedicine() {
        Medicine medicine = (Medicine) cmbMedicine.getSelectedItem();
        if (medicine == null) {
            showStatus("Select at least one medicine!!", true);
            return;
        }
        if (medicine.getStock() == 0) {
            showStatus(medicine.getName() + " is out of stock.", true);
        }
        int quantity = (Integer) spnQuantity.getValue();
        if (quantity <= 0 || quantity > medicine.getStock()) {
            showStatus("Quantity must be between 1 and " + medicine.getStock(), true);
        }
        String dosage = txtDosage.getText().trim();
        itemsModel.addElement(new PrescriptionItem(medicine.getId(), medicine.getName(), quantity, dosage));
        txtDosage.setText(" ");
        showStatus("Added " + medicine.getName(), false);
        //System.out.println(itemsModel);
    }

    private void showStatus(String message, boolean error) {
        lblStatus.setForeground(error ? Color.RED : Color.DARK_GRAY);
        lblStatus.setText(message);
    }
    private void savePrescription() {
        Patient patient = (Patient) cmbPatient.getSelectedItem();
        if (patient == null) {
            showStatus("Select a patient!", true);
        }
        if (itemsModel.isEmpty()) {
            showStatus("Add at least one medicine!", true);
        }
        List<PrescriptionItem> items = new ArrayList<>();
        for (int i = 0; i < itemsModel.getSize(); i++ ) {
            items.add(itemsModel.getElementAt(i));
        }
        try {
            String token = prescriptionService.createPrescription(doctor, patient.getID(), items, txtNotes.getText());
            JOptionPane.showMessageDialog(this, "Prescription Saved! \n Token Number: " + token);
            itemsModel.clear();
            txtNotes.setText(" ");
            loadMedicine();
            showStatus("Prescription Saved Successfully! token: " + token, false);
        } catch (Exception ex) {
            showStatus("Failed to Save Prescription! Error: " + ex.getMessage(), true);
        }
    }
}
