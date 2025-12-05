package system.db;

import system.models.Medicine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicineRepository {

    public List<Medicine> getAll() throws SQLException{
        List<Medicine> meds = new ArrayList<>();
        String query = "SELECT * FROM medicines ORDER BY name";
        try (Connection conn = Database.getConn();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()){
                 while (rs.next()) {
                     meds.add(map(rs));
                 }
        }
        return meds;
    }
    public void updateStock(int medicineId, int newStock) throws SQLException {
        String query = "UPDATE medicines SET stock = ? WHERE id = ?";
        try (Connection conn = Database.getConn();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, newStock);
            ps.setInt(2, medicineId);
            ps.executeUpdate();
        }
    }
    public Medicine createMedicine(String name, String description, int stock, String unit) throws SQLException {
        String query = "INSERT INTO medicines (name, description, stock, unit) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, stock);
            ps.setString(4, unit);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    return new Medicine(id, name, description, stock, unit);
                }
            }
        }
        throw new SQLException("Failed to create medicine!");
    }
    private Medicine map(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("stock"),
                rs.getString("unit")
        );
    }
}
