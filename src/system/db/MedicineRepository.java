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
