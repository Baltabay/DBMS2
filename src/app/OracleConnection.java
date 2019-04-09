package app;

import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;


public class OracleConnection {
    private String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:senay";
    private String userid;
    private String password;
    private Connection conn;

    public OracleConnection(String userid, String password )throws SQLException {
        this.userid = userid;
        this.password = password;
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(jdbcUrl);
        conn = ds.getConnection(this.userid, this.password);
    }

    public String getUserid() {
        return userid;
    }

    public void changeUser(String userid, String password) throws SQLException {
        this.userid = userid;
        this.password = password;
        OracleDataSource ds = new OracleDataSource();
        ds.setURL(jdbcUrl);
        conn = ds.getConnection(this.userid, this.password);
    }

    public Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) throws SQLException {
        OracleConnection handler = new OracleConnection("\"Project\"", "87013345999b");
        Connection connection = handler.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from COUNTRIES");

        while (rs.next()) System.out.println(rs.getInt(1) + " " + rs.getString(2));

        connection.close();
    }
}
