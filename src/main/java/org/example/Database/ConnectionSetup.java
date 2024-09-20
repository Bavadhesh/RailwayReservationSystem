package org.example.Database;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSetup {
    private static DataSource datasource;
       public static Connection connection;


    public static Connection setConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/RailwayReservationSystem", "root", "root1234");
        return connection;
    }

    public static Connection getConnection() throws SQLException, NamingException {
          InitialContext ctx = new InitialContext();
          datasource = (DataSource) ctx.lookup("java:comp/env/RailwayReservationSystem");
          return datasource.getConnection();
    }

}
