import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Козадаев_АС on 28.11.2017.
 */
public class conn {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn(String dbFullName) throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFullName);

        System.out.println("База Подключена!");
    }

    public static List<String> getTableCollNames(Statement statmt, String tableName){
        List<String> result = new LinkedList<String>();
        try {
            resSet = statmt.executeQuery("pragma table_info(" + tableName + ");");
            while(resSet.next()){
                result.add(resSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getAllTableNamesInDb(Statement statmt){
        List<String> result = new LinkedList<String>();
        try {
            resSet = statmt.executeQuery("select * from sqlite_master\n" +
                    "where type = 'table'");
            while(resSet.next()){
                result.add(resSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> ReadDBRows(Statement statmt, String tableName) throws ClassNotFoundException, SQLException {
        List<String> result = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        List<String> collNames = getTableCollNames(statmt, tableName);
        String tmpString = "";
        for (String s : collNames) {
            tmpString += s + "\t";
            sb.append(s.contains("time")? "datetime("+s+" / 1000000 + (strftime('%s', '1601-01-01')), 'unixepoch') " + s + ", ": s + ", ");
        }
        String s = sb.toString();
        s = s.substring(0, s.length() - 2);
        ResultSet resSet = statmt.executeQuery("select " + s + " " + " from " + tableName);
        result.add(tmpString);
        while(resSet.next()){
            sb.setLength(0);
            for (String s1 : collNames){
                sb.append(resSet.getString(s1) + "\t");
            }//sb.append("\n");
            result.add(sb.toString());
        }
        return result;
    }

    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        List<String> dbs = getAllTableNamesInDb(statmt);
        for (String dbName: dbs){
            List<String> dbRows = ReadDBRows(statmt, dbName);

            try{
                FileWriter writer = new FileWriter(dbName + ".csv", false);
                for (String s: dbRows){
                    writer.write(s);
                    // запись по символам
                    writer.append('\n');
                }
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
            System.out.println(String.format("Таблица %s выведена", dbName));
        }

    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        statmt.close();
        conn.close();
        resSet.close();
        System.out.println("Соединения закрыты");
    }



}
