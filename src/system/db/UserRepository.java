package system.db;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import system.models.*;

import javax.swing.text.html.HTMLDocument;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public User authenticate(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try(Connection conn = Database.getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        }
    }
    public boolean createUser(String fullname, String username, String password, User.userRole role) throws SQLException {
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = Database.getConn()) {
            PreparedStatement check = conn.prepareStatement(checkQuery);
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                String insertQuery = "INSERT INTO users (full_name, username, password_hash, role) VALUES (?, ?, ?, ?)";
                PreparedStatement insert = conn.prepareStatement(insertQuery);
                insert.setString(1, fullname);
                insert.setString(2, username);
                insert.setString(3, password);
                insert.setString(4, role.name());
                insert.executeUpdate();
                return true;
            }
        }
    }
    public List<Patient> getPatient() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'PATIENT' ORDER BY full_name";
        try (Connection conn = Database.getConn();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                patients.add(new Patient (
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("username")));
            }
        }
        return patients;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String fullname = rs.getString("full_name");
        String username = rs.getString("username");
        String role = rs.getString("role");
        return switch (role) {
            case "PHARMACIST" -> new Pharmacist(id, fullname, username);
            case "DOCTOR" -> new Doctor(id, fullname, username);
            case "PATIENT" -> new Patient(id, fullname, username);
            default -> throw new IllegalArgumentException("Unsupported role " + role);
        };
    }
}
