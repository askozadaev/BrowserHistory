import java.sql.SQLException;

/**
 * Created by Козадаев_АС on 28.11.2017.
 */
public class db {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
    conn.Conn("History.db");
    conn.ReadDB();
    conn.CloseDB();
}
}