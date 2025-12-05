package system.db;

import system.models.Medicine;
import system.models.Prescription;
import system.models.PrescriptionItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepository {

    public Prescription findByToken (String token) throws SQLException {
        String query = "SELECT p.*, u.full_name AS patient_name, d.full_name AS doctor_name " +
                "FROM prescriptions p " +
                "JOIN users u ON p.patient_id = u.id " +
                "JOIN users d ON p.doctor_id = d.id " +
                "WHERE secure_token = ? ";
        try (Connection conn = Database.getConn(); PreparedStatement ps = conn.prepareStatement(query);){
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                List<PrescriptionItem> items = loadItems(conn, rs.getInt("id"));
                return new Prescription(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getString("secure_token"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("notes"),
                        items
                );
            }
        }
        return null;
    }
    public List<Prescription> findByPatient(int patientID) throws SQLException {
        List<Prescription> prescriptions = new ArrayList<>();
        String query = "SELECT * FROM prescriptions WHERE patient_id = ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, patientID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                List<PrescriptionItem> items = loadItems(conn,rs.getInt("id"));
                prescriptions.add(new Prescription(
                        rs.getInt("id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getString("secure_token"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("notes"),
                        items
                        ));
            }
        }
        return prescriptions;
    }
    public int createPrescription (Connection conn, int doctorID, int patientID, String secureToken, String notes) throws SQLException {
        String insertQuery = "INSERT INTO prescriptions (doctor_id, patient_id, secure_token, notes) VALUES (?, ?, ?, ?) ";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doctorID);
            ps.setInt(2, patientID);
            ps.setString(3, secureToken);
            ps.setString(4, notes);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
                throw new SQLException ("Failed to create prescription!");
        }
    }

    public void insertItems(Connection conn, int prescriptionID, List<PrescriptionItem> items) throws SQLException {
        String insertQuery = "INSERT INTO prescription_items (prescription_id, medicine_id, quantity, dosage_instructions) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            for (PrescriptionItem item : items) {
                ps.setInt(1, prescriptionID);
                ps.setInt(2, item.getMedicineId());
                ps.setInt(3, item.getQuantity());
                ps.setString(4, item.getDosageInstructions());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    public List<PrescriptionItem> loadItems(Connection conn, int prescriptionId) throws SQLException {
        List<PrescriptionItem> items = new ArrayList<>();
        String query = "SELECT pi.*, m.name FROM prescription_items pi " +
                "JOIN medicines m ON pi.medicine_id = m.id " +
                "WHERE prescription_id = ? ";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, prescriptionId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            items.add (new PrescriptionItem(
                    rs.getInt("medicine_id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getString("dosage_instructions")));
        }
        return items;
    }
}
