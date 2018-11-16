import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static javax.swing.text.html.HTML.Tag.HEAD;
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

    enum State{
        LOGIN, STUDENTMENU, TRANSCRIPT, PERSONALDETAIL, COURSE, ENROLL, WITHDRAW, PERSONALDETAILS
    }


    private Connection  con;    // connect to database
    private String      studentID;   // store username, in order to retrieve data from database in the future
    private String year;
    private String mon;
    private String quarter;

    DB() {
        DB_SETTING setting = new DB_SETTING();
        studentID = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        LocalDate todayDate = LocalDate.now();
//        System.out.println("今天的日期："+todayDate);
        String day = todayDate.toString();
        String[] tmp = day.split("-"); // format: xxxx-xx-xx
        year = tmp[0];
        mon = tmp[1];
        switch (mon) {
            case "1" : quarter = "Q2"; break;
            case "2" : quarter = "Q2"; break;
            case "3" : quarter = "Q2"; break;
            case "4" : quarter = "Q3"; break;
            case "5" : quarter = "Q3"; break;
            case "6" : quarter = "Q3"; break;
            case "7" : quarter = "Q4"; break;
            case "8" : quarter = "Q4"; break;
            case "9" : quarter = "Q1"; break;
            case "10" : quarter = "Q1"; break;
            case "11" : quarter = "Q1"; break;
            case "12" : quarter = "Q1"; break;
        }
    }

    public void connectDB() {
        DB_SETTING setting = new DB_SETTING();

        try {
            Class.forName(DRIVER);
            if (!con.isClosed()) {
                System.out.println("Connection Success...");
            }
            Statement statement = con.createStatement();
            String sql = "select * from classroom;";
            ResultSet resultSet = statement.executeQuery(sql);


            String name, movie;
            String[][] stars = new String[100][100];
            int i = 0;

            while (resultSet.next()) {
                name = resultSet.getString("ClassroomId");
                stars[i][0] = name;
                movie = resultSet.getString("Type");
                stars[i++][1] = movie;
//                System.out.println("姓名：" + name);
                i++;
            }
            String[] t = {"id", "type"};
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



    public boolean LoginMenu(String username, String password) {
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            String sql = "select * from student;";
            ResultSet resultSet = statement.executeQuery(sql);


            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String psw = resultSet.getString("password");
//                System.out.printf("usr = %s, pswd = %s", name, psw);
                if (name.equals(username) && password.equals(psw)) {
                    studentID = resultSet.getString("ID");
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        return false;
    }


    public void logout() {
        studentID = null;
    }
    public void StudentMenu() {
        String getCurrentCourseSql =   "SELECT UoSName FROM unitofstudy " +
                                    "WHERE UoSCode in(SELECT UoSCode FROM transcript " +
                                        "where  StudId =\"" + this.studentID +"\" and " +
                                                "Year =\"" + this.year + "\" and " +
                                                "Semester =\""+ this.quarter +"\");";
        List<List<String>> list = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(getCurrentCourseSql);


            while (resultSet.next()) {
                String coursename = resultSet.getString("UoSName");
//                System.out.println(coursename);
                List<String> temp = new ArrayList<>();
                temp.add(coursename);
                list.add(temp);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        System.out.println("+     YOU ARE IN STUDENT MENU NOW    +");
        System.out.println("++++++++++++++++++++++++++++++++++++++");
        System.out.println("This is your current quarter courses");

        showTable(list, new String[]{"Course Name"});
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

    public void printStudentMenu() {
        String[][] s = {{"Transcript","1"},
                { "Enroll", "2"},
                {"Withdraw", "3"},
                {"Personal Details", "4"},
                {"Logout", "5"}};
        String[] head = {"Operation", "Code"};
        System.out.println("-----------------------------");
        System.out.println(new TextTable(head, s));
    }
    public void showTable(List<List<String>> list, String[] head) {
        int l = list.size();
        String[][] strs = new String[l][];
//        System.out.println(list);
//        System.out.println("==================================");
//        System.out.println(Arrays.toString(list.toArray()));

        for (int i = 0; i < l; i++) {
            int size = list.get(i).size();
            strs[i] = new String[size]; //
            list.get(i).toArray(strs[i]);
        }
        System.out.println(new TextTable(head, strs));
    }

    public static void main(String[] args) {
        DB db = new DB();
        State state = State.LOGIN;

        Scanner sc = new Scanner(System.in);


        while (true) {
            switch (state) {
                case LOGIN:
                    System.out.print("Please enter your name：");
                    String userName = sc.nextLine();
                    System.out.print("Please enter your password：");
                    String password = sc.nextLine();
                    System.out.println(userName + password);

                    if (db.LoginMenu(userName, password)) {
                        state = State.STUDENTMENU;
                        System.out.println("login successfully...");
                    } else {
                        System.out.println("Login Error");
                    }

                    break;
                case STUDENTMENU:
                    db.StudentMenu();
                    LocalDate todayDate = LocalDate.now();
                    System.out.println("Today's Date："+todayDate);


                    db.printStudentMenu();
                    System.out.print("Please enter operation code: ");
                    String code = sc.nextLine();
                    switch (code) {
                        case "1": state = State.TRANSCRIPT; break;
                        case "2": state = State.ENROLL; break;
                        case "3": state = State.WITHDRAW; break;
                        case "4": state = State.PERSONALDETAIL; break;
                        case "5":
                            db.logout();
                            System.out.println("Logout successfully...");
                            state = State.LOGIN;
                            break;
                        default: System.out.println("Wrong code, please enter again.");
                    }
                    break;
                case COURSE:
                case ENROLL:
                case WITHDRAW:
                case TRANSCRIPT:
                case PERSONALDETAIL:
                case PERSONALDETAILS:
                default:
                    break;
            }
        }

    }
}