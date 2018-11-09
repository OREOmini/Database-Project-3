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
//    public static void main( String[ ] args )
//
//    {
//
//        while (true) {Scanner sc = new Scanner( System.in );
//        System.out.print( "Please enter a string : " );
//
//        System.out.print( "Your input is :" + sc.next( ) );
//    }
//
//
//
//    }
//
//
//
//}
    public static void main(String[] args) {
        Connection con;
        DB_SETTING setting = new DB_SETTING();
        String driver = setting.DRIVER;
        //这里我的数据库是qcl
        String url = setting.URL;
        String user = setting.USER;
        String password = setting.PASSWORD;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
            }
            Statement statement = con.createStatement();
            String sql = "select * from appeared_in;";//我的表格叫home
            ResultSet resultSet = statement.executeQuery(sql);


            String name, movie;
            String[][] stars = new String[100][100];
            int i = 0;

            while (resultSet.next()) {
                name = resultSet.getString("star");
                stars[i][0] = name;
                movie = resultSet.getString("movie");
                stars[i++][1] = movie;
//                System.out.println("姓名：" + name);
            }
            String[] t = {"star", "movie"};
            System.out.println(new TextTable(t, stars));
            resultSet.close();
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动没有安装");

        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("数据库连接失败");
        }
    }
}