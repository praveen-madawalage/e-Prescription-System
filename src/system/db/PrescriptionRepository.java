package system.db;

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
    private List<PrescriptionItem> loadItems(Connection conn, int prescriptionId) throws SQLException {
        List<PrescriptionItem> items = new ArrayList<>();
        String query = "SELECT pi.*, m.name FROM prescription_items pi";
        return null;
    }
}
