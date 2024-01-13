
import login.*;
import logic.backEnd;
import java.sql.*;
import welcome.*;

public class Journalapp {

    public static void main(String[] args) { 
        backEnd backEnd = new backEnd();
        Connection conn = backEnd.getConnection();
        loginPage loginUi = new loginPage(conn);
        //registerPage loginUi = new registerPage(conn);
        //welcomePage welcomeUi = new welcomePage(conn, "1");
        //addSection frame = new addSection(conn, 27);

    }
}
