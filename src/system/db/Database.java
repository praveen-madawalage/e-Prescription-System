package system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    //static variables to make them only accessible within the class.
    private static final String url = "jdbc:mysql://localhost:3306/hospital_rx?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String username = "root";
    private static final String password = "";

    //to prevent users from creating a Database object.
    //we only need the getConn() method of the class.
    private Database() {}

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}