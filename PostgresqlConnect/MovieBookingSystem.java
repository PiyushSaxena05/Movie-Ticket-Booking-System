import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

interface MovieIt{

    public void info(Connection con , Scanner sc);
    public void selectMovie(Connection con , Scanner sc) throws SQLException;
    public void exit() throws InterruptedException;
}
class Movie implements MovieIt {
    String query1 =("INSERT INTO info(Name,Mob_No,Gmail)VALUES(?,?,?)");
    String query2 = ("INSERT INTO movieselection(Movie_name,Movie_date,Payment,Seat_Selected)VALUES(?,?,?,?)");

    @Override
    public void info(Connection con , Scanner sc) {
        System.out.println("Please enter your name: ");
        String name = sc.next();
        System.out.println("Please enter your mobile number: ");
        Long mobile_no = sc.nextLong();
        System.out.print("Please enter your gmail ID: ");
        sc.nextLine();
        String gmail = sc.nextLine();
        System.out.println();
        System.out.println("Your name : "+ name);
        System.out.println("Your mobile number : "+mobile_no);
        System.out.println("Your gmail-id: "+gmail);
        try{
            try(PreparedStatement p = con.prepareStatement(query1)){
                p.setString(1,name);
                if (mobile_no >= 1_000_000_000L && mobile_no <= 9_999_999_999L) {
                    p.setLong(2,mobile_no);
                } else {
                    System.out.println("Please enter valid number");
                }
                p.setString(3,gmail);
                int rowsaffect = p.executeUpdate();
                if(rowsaffect>0){
                    System.out.println("ALL SET");
                }else{
                    System.out.println("Something is wrong");
                }

            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void selectMovie(Connection con , Scanner sc) throws SQLException {
        System.out.print("Please enter the movie name: ");

        String movieName = sc.nextLine();

        System.out.print("Please enter the date : ");
        int date = sc.nextInt();

        System.out.println("Please select the timings: ");
        Random r =  new Random();
        int hour = r.nextInt(14)+10;
        int minutes = r.nextInt(60);
        LocalTime lt = LocalTime.of(hour,minutes);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
        System.out.println("Timings available are: ");
        System.out.println("you can book movie at : "+lt.format(f));
        int payment=0;
        String seatselected=" ";
        con.setAutoCommit(false);
        while(true){
            System.out.println("if you are satisfied with the timings press Y");
            String enter = sc.next();
            if(enter.equalsIgnoreCase("y")){
                System.out.println("Please proceed with the payment: ");
                System.out.println("Pay 250 for Silver / Economy");
                System.out.println("Pay 350 for Gold / Regular");
                System.out.println("Pay 420 for Platinum / Executive");
                System.out.println("Pay 500 for Recliner / Lounger");
                System.out.println("Pay 800 for Sofa / Couples Seat");
                System.out.println("Pay 1200 for VIP / Director’s Cut");
                payment = sc.nextInt();
                switch (payment) {
                    case 250 -> {
                        System.out.println("Payment succesfull");
                        seatselected = "Silver/Economy";

                    }
                    case 350 -> {
                        System.out.println("Payment succesfull");
                        seatselected = "Gold / Regular";

                    }
                    case 420 -> {
                        System.out.println("Payment succesfull");
                        seatselected = "Platinum / Executive";

                    }
                    case 500 -> {
                        System.out.println("Payment succesfull");
                        seatselected = "Recliner / Lounger";


                    }
                    case 800 -> {
                        System.out.println("Payment succesfull");
                        seatselected= "Sofa / Couples Seat";

                    }
                    case 1200 -> {
                        System.out.println("Payment succesfull");
                        seatselected= " VIP / Director’s Cut";

                    }
                    default -> {
                        System.out.println("Please enter valid amount");
                        System.out.println("Transaction cancelled");
                        con.rollback();
                        return;
                    }
                }
                System.out.println("Your movie show is booked at: "+lt.format(f)+" on "+ date);
                System.out.println("Enjoy your show ");
                try(PreparedStatement p = con.prepareStatement(query2)){
                    p.setString(1,movieName);
                    p.setInt(2,date);
                    p.setInt(3,payment);
                    p.setString(4,seatselected);
                    int rowsaffect = p.executeUpdate();
                    if(rowsaffect>0){
                        System.out.println("ALL SET");
                        con.commit();
                    }else{
                        System.out.println("Something is wrong");
                    }
                    break;

                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }else{
                hour = r.nextInt(14)+10;
                minutes= r.nextInt(60);
                lt = LocalTime.of(hour,minutes);
                f = DateTimeFormatter.ofPattern("hh:mm a");
                System.out.println("New timing: " + lt.format(f));
            }

        }


    }



    @Override
    public void exit() throws InterruptedException {
        System.out.print("Exiting the booking system ");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou ");

    }
}

public class MovieBookingSystem extends Movie {
    private static final String url = "jdbc:postgresql://localhost:5432/movie";
    private static final String user = "postgres";
    private static final String password = "PIYUSH@111WORD016";
    public static void main(String[] args)throws ClassNotFoundException,SQLException {
        try{
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,user,password);
            while(true) {
                System.out.println();
                Scanner sc = new Scanner(System.in);
                System.out.println("Welcome to our MOVIE TICKET BOOKING SYSTEM");
                System.out.println("Please choose option 1 to save your info: ");
                Movie obj = new Movie();
                int option = sc.nextInt();
                if(option==1){
                    obj.info(con,sc);
                    System.out.println("Now please enter option 2 to select movie: ");
                    int option2 = sc.nextInt();
                    if(option2==2){
                        sc.nextLine();
                        obj.selectMovie(con,sc);
                    }else{
                        obj.exit();
                    }
                }else {
                    obj.exit();
                }

            }



        }catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

