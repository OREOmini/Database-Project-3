import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//import com.mysql.cj.jdbc.Driver;

/**
 * Created by qcl on 2017/11/18.
 * 数据库连接
 */
public class DB {
    // CONSTANT VARIABLE
    private static final DB_SETTING setting = new DB_SETTING();
    private static final String DRIVER = setting.DRIVER;
    private static final String URL = setting.URL;
    private static final String USER = setting.USER;
    private static final String PASSWORD = setting.PASSWORD;


    private Connection  con;    // connect to database
    private String      username;   // store username, in order to retrieve data from
                                // database in the future




    public void LoginMenu() {

    }
    public void StudentMenu() {

    }
    public void StudentNextOperation() {

    }
    public void TranscriptShow() {

    }
    public void TranscriptNextOperation() {

    }
    // the following method need stored procedures.
    public void Enroll() {

    }
    // the following method need stored procedures and triggers.
    public void WithDraw() {

    }
    public void PersonalDetail() {

    }


    public void ShowCurrentTime() {

    }
    public static void main(String[] args) {
        DB db = new DB();


        Connection con;
        DB_SETTING setting = new DB_SETTING();

        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            if (!con.isClosed()) {
                System.out.println("Connection Success...");
            }
            Statement statement = con.createStatement();
            String sql = "select * from faculty;";//我的表格叫home
            ResultSet resultSet = statement.executeQuery(sql);


            String name, movie;
            String[][] stars = new String[100][100];
            int i = 0;

            while (resultSet.next()) {
                name = resultSet.getString("id");
                stars[i][0] = name;
                movie = resultSet.getString("name");
                stars[i][1] = movie;
//                System.out.println("姓名：" + name);
                i++;
            }
            String[] t = {"id", "name"};
            System.out.println(new TextTable(t, stars));
            resultSet.close();
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");

        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
    }
}