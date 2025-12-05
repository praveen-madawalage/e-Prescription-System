package system.utility;

import system.db.Database;
import system.db.PrescriptionRepository;
import system.models.Doctor;
import system.models.Prescription;
import system.models.PrescriptionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepo;

    public PrescriptionService() {
        this.prescriptionRepo = new PrescriptionRepository();
    }
    public Prescription getByToken(String token) throws SQLException {
        return prescriptionRepo.findByToken(token);
    }

    public List<Prescription> getPatientHistory(int patientID) throws SQLException {
        return prescriptionRepo.findByPatient(patientID);
    }
    private void validateStock(Connection conn, List<PrescriptionItem> items) throws SQLException {
        String query = "SELECT stock FROM medicines WHERE id = ? FOR UPDATE ";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (PrescriptionItem i : items) {
                ps.setInt(1, i.getMedicineId());
                ResultSet rs = ps.executeQuery();
                if (! rs.next()) {
                    throw new SQLException("Medicine " + i.getMedicineName() + " out of stock!");
                } else {
                    int availableStock = rs.getInt("stock");
                    if (availableStock < i.getQuantity()) {
                        throw new SQLException(i.getMedicineName() + " only has " + availableStock + " units left!");
                    }
                }
            }
        }
    }
    private int lockPrescription(Connection conn, String token) throws SQLException{
        String cleanToken = token.trim();
        String query = "SELECT id, status FROM prescriptions WHERE secure_token = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, cleanToken);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Prescription not found!");
            }
            if ("DISPENSED".equals(rs.getString("status"))) {
                throw new SQLException("Prescription already dispensed!");
            }
            return rs.getInt("id");
        }
    }
    private void deductInventory(Connection conn, List<PrescriptionItem> items) throws SQLException {
        String updateQuery = "UPDATE medicines SET stock = stock - ? WHERE id = ? AND stock >= ? ";
        try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            for (PrescriptionItem i : items) {
                ps.setInt(1, i.getQuantity());
                ps.setInt(2, i.getMedicineId());
                ps.setInt(3, i.getQuantity());
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Not enough stock: " + i.getMedicineName());
                }
            }
        }
    }
    private void updateStatus (Connection conn, int prescriptionID) throws SQLException {
        String query = "UPDATE prescriptions SET status = 'DISPENSED' WHERE id = ? ";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, prescriptionID);
            ps.executeUpdate();
        }
    }
    public String createPrescription (Doctor doctor, int patientId,
                                      List<PrescriptionItem> items, String notes) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Prescriptions cannot be empty!");
        }
        String secureToken = SecureTokenGenerator.generateToken();
        try (Connection conn = Database.getConn()) {
            conn.setAutoCommit(false);
            try {
                validateStock(conn, items);
                int prescriptionID = prescriptionRepo.createPrescription(conn, doctor.getID(), patientId, secureToken, notes);
                prescriptionRepo.insertItems(conn, prescriptionID, items);
                deductInventory(conn, items);
                conn.commit();
                return secureToken;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public void dispense(String secureToken) throws SQLException {
        try (Connection conn = Database.getConn()) {
            conn.setAutoCommit(false);
            try {
                int prescriptionId = lockPrescription(conn, secureToken);
                List<PrescriptionItem> items = prescriptionRepo.loadItems(conn, prescriptionId);
                deductInventory(conn, items);
                updateStatus(conn, prescriptionId);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
