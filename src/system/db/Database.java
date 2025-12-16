package system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    //replace xxx//localhost:3306/xxx with respective prot number.
    private static final String url = "jdbc:mysql://localhost:3306/hospital_sys?useSSL=false&allowPublicKeyRetrieval=true";
    //change the username and password to match your credentials.
    private static final String username = "root";
    private static final String password = "";

    private Database() {}

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}