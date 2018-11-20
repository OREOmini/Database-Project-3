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
        LOGIN, STUDENTMENU, TRANSCRIPT, PERSONALDETAIL, ENROLL, WITHDRAW
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
                String name = resultSet.getString("id");
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

    public void TranscriptShow() {
        // same as studentmenu but without year and quarter.
        String uoSCode, courseYear, courseSemester, grade;
        String getTranscriptSql =   "SELECT Year,Semester,UoSCode,Grade FROM transcript " +
                "where StudId =\"" + this.studentID +"\"";
        List<List<String>> list = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(getTranscriptSql);


            while (resultSet.next()) {
                uoSCode = resultSet.getString("UoSCode");
                courseYear = resultSet.getString("Year");
                courseSemester = resultSet.getString("Semester");
                grade = resultSet.getString("Grade");

                List<String> temp = new ArrayList<>();
                temp.add(uoSCode);
                temp.add(courseYear);
                temp.add(courseSemester);
                temp.add(grade);
                list.add(temp);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        String[] head = {"UoSCode", "Year", "Semester", "Grade"};
        showTable(list, head);
    }
    public void ShowCourseDetail(String courseNum) {
        List<String> list = new ArrayList<>();
        String title, courseYear, courseQuarter, enrolledStuNum, maxEnroll, lecturer, grade;
        list.add(courseNum);
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();

            // ------- title ------
            ResultSet resultSet = statement.executeQuery("SELECT UoSName " +
                    "FROM unitofstudy " +
                    "WHERE UoSCode=\""+courseNum+"\"");
            title = resultSet.next() ? resultSet.getString("UoSName") : null;
            list.add(title);

            // ------ courseYear ------
            resultSet = statement.executeQuery("SELECT Year " +
                    "FROM transcript " +
                    "WHERE UoSCode=\"" + courseNum + "\"" + " and StudId=\"" + studentID + "\"");
            courseYear = resultSet.next() ? resultSet.getString("year") : null;
            list.add(courseYear);

            // ------ courseQuarter ------
            resultSet = statement.executeQuery("SELECT Semester " +
                    "FROM transcript " +
                    "WHERE UoSCode=\"" + courseNum + "\"" + " and StudId=\"" + studentID + "\"");
            courseQuarter = resultSet.next() ? resultSet.getString("semester") : null;
            list.add(courseQuarter);

            // ------ enrolled student number ------
            resultSet = statement.executeQuery("SELECT Enrollment " +
                    "FROM uosoffering " +
                    "WHERE UoSCode=\"" + courseNum + "\"" +
                    " and year=\"" + courseYear + "\""+ " and Semester=\"" + courseQuarter + "\"");
            enrolledStuNum = resultSet.next() ? resultSet.getString("Enrollment") : null;
            list.add(enrolledStuNum);

            // ------ max enrolled student number ------
            resultSet = statement.executeQuery("SELECT MaxEnrollment " +
                    "FROM uosoffering " +
                    "WHERE UoSCode=\"" + courseNum + "\"" +
                    " and year=\"" + courseYear + "\""+ " and Semester=\"" + courseQuarter + "\"");
            maxEnroll = resultSet.next() ? resultSet.getString("MaxEnrollment") : null;
            list.add(maxEnroll);

            // ------ lecturer ------
            resultSet = statement.executeQuery("SELECT Name " +
                    "FROM faculty " +
                    "WHERE Id in (select InstructorId " +
                        "from uosoffering where UoSCode=\"" + courseNum + "\"" +
                        " and year=\"" + courseYear + "\""+ " and Semester=\"" + courseQuarter + "\")");
            lecturer = resultSet.next() ? resultSet.getString("Name") : null;
            list.add(lecturer);

            // ------ grade ------
            resultSet = statement.executeQuery("SELECT Grade " +
                    "FROM transcript " +
                    "WHERE UoSCode=\"" + courseNum + "\"" + " and StudId=\"" + studentID + "\"");
            grade = resultSet.next() ? resultSet.getString("Grade") : null;
            list.add(grade);

            List<List<String>> res = new ArrayList<>();
            res.add(list);
            String[] head = {"Course Code", "Title", "Year", "Quarter", "Enrolled Number",
                    "Maximum Enrollment", "Lecturer", "Grade"};

            showTable(res, head);
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
    }
    // the following method need stored procedures.

    public void Enroll(String courseCode) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter the year of the course: ");
        String inputYear = sc.nextLine();
        System.out.print("Please enter the semester of the course: ");
        String inputQuarter = sc.nextLine();

        String query = "CALL enrollCheck('"+studentID+"','"+courseCode+"','"+inputYear+"','"+inputQuarter+"', @message, @output);";
        String query2 = "select @message, @output;";
//        System.out.printf("query = %s\n", query);
        int output;
        String message;
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            statement.executeQuery(query);
            ResultSet resultSet = statement.executeQuery(query2);

            while (resultSet.next()) {
                output = resultSet.getInt("@output");
                message = resultSet.getString("@message");
                if ( output == 1 ) {
                    System.out.println("---- Enroll successfully ----");
                }
                else if ( output == 2){
                    System.out.println("---- The course you choose doesn't exist ----");
                }
                else if ( output == 3 ){
                    System.out.println("---- Sorry...the course is full ----");
                }
                else if ( output == 0 ) {
                    System.out.printf("You haven't meet the prerequisites of this(these) course(s): %s\n", message);

                }
                else {
                    System.out.println("Some error...");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }

    }

    public void ShowOfferedEnrollCourses() {
        List<List<String>> list = new ArrayList<>();
        String uoSCode, courseSemester, courseYear;
        String curYear = year, curSemester = quarter;
        String query = "select uoscode, semester, year from uosoffering u " +
                "where " +
                "NOT EXISTS\n" +
                "(SELECT t.UoSCode, t.Semester, t.Year \n" +
                "FROM transcript t \n" +
                "WHERE t.Studid = \"" + studentID + "\" and u.UoSCode = t.UoSCode and" +
                " u.Semester = t.Semester and u.Year ='"+curYear+"')" + " and " +
                "year = '"+curYear+"' and semester = '"+curSemester+"' ";
//        System.out.println(query);
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                uoSCode = resultSet.getString("UoSCode");
                courseYear = resultSet.getString("Year");
                courseSemester = resultSet.getString("Semester");

                List<String> temp = new ArrayList<>();
                temp.add(uoSCode);
                temp.add(courseYear);
                temp.add(courseSemester);
                list.add(temp);
            }
            if ( curSemester == "Q1" ) {
                curSemester = "Q2";
                curYear = (Integer.parseInt(curYear) + 1) + "";
            }
            else if ( curSemester == "Q4" ){
                curSemester = "Q1";
            }
            else if ( curSemester == "Q3"){
                curSemester = "Q4";
            }
            else {
                curSemester = "Q3";
            }
            query = "select uoscode, semester, year from uosoffering u " +
                    "where year = '"+curYear+"' and semester = '"+curSemester+"' and " +
                    "NOT EXISTS\n" +
                    "(SELECT t.UoSCode, t.Semester, t.Year \n" +
                    "FROM transcript t \n" +
                    "WHERE t.Studid = \"" + studentID + "\" and u.UoSCode = t.UoSCode and" +
                    " u.Semester = t.Semester and u.Year ='"+curYear+"')";
            resultSet = statement.executeQuery(query);
            while ( resultSet.next() ) {
                uoSCode = resultSet.getString("UoSCode");
                courseYear = resultSet.getString("Year");
                courseSemester = resultSet.getString("Semester");

                List<String> temp = new ArrayList<>();
                temp.add(uoSCode);
                temp.add(courseYear);
                temp.add(courseSemester);
                list.add(temp);
            }



        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        String[] head = {"UoSCode", "Year", "Semester"};
        showTable(list, head);

    }
    // the following method need stored procedures and triggers.
    public void WithDraw(String courseCode) {
        // TODO: withdraw constrain
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter the year of the course: ");
        String inputYear = sc.nextLine();
        System.out.print("Please enter the semester of the course: ");
        String inputQuarter = sc.nextLine();

        String query = "CALL withdrawCheck('"+studentID+"','"+courseCode+"','"+inputYear+"','"+inputQuarter+"', @message, @output);";
        String query2 = "select @message, @output;";
        int output;
        String message;
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            statement.executeQuery(query);
            ResultSet resultSet = statement.executeQuery(query2);

            while (resultSet.next()) {
                output = resultSet.getInt("@output");
                message = resultSet.getString("@message");
                if ( output == 1 ) {
                    System.out.println("---- Drop successfully ----");
                }
                else if ( output == 0 ) {
                    System.out.printf("%s\n", message);

                }
                else {
                    System.out.println("Some error...");
                }
            }

            resultSet = statement.executeQuery("Select @status;");
            while (resultSet.next()) {
                String status = resultSet.getString("@status");
                if (status.equals("below")) {
                    System.out.println("*** WARNING: The enrollment of this course is below 50% ***");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
    }
    public void ShowWithDrawTable() {
        List<List<String>> list = new ArrayList<>();
        String uoSCode, courseSemester, courseYear;
        String query = "SELECT distinct t.UosCode as UosCode, t.Semester as Semester, t.Year as Year \n" +
                "FROM transcript t\n" +
                "WHERE t.Studid = \"" + studentID +"\" and t.Grade is NULL";
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                uoSCode = resultSet.getString("UoSCode");
                courseYear = resultSet.getString("Year");
                courseSemester = resultSet.getString("Semester");

                List<String> temp = new ArrayList<>();
                temp.add(uoSCode);
                temp.add(courseYear);
                temp.add(courseSemester);
                list.add(temp);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        String[] head = {"UoSCode", "Year", "Semester"};
        showTable(list, head);
    }
    public void ShowPersonalDetail() {
        String query = "SELECT * FROM student where Id='" + studentID + "'";
        List<List<String>> list = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String psw = resultSet.getString("password");
                String address = resultSet.getString("address");
                List<String> temp = new ArrayList<>();
                temp.add(studentID);
                temp.add(name);
                temp.add(psw);
                temp.add(address);
                list.add(temp);
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
        String[] head = {"ID", "Name", "Password", "Address"};
        showTable(list, head);
    }
    public void ShowChangePersonalDetailMenu() {
        String[][] ops = {{"1", "Change password"}, {"2", "Change Address"}, {"3", "Go back to student menu"}};
        String[] head = {"Operation", "Code"};
        System.out.print(new TextTable(head, ops));
    }
    public void ChangePassword(String newPsw) {
        String update = "UPDATE student " +
                "SET student.Password = \""+ newPsw + "\" WHERE student.Id = \"" + studentID + "\"";

        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            statement.executeUpdate(update);

        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
    }
    public void ChangeAddress(String newAddress) {
        String update = "UPDATE student " +
                "SET student.Address = \""+ newAddress + "\" WHERE student.Id = \"" + studentID + "\"";

        try {
            Class.forName(DRIVER);
            if (con.isClosed()) {
                System.out.println("Connection is closed...");
            }
            Statement statement = con.createStatement();
            statement.executeUpdate(update);

        } catch (ClassNotFoundException e) {
            System.out.println("Drive not found, please check...");
        } catch (SQLException e) {
            System.out.println(e);
            System.out.println("Fail to connect to database...");
        }
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
                    System.out.print("Please enter your student ID：");
                    String userName = sc.nextLine();
                    System.out.print("Please enter your password：");
                    String password = sc.nextLine();
//                    System.out.println(userName + password);

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

                case ENROLL:
                    db.ShowOfferedEnrollCourses();
                    String enrollCourseCode = "";
                    System.out.print("Please enter the UoSCode to enroll a course/ Enter 'q' to go back: ");
                    enrollCourseCode = sc.nextLine();
                    while (!enrollCourseCode.equals("q")) {
                        db.Enroll(enrollCourseCode);
                        System.out.print("Please enter the UoSCode to enroll a course/ Enter 'q' to go back: ");
                        enrollCourseCode = sc.nextLine();
                    }
                    state = State.STUDENTMENU;
                    break;
                case WITHDRAW:
                    db.ShowWithDrawTable();
                    System.out.print("Please enter the UoSCode to withdraw a course/ Enter 'q' to go back: ");
                    String withdrawCourseCode = "";
                    withdrawCourseCode = sc.nextLine();
                    while (!withdrawCourseCode.equals("q")) {
                        db.WithDraw(withdrawCourseCode);
                        System.out.print("Please enter the UoSCode to withdraw a course/ Enter 'q' to go back: ");
                        withdrawCourseCode = sc.nextLine();
                    }
                    state = State.STUDENTMENU;
                    break;
                case TRANSCRIPT:
                    db.TranscriptShow();
                    System.out.print("Please enter the UoSCode to view the detail/ Enter 'q' to go back: ");
                    String courseCode = "";
                    courseCode = sc.nextLine();
                    while (!courseCode.equals("q")) {
                        System.out.println("Now showing the detail of course " + courseCode);
                        db.ShowCourseDetail(courseCode);
                        System.out.print("Please enter the UoSCode to view the detail/ Enter 'q' to go back: ");
                        courseCode = sc.nextLine();
                    }
                    state = State.STUDENTMENU;
                    break;
                case PERSONALDETAIL:
                    db.ShowPersonalDetail();
                    db.ShowChangePersonalDetailMenu();
                    System.out.print("Please enter operation code:");
                    String opCode = "";
                    opCode = sc.nextLine();
                    switch (opCode) {
                        case "1":
                            System.out.print("Please enter your new password: ");
                            String newPsw = sc.nextLine();
                            db.ChangePassword(newPsw);
                            System.out.println("Change password successfully!");
                            break;
                        case "2":
                            System.out.print("Please enter your new address: ");
                            String newAddress = sc.nextLine();
                            db.ChangeAddress(newAddress);
                            System.out.println("Change address successfully!");
                            break;
                        case "3":
                            state = State.STUDENTMENU;
                            break;
                        default: break;
                    }
                    break;
                default:
                    break;
            }
        }

    }
}