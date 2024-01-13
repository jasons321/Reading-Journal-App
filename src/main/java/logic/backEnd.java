package logic;

import java.sql.*;

public class backEnd {
    Connection conn; 
    public backEnd() {}
    
    public Connection getConnection() { 
         try { 
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("", "", "");
            return conn;
        }
        catch (Exception error) { 
            System.out.println(error);
        }
        return null;
    }
}
