import com.mysql.jdbc.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

class MyLibrary
{
    private Connection con;

    public MyLibrary()
    {
        try
        {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void sissueBook() throws SQLException
    {
        System.out.println("\033[H\033[2J");
        System.out.flush();

        System.out.print("Enter the book name which you want to issue: ");
        Scanner sc=new Scanner(System.in);
        String bname=sc.nextLine();
        PreparedStatement pstmt1 = con.prepareStatement("select `Book Name` from `books list` where `Book Name`=? and `Available Copies`>0");
        pstmt1.setString(1,bname);
        ResultSet rs= pstmt1.executeQuery();
         if(rs.next())
         {
             System.out.print("Enter Roll no: ");
             int roll = sc.nextInt();
             sc.nextLine();     //to read the \n.
             System.out.print("Enter Course: ");
             String course = sc.nextLine();
             PreparedStatement pstmt4 = con.prepareStatement("select `Book Name` from `Student` where `Roll No`=? and `Course`=?");
             pstmt4.setInt(1,roll);
             pstmt4.setString(2,course);
             ResultSet rs2=pstmt4.executeQuery();
             if(!rs2.next()) {
                 System.out.print("Enter Student Name: ");
                 String sname = sc.nextLine();
                 System.out.print("Enter Semester: ");
                 int sem = sc.nextInt();
                 sc.nextLine();     //to read the \n.
                 System.out.print("Enter Issued Date(yyyy-mm-dd): ");
                 String d1 = sc.nextLine();
                 PreparedStatement pstmt2 = con.prepareStatement("select `Book ID` from `books list` where `Book Name`=?");
                 pstmt2.setString(1, bname);
                 ResultSet rs1 = pstmt2.executeQuery();
                 int bid = 0;
                 if (rs1.next())
                     bid = rs1.getInt("Book ID");
                 PreparedStatement pstmt = con.prepareStatement("insert into Student (`Roll No`,Name,Course,Semester,`Book ID`,`Book Name`,`Issued Date`) values (?,?,?,?,?,?,?)");
                 pstmt.setInt(1, roll);
                 pstmt.setString(2, sname);
                 pstmt.setString(3, course);
                 pstmt.setInt(4, sem);
                 pstmt.setInt(5, bid);
                 pstmt.setString(6, bname);
                 pstmt.setString(7, d1);
                 pstmt.executeUpdate();
                 System.out.println("Book is issued Successfully. Please Return it within 15 days otherwise Fine Rs5 per day after 15 days.\n\n");
                 PreparedStatement pstmt3 = con.prepareStatement("update `books list` set `Available Copies`=`Available Copies`-1 where `Book Name`=?");
                 pstmt3.setString(1, bname);
                 pstmt3.executeUpdate();
             }
             else {
                    String bname1=rs2.getString("Book Name");
                 System.out.println("you have already issued a book "+bname1+" ! To issue a new book First return it.");
             }
         }
         else
             System.out.println("Sorry! This book is not available.\n\n");
        System.out.print("press 1 to Go back : ");
        if(sc.nextInt()==1)
            student();
    }

        public void sreturnBook() throws SQLException
        {
        System.out.println("\033[H\033[2J");
         System.out.flush();

        System.out.print("Enter Roll no: ");
        Scanner sc=new Scanner(System.in);
        int rollno=sc.nextInt();
        sc.nextLine();  // to read \n.
        System.out.print("Enter Course: ");
        String course=sc.nextLine();
       PreparedStatement pstmt4 = con.prepareStatement("select `Roll No` from `Student` where `Roll No`=? and `Course`=?");
       pstmt4.setInt(1,rollno);
       pstmt4.setString(2,course);
       ResultSet rs1=pstmt4.executeQuery();
       if(rs1.next()) {
           PreparedStatement pstmt2 = con.prepareStatement("select `Issued Date`,`Book Name` from `Student` where `Roll No`=? and `Course`=?");
           pstmt2.setInt(1, rollno);
           pstmt2.setString(2, course);
           ResultSet rs = pstmt2.executeQuery();
           String bname = "";
           String str = "";
           int daydif = 0;
           LocalDate rdate = LocalDate.now();
           if (rs.next()) {
               bname = rs.getString("Book Name");
               str = rs.getString("Issued Date");
               LocalDate idate = LocalDate.parse(str);
               daydif = (int) (ChronoUnit.DAYS.between(idate, rdate));
           }

           PreparedStatement pstmt3 = con.prepareStatement("update `books list` set `Available Copies`=`Available Copies`+1 where `Book Name`=?");
           pstmt3.setString(1, bname);
           pstmt3.executeUpdate();

           if (daydif > 15) {
               int days = daydif - 15;
               int fine = days * 5;
               System.out.println("Book is Returned " + days + " days Late so Submit fine Rs " + fine);
           } else
               System.out.println("Book Returned Successfully.");

           PreparedStatement pstmt1 = con.prepareStatement("delete  from `Student` where `Roll No`=? and `Course`=?");
           pstmt1.setInt(1, rollno);
           pstmt1.setString(2, course);
           pstmt1.executeUpdate();
       }
       else
       System.out.println("Sorry No Record Found !");
            System.out.print("press 1 to Go back : ");
            if(sc.nextInt()==1)
                student();
    }

    void addBook()throws SQLException
    {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        System.out.print("                    To Add a new book in Library\nEnter BookID: ");
        Scanner sc=new Scanner(System.in);
        int bid=sc.nextInt();
        sc.nextLine(); //to read \n.
        PreparedStatement pstmt= con.prepareStatement("select `Book ID` from `books list` where `Book ID`=?");
        pstmt.setInt(1,bid);
        ResultSet rs= pstmt.executeQuery();
        if(!rs.next())
        {
            System.out.print("Enter Book Name: ");
            String str=sc.nextLine();
            System.out.print("Enter Author Name: ");
            String str1=sc.nextLine();
            System.out.print("Enter no of Copies: ");
            int copies=sc.nextInt();
            PreparedStatement pstmt1= con.prepareStatement("insert into `books list` (`Book ID`,`Book Name`,`Author Name`,`Available Copies`) values (?,?,?,?)");
            pstmt1.setInt(1,bid);
            pstmt1.setString(2,str);
            pstmt1.setString(3,str1);
            pstmt1.setInt(4,copies);
            pstmt1.executeUpdate();
        }
        else
        {
            System.out.println("This BookID already exists !");
            System.out.print("press 1 to Go back : ");
            if(sc.nextInt()==1)
                teacher();
        }
    }

    void viewBookList() throws SQLException
    {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        Statement stmt= con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from `books list`");
        while (rs.next()) {
            System.out.print(rs.getInt("Book ID"));
            System.out.print("        ");
            System.out.print(rs.getString("Book Name"));
            System.out.print("       ");
            System.out.print(rs.getString("Author Name"));
            System.out.print("       ");
            System.out.print(rs.getInt("Available Copies"));
            System.out.println();
        }
        Scanner sc=new Scanner(System.in);
        System.out.print("press 1 to Go back : ");
        if(sc.nextInt()==1)
            teacher();
    }

    void tissueBook() throws SQLException {
        System.out.print("Enter the book name which you want to issue: ");
        Scanner sc=new Scanner(System.in);
        String bname=sc.nextLine();
        PreparedStatement pstmt1 = con.prepareStatement("select `Book Name` from `books list` where `Book Name`=? and `Available Copies`>0");
        pstmt1.setString(1,bname);
        ResultSet rs= pstmt1.executeQuery();
        if(rs.next())
        {
                System.out.print("Enter Teacher Name: ");
                String tname = sc.nextLine();
                System.out.print("Enter Issued Date(yyyy-mm-dd): ");
                String d1 = sc.nextLine();
                PreparedStatement pstmt2 = con.prepareStatement("select `Book ID` from `books list` where `Book Name`=?");
                pstmt2.setString(1, bname);
                ResultSet rs1 = pstmt2.executeQuery();
               int bid=0;
                if (rs1.next())
                    bid = rs1.getInt("Book ID");
                PreparedStatement pstmt = con.prepareStatement("insert into Teacher (Name,`Book ID`,`Book Name`,`Issued Date`) values (?,?,?,?)");
                pstmt.setString(1, tname);
                pstmt.setInt(2, bid);
                pstmt.setString(3, bname);
                pstmt.setString(4, d1);
                pstmt.executeUpdate();
                System.out.println("Book is issued Successfully.");
                PreparedStatement pstmt3 = con.prepareStatement("update `books list` set `Available Copies`=`Available Copies`-1 where `Book Name`=?");
                pstmt3.setString(1, bname);
                pstmt3.executeUpdate();
        }
        else
            System.out.println("Sorry! This book is not available.\n\n");
            System.out.print("press 1 to Go back : ");
            if (sc.nextInt() == 1)
                teacher();

    }

    void treturnBook() throws SQLException {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter Name: ");
        String tname=sc.nextLine();
        System.out.print("Enter Book Name: ");
        String bname=sc.nextLine();
        PreparedStatement pstmt4 = con.prepareStatement("select `Book Name` from `Teacher` where `Name`=? and `Book Name`=?");
        pstmt4.setString(1,tname);
        pstmt4.setString(2,bname);
        ResultSet rs1=pstmt4.executeQuery();
        if(rs1.next()) {

            PreparedStatement pstmt3 = con.prepareStatement("update `books list` set `Available Copies`=`Available Copies`+1 where `Book Name`=?");
            pstmt3.setString(1, bname);
            pstmt3.executeUpdate();

            PreparedStatement pstmt1 = con.prepareStatement("delete  from `Teacher` where `Name`=? and `Book Name`=?");
            pstmt1.setString(1, tname);
            pstmt1.setString(2, bname);
            pstmt1.executeUpdate();

            System.out.println("Book has returned successfully");
        }
        else
            System.out.println("Sorry No Record Found !");
        System.out.print("press 1 to Go back : ");
        if(sc.nextInt()==1)
            teacher();
    }

    void teacher()  {

            System.out.println("                         TEACHER  SECTION");
            System.out.println("1. Add Book\n2. View BookList\n3. IssueBook\n4. ReturnBook\n5. Go Back To Main Menu");
            System.out.print("Enter your choice: ");
            Scanner sc=new Scanner(System.in);
            try
            {
                int choice=sc.nextInt();
                if(choice==1)
                    addBook();
                else if(choice==2)
                    viewBookList();
                else if(choice==3)
                    tissueBook();
                else if(choice==4)
                    treturnBook();
                else if(choice==5)
                    start();
                else
                    System.out.println("Invalid Choice!");
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
    }

    void reIssue() throws SQLException {
        System.out.print("Enter your Roll no:");
        Scanner sc=new Scanner(System.in);
        int roll=sc.nextInt();
        sc.nextLine(); //to read \n.
        System.out.print("Enter Course:");
        String course=sc.nextLine();
        System.out.print("Enter the Book Name which you want to reissue: ");
        String bname=sc.nextLine();
        PreparedStatement pstmt= con.prepareStatement("select `Roll no` from Student where `Roll no`=? and `Book Name`=? and Course=?");
        pstmt.setInt(1,roll);
        pstmt.setString(2,bname);
        pstmt.setString(3,course);
        ResultSet rs=pstmt.executeQuery();
        if(rs.next())
        {
            System.out.print("Enter date of reissue(yyyy-mm-dd): ");
            String issuedate= sc.nextLine();
            PreparedStatement pstmt1= con.prepareStatement("update Student set `Issued Date`=`? where `Roll no`=? and `Book Name`=?");
            pstmt1.setString(1,issuedate);
            pstmt1.setInt(2,roll);
            pstmt1.setString(3,bname);
            pstmt1.executeQuery();
            System.out.println("Book is reissued successfully");
        }
        else
            System.out.println("Sorry! No Record Found");
        System.out.print("Enter 1 to go back: ");
        if(sc.nextInt()==1)
            student();
    }

    void start() throws SQLException
    {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        System.out.println("                                   Welcome To DDUC Library !");
        System.out.println("1. Login as a Teacher\n2. Login as a Student\n3. Close the Library" );
        System.out.print("Enter your Choice: ");
        Scanner sc=new Scanner(System.in);
        int choice=sc.nextInt();
        sc.nextLine(); //to read \n.
        if(choice ==1)
        {
            System.out.println("please verify yourself");
            System.out.print("Enter Username: ");
            String str1=sc.nextLine();
            System.out.print("Enter Password: ");
            String str2=sc.nextLine();
            if(str1.equals("admin") && str2.equals("admin"))
               teacher();
            else
            {
                System.out.println("Invalid Username or Password !");
                System.out.print("press 1 to Go back : ");
                if(sc.nextInt()==1)
                    start();
            }
        }
        else if(choice==2)
        {
            System.out.println("please verify yourself");
            System.out.print("Enter College EmailId: ");
            String str1=sc.nextLine();
            PreparedStatement pstmt= con.prepareStatement("select `Email ID` from `college student list` where `Email ID`=?");
            pstmt.setString(1,str1);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next())
              student();
            else
            {
                System.out.println("Sorry ! No record Found !");
                System.out.print("Enter 1 to go back: ");
                if(sc.nextInt()==1)
                    start();
            }
        }
        else if (choice==3)
               return;
        else
            System.out.println("Invalid Choice");
    }

    void student() throws SQLException {
        System.out.println("\033[H\033[2J");
        System.out.flush();
        Scanner sc=new Scanner(System.in);
            System.out.println("                         STUDENT  SECTION");
            System.out.println("1. IssueBook\n2. ReturnBook\n3. ReIssueBook\n4. Go Back To Main Menu");
            System.out.print("Enter your choice: ");

                int choice=sc.nextInt();
                if(choice==1)
                    sissueBook();
                else if(choice==2)
                    sreturnBook();
                else if(choice==3)
                    reIssue();
                else if(choice==4)
                     start();
                else
                    System.out.println("Invalid Choice!");
    }
}

public class LibDemo {
    public static void main(String []args) {
        MyLibrary obj=new MyLibrary();
        try {
              obj.start();
//            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
//            Statement stmt = con.createStatement();
//            stmt.executeUpdate("insert into `college student list` values('sanjana.55@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('simran.23@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('swati.52@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('rohan.16@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('seema.27@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('harshit.11@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('priya.17@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('shweta.58@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('rahul.01@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('deepak.08@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('rita.13@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('tanya.15@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('manvi.32@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('kartik.30@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('kriti.06@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('pihu.29@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('shriti.25@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('divya.64@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('diksha.10@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('sahil.14@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('isha.22@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('neetu.45@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('manak.47@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('nitin.48@ddu.du.ac.in')");
//            stmt.executeUpdate("insert into `college student list` values('harsh.51@ddu.du.ac.in')");
        }
       catch (SQLException e)
       {
           e.printStackTrace();
       }

//        stmt.executeUpdate("insert into `books list` values(1001,'Programming in Java','Herbert Schildt',10 )");//use BooksList or accent grave character`
//        stmt.executeUpdate("insert into `books list` values(1002,'Programming in C++','Robert Hoffman',19)");
//        stmt.executeUpdate("insert into `books list` values(1003,'Programming in PHP','Kevin Tatroe',8)");
//        stmt.executeUpdate("insert into `books list` values(1004,'Discrete Structure','Kenneth H. Rosen',5)");
//        stmt.executeUpdate("insert into `books list` values(1005,'Operating System','Abraham Silberschatz',2)");
//        stmt.executeUpdate("insert into `books list` values(1006,'Networking','Behrouz Forouzan',10)");
//        stmt.executeUpdate("insert into `books list` values(1007,'Data Structure','Robert Lafore',18)");
//        stmt.executeUpdate("insert into `books list` values(1008,'Database Management','Rajiv Chopra',6)");
//        stmt.executeUpdate("insert into `books list` values(1009,'Linear Algebra','JK Thukral',15)");
//        stmt.executeUpdate("insert into `books list` values(1010,'Calculus','Strauss',21)");
//        stmt.executeUpdate("insert into `books list` values(1011,'Linear Programming','Robert',7)");
//        stmt.executeUpdate("insert into `books list` values(1012,'Differential Equations','Shepley L Ross',20)");
//        stmt.executeUpdate("insert into `books list` values(1013,'Android Programming','John Horton',5)");
//        stmt.executeUpdate("insert into `books list` values(1014,'Programming in Python','Mark Lutz',30)");
//        stmt.executeUpdate("insert into `books list` values(1015,'Web Development','Robin Nixon',16)");
//        stmt.executeUpdate("insert into `books list` values(1016,'Machine Learning','Drew Conway',6)");
//        stmt.executeUpdate("insert into `books list` values(1017,'Programming in C','Herbert Schildt',10)");
//        stmt.executeUpdate("insert into `books list` values(1018,'JavaScript','Robin Nixon',9)");
//        stmt.executeUpdate("insert into `books list` values(1019,'Advanced Java','Herbert Schildt',11)");
//        stmt.executeUpdate("insert into `books list` values(1020,'Computer Architecture','Behrouz Forouzan',13)");

    }
}
